from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
from django.core import serializers
from django.core.mail import send_mail
from django.http import JsonResponse, HttpResponseBadRequest

try:
    from Backend.settings import DEBUG, EMAIL_HOST_USER, EMAIL_HOST_PASSWORD
except ImportError as e:
    from test.settings import DEBUG, EMAIL_HOST_USER, EMAIL_HOST_PASSWORD

from DenariiMobile.interface import wallet
from DenariiMobile.models import DenariiUser, DenariiAsk

if DEBUG:
    import DenariiMobile.testing.testing_denarii_client as denarii_client
else:
    import denarii_client

client = denarii_client.DenariiClient()


def get_user_with_username_and_email(username, email):
    try:
        existing = DenariiUser.objects.get(username=username, email=email)
        return existing
    except DenariiUser.DoesNotExist:
        return None


def get_user_with_username_or_email(username_or_email):
    existing = get_user_with_username(username_or_email)
    if existing is None:
        existing = get_user_with_email(username_or_email)
        return existing
    else:
        return existing


def get_user_with_username(username):
    try:
        existing = DenariiUser.objects.get(username=username)
        return existing
    except DenariiUser.DoesNotExist:
        return None


def get_user_with_email(email):
    try:
        existing = DenariiUser.objects.get(email=email)
        return existing
    except DenariiUser.DoesNotExist:
        return None


def get_user(username, email, password):
    try:
        existing = authenticate(username=username, email=email, password=password)
        return existing
    except DenariiUser.DoesNotExist:
        return None


def get_user_with_id(user_id):
    try:
        existing = DenariiUser.objects.get(id=user_id)
        return existing
    except DenariiUser.DoesNotExist:
        return None


def get_ask_with_id(ask_id):
    try:
        existing = DenariiAsk.objects.get(ask_id=ask_id)
        return existing
    except DenariiAsk.DoesNotExist:
        return None


def get_wallet(user):
    # Only get the first wallet for now. In theory there could be many, but
    # we want to enforce one username + email per wallet.
    return user.walletdetails_set.all()[0]


def get_user_asks(user):
    return user.denariiask_set.all()


def try_to_buy_denarii(ordered_asks, to_buy_amount, bid_price, buy_regardless_of_price, fail_if_full_amount_isnt_met):
    current_bought_amount = 0
    current_ask_price = bid_price
    asks_met = []
    for ask in ordered_asks:

        current_ask_price = ask.asking_price

        if buy_regardless_of_price == "False" and current_ask_price > bid_price:

            if fail_if_full_amount_isnt_met == "True":
                for reprocessed_ask in ordered_asks:
                    reprocessed_ask.in_escrow = False
                    reprocessed_ask.amount_bought = 0
                    reprocessed_ask.save()

            return False, "Asking price was higher than bid price", asks_met

        current_bought_amount += ask.amount

        ask.in_escrow = True

        if current_bought_amount > to_buy_amount:
            ask.amount_bought = ask.amount - (current_bought_amount - to_buy_amount)
        else:
            ask.amount_bought = ask.amount

        ask.save()

        asks_met.append(ask)

        if current_bought_amount > to_buy_amount:
            return True, None, asks_met

    return False, "Reached end of asks so not enough was bought", asks_met


# In spite of its name this function handles login and registration
def get_user_id(request, username, email, password):
    existing = get_user(username, email, password)
    if existing is not None:

        existing_wallet = get_wallet(existing)
        serialized_wallet = serializers.serialize('json', [existing_wallet], fields='user_identifier')
        return JsonResponse({'wallet': serialized_wallet})
    else:

        # We first need to check no user has this email or username.
        if get_user_with_username(username) is not None or get_user_with_email(email) is not None:
            return HttpResponseBadRequest("User already exists")

        new_user = DenariiUser.objects.create_user(username=username, email=email, password=password)
        new_user.save()

        new_wallet_details = new_user.walletdetails_set.create(user_identifier=str(new_user.id))
        new_wallet_details.save()

        serialized_wallet = serializers.serialize('json', [new_wallet_details], fields='user_identifier')
        return JsonResponse({'wallet': serialized_wallet})


def request_reset(request, username_or_email):
    user = get_user_with_username_or_email(username_or_email)

    if user is not None:
        random_number = DenariiUser.objects.make_random_password(length=6, allowed_chars='123456789')

        # Send the user an email.
        send_mail("Password reset id", f"Your password reset id is {random_number}", f"{EMAIL_HOST_USER}@gmail.com",
                  [user.email], auth_user=EMAIL_HOST_USER, auth_password=EMAIL_HOST_PASSWORD)

        user.reset_id = random_number
        user.save()

        existing_wallet = get_wallet(user)

        # We send no data back. Just a successful response.
        serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                  fields=())

        return JsonResponse({'wallet': serialized_wallet})

    else:
        return HttpResponseBadRequest("No user with that username or email")


def verify_reset(request, username_or_email, reset_id):
    user = get_user_with_username_or_email(username_or_email)

    if user is not None:
        if reset_id == user.reset_id and reset_id != 0:

            user.reset_id = 0
            user.save()

            existing_wallet = get_wallet(user)

            # We send no data back. Just a successful response.
            serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                      fields=())

            return JsonResponse({'wallet': serialized_wallet})
        else:
            return HttpResponseBadRequest("That reset id does not match")
    else:
        return HttpResponseBadRequest("No user with that username or email")


def reset_password(request, username, email, password):
    user = get_user_with_username_and_email(username, email)

    if user is not None:
        user.password = password
        user.save()

        existing_wallet = get_wallet(user)

        # We send no data back. Just a successful response.
        serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                  fields=())

        return JsonResponse({'wallet': serialized_wallet})
    else:
        return HttpResponseBadRequest("No user with that username and email")


@login_required
def create_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        wallet_interface = wallet.Wallet(wallet_name, password)
        res = client.create_wallet(wallet_interface)
        if res is True:
            existing_wallet.wallet_name = wallet_name
            existing_wallet.wallet_password = password

            res = client.query_seed(wallet_interface)
            if res is True:
                existing_wallet.seed = wallet_interface.phrase

                res = client.get_address(wallet_interface)

                if res is True:

                    existing_wallet.wallet_address = wallet_interface.address

                    existing_wallet.save()

                    serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                              fields=('seed', 'wallet_address'))

                    return JsonResponse({'wallet': serialized_wallet})
                else:
                    return HttpResponseBadRequest("Could not get wallet address")
            else:
                return HttpResponseBadRequest("Could not get wallet seed")
        else:
            return HttpResponseBadRequest("Cannot create wallet")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def restore_wallet(request, user_id, wallet_name, password, seed):
    existing = get_user_with_id(user_id)
    if existing is not None:

        existing_wallet = get_wallet(existing)

        wallet_interface = wallet.Wallet(wallet_name, password, seed)
        res = client.restore_wallet(wallet_interface)

        if res is True:

            existing_wallet.wallet_name = wallet_interface.name
            existing_wallet.wallet_password = wallet_interface.password
            existing_wallet.seed = wallet_interface.phrase

            res = client.get_address(wallet_interface)

            if res is True:

                existing_wallet.wallet_address = wallet_interface.address

                existing_wallet.save()

                serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                          fields='wallet_address')

                return JsonResponse({'wallet': serialized_wallet})
            else:
                return HttpResponseBadRequest("Could not get wallet address")
        else:
            return HttpResponseBadRequest("Could not restore wallet")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def open_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        wallet_interface = wallet.Wallet(wallet_name, password)

        res = client.set_current_wallet(wallet_interface)

        if res is True:
            existing_wallet.wallet_name = wallet_interface.name
            existing_wallet.wallet_address = wallet_interface.password

            res = client.query_seed(wallet_interface)
            if res is True:
                existing_wallet.seed = wallet_interface.phrase

                res = client.get_address(wallet_interface)

                if res is True:

                    existing_wallet.wallet_address = wallet_interface.address

                    existing_wallet.save()

                    serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                              fields=('seed', 'wallet_address'))

                    return JsonResponse({'wallet': serialized_wallet})
                else:
                    return HttpResponseBadRequest("Could not get wallet address")
            else:
                return HttpResponseBadRequest("Could not get wallet seed")
        else:
            return HttpResponseBadRequest("Could not open wallet")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_balance(request, user_id, wallet_name):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        wallet_interface = wallet.Wallet(wallet_name, existing_wallet.wallet_password)

        wallet_interface.balance = client.get_balance_of_wallet(wallet_interface)
        existing_wallet.balance = wallet_interface.balance

        existing_wallet.save()

        serialized_wallet = serializers.serialize('json', [existing_wallet],
                                                  fields='balance')

        return JsonResponse({'wallet': serialized_wallet})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def send_denarii(request, user_id, wallet_name, address, amount):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        sender = wallet.Wallet(wallet_name, existing_wallet.wallet_password)
        # We only need the receiver's address not their wallet name and password
        receiver = wallet.Wallet("", "")
        receiver.address = address

        amount_sent = client.transfer_money(float(amount), sender, receiver)

        existing_wallet.balance = client.get_balance_of_wallet(sender)

        existing_wallet.save()

        # We explicitly send no fields when sending denarii because the client should know how much was sent
        # based on whether the call was successful or not.
        serialized_wallet = serializers.serialize('json', [existing_wallet], fields=())

        if amount_sent is True:
            return JsonResponse({'wallet': serialized_wallet})
        else:
            return HttpResponseBadRequest("Could not send denarii")

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_prices(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        filtered_by_escrow = DenariiAsk.objects.filter(in_escrow=False)
        filtered_by_user_id = filtered_by_escrow.exclude(user_identifier=user_id)
        total_asks = filtered_by_user_id.count()
        lowest_priced_asks = None
        if total_asks < 100:
            lowest_priced_asks = filtered_by_user_id.order_by("asking_price")[:]
        else:
            lowest_priced_asks = filtered_by_user_id.order_by("asking_price")[:100]

        serialized_denarii_asks = serializers.serialize('json', lowest_priced_asks,
                                                        fields=('ask_id', 'asking_price', 'amount'))

        return JsonResponse({'denarii_asks': serialized_denarii_asks})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def buy_denarii(request, user_id, amount, bid_price, buy_regardless_of_price, fail_if_full_amount_isnt_met):
    existing = get_user_with_id(user_id)

    if existing is not None:
        filtered_asks = DenariiAsk.objects.filter(in_escrow=False)
        ordered_asks = filtered_asks.order_by('asking_price')

        complete_purchase, error_message, asks_met = try_to_buy_denarii(ordered_asks, amount, bid_price,
                                                                        buy_regardless_of_price,
                                                                        fail_if_full_amount_isnt_met)
        if error_message is None:
            serialized_asks_met = serializers.serialize('json', asks_met, fields='ask_id')
            return JsonResponse({'denarii_asks': serialized_asks_met})
        elif len(asks_met) == 0:
            return HttpResponseBadRequest("No asks could be met with the bid price")
        else:
            if fail_if_full_amount_isnt_met == "True":
                return HttpResponseBadRequest("Could not buy the asked amount")
            else:
                serialized_asks_met = serializers.serialize('json', asks_met, fields='ask_id')
                return JsonResponse({'denarii_asks': serialized_asks_met})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def transfer_denarii(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)

        if ask is not None:

            if ask.in_escrow is True:
                asking_user = ask.denarii_user

                sender_user_wallet = get_wallet(asking_user)

                receiver_wallet = get_wallet(existing)

                sender = wallet.Wallet(sender_user_wallet.wallet_name, sender_user_wallet.wallet_password)
                receiver = wallet.Wallet(receiver_wallet.wallet_name, receiver_wallet.wallet_password)
                receiver.address = receiver_wallet.wallet_address

                amount_sent = client.transfer_money(float(ask.amount_bought), sender, receiver)

                sender_user_wallet.balance = client.get_balance_of_wallet(sender)

                sender_user_wallet.save()

                receiver_wallet.balance = client.get_balance_of_wallet(receiver)

                receiver_wallet.save()

                ask.in_escrow = False
                ask.amount = ask.amount - ask.amount_bought
                ask.amount_bought = 0

                serialized_ask = serializers.serialize('json', [ask], fields=('ask_id', 'amount_bought'))

                if ask.amount == 0:
                    ask.delete()
                else:
                    ask.save()

                if amount_sent is True:
                    return JsonResponse({'denarii_asks': serialized_ask})
                else:
                    return HttpResponseBadRequest("Could not transfer denarii")

            else:
                return HttpResponseBadRequest("Ask was not bought")

        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def make_denarii_ask(request, user_id, amount, asking_price):
    existing = get_user_with_id(user_id)

    if existing is not None:
        new_ask = existing.denariiask_set.create(user_identifier=existing.id)
        new_ask.in_escrow = False
        new_ask.amount = amount
        new_ask.asking_price = asking_price
        new_ask.ask_id = new_ask.primary_key
        new_ask.save()

        serialized_ask = serializers.serialize('json', [new_ask], fields=('ask_id', 'amount', 'asking_price'))
        return JsonResponse({'denarii_asks': serialized_ask})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def poll_for_completed_transaction(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        current_asks = existing.denariiask_set.all()

        serialized_asks = serializers.serialize('json', current_asks, fields=('ask_id', 'amount', 'asking_price'))
        return JsonResponse({'denarii_asks': serialized_asks})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def cancel_ask(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)
        if ask is not None:
            ask.delete()

            serialized_asks = serializers.serialize('json', [ask], fields='ask_id')
            return JsonResponse({'denarii_asks': serialized_asks})
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")
