import json

from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
from django.core import serializers
from django.core.mail import send_mail
from django.http import JsonResponse, HttpResponseBadRequest

try:
    from Backend.settings import DEBUG, EMAIL_HOST_USER, EMAIL_HOST_PASSWORD, API_KEY, CHECKR_API_KEY
except ImportError as e:
    from test.settings import DEBUG, EMAIL_HOST_USER, EMAIL_HOST_PASSWORD

from DenariiMobile.interface import wallet, work_location
from DenariiMobile.models import DenariiUser, DenariiAsk, Response, CreditCard, SupportTicket, SupportTicketComment

if DEBUG:
    import DenariiMobile.testing.testing_denarii_client as denarii_client
    import DenariiMobile.testing.testing_stripe as stripe
    import DenariiMobile.testing.testing_checkr as checkr

    stripe.default_http_client = stripe.StripeTestingClient()
else:
    import denarii_client
    import stripe
    import checkr

    client = stripe.http_client.RequestsClient()
    stripe.default_http_client = client
    stripe.api_key = API_KEY

client = denarii_client.DenariiClient()

checkr_client = checkr.CheckrClient(CHECKR_API_KEY)


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


def get_support_ticket_with_id(support_ticket_id):
    try:
        existing = SupportTicket.objects.get(support_id=support_ticket_id)
        return existing
    except SupportTicket.DoesNotExist:
        return None


def get_wallet(user):
    # Only get the first wallet for now. In theory there could be many, but
    # we want to enforce one username + email per wallet.
    try:
        return user.walletdetails_set.all()[0]
    except Exception as get_wallet_error:
        print(get_wallet_error)
        return None


def get_user_asks(user):
    return user.denariiask_set.all()


def get_credit_card(user):
    # Only get the first credit card for now. In theory there could be many, but we want to enforce one username + email
    # per credit card.
    try:
        return user.creditcard_set.all()[0]
    except Exception as get_credit_card_error:
        print(get_credit_card_error)
        return None


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


def update_user_verification_status(user):
    if user.report_id is not None:
        success, res = checkr_client.get_report(user.report_id)
        if success and res["status"] == "complete":
            user.identity_is_verified = res["result"] == "clear"
            user.verification_report_status = "complete"

        user.save()


# In spite of its name this function handles login and registration
# TODO: split up
def get_user_id(request, username, email, password):
    existing = get_user(username, email, password)
    if existing is not None:
        response = Response.objects.create(user_identifier=str(existing.id))

        serialized_response_list = serializers.serialize('json', [response], fields='user_identifier')
        return JsonResponse({'response_list': serialized_response_list})
    else:

        # We first need to check no user has this email or username.
        if get_user_with_username(username) is not None or get_user_with_email(email) is not None:
            return HttpResponseBadRequest("User already exists")

        new_user = DenariiUser.objects.create_user(username=username, email=email, password=password)
        new_user.save()

        new_wallet_details = new_user.walletdetails_set.create()
        new_wallet_details.save()

        response = Response.objects.create(user_identifier=str(new_user.id))

        serialized_response_list = serializers.serialize('json', [response], fields='user_identifier')
        return JsonResponse({'response_list': serialized_response_list})


def request_reset(request, username_or_email):
    user = get_user_with_username_or_email(username_or_email)

    if user is not None:
        random_number = DenariiUser.objects.make_random_password(length=6, allowed_chars='123456789')

        # Send the user an email.
        send_mail("Password reset id", f"Your password reset id is {random_number}", f"{EMAIL_HOST_USER}@gmail.com",
                  [user.email], auth_user=EMAIL_HOST_USER, auth_password=EMAIL_HOST_PASSWORD)

        user.reset_id = random_number
        user.save()

        response = Response.objects.create()

        # We send no data back. Just a successful response.
        serialized_response_list = serializers.serialize('json', [response],
                                                         fields=())

        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with that username or email")


def verify_reset(request, username_or_email, reset_id):
    user = get_user_with_username_or_email(username_or_email)

    if user is not None:
        if reset_id == user.reset_id and reset_id != 0:

            user.reset_id = 0
            user.save()

            response = Response.objects.create()

            # We send no data back. Just a successful response.
            serialized_response_list = serializers.serialize('json', [response],
                                                             fields=())

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("That reset id does not match")
    else:
        return HttpResponseBadRequest("No user with that username or email")


def reset_password(request, username, email, password):
    user = get_user_with_username_and_email(username, email)

    if user is not None:
        user.password = password
        user.save()

        response = Response.objects.create()

        # We send no data back. Just a successful response.
        serialized_response_list = serializers.serialize('json', [response],
                                                         fields=())

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with that username and email")


@login_required
def create_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        if existing_wallet is None:
            return HttpResponseBadRequest("No wallet for user")

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

                    response = Response.objects.create(seed=existing_wallet.seed,
                                                       wallet_address=existing_wallet.wallet_address)

                    serialized_response_list = serializers.serialize('json', [response],
                                                                     fields=('seed', 'wallet_address'))

                    return JsonResponse({'response_list': serialized_response_list})
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

        if existing_wallet is None:
            return HttpResponseBadRequest("No wallet for user")

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

                response = Response.objects.create(wallet_address=existing_wallet.wallet_address)

                serialized_response_list = serializers.serialize('json', [response],
                                                                 fields='wallet_address')

                return JsonResponse({'response_list': serialized_response_list})
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

        if existing_wallet is None:
            return HttpResponseBadRequest("No wallet for user")

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

                    response = Response.objects.create(seed=existing_wallet.seed,
                                                       wallet_address=existing_wallet.wallet_address)

                    serialized_response_list = serializers.serialize('json', [response],
                                                                     fields=('seed', 'wallet_address'))

                    return JsonResponse({'response_list': serialized_response_list})
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

        if existing_wallet is None:
            return HttpResponseBadRequest("No wallet for user")

        wallet_interface = wallet.Wallet(wallet_name, existing_wallet.wallet_password)

        wallet_interface.balance = client.get_balance_of_wallet(wallet_interface)
        existing_wallet.balance = wallet_interface.balance

        existing_wallet.save()

        response = Response.objects.create(balance=existing_wallet.balance)

        serialized_response_list = serializers.serialize('json', [response],
                                                         fields='balance')

        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def send_denarii(request, user_id, wallet_name, address, amount):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        if existing_wallet is None:
            return HttpResponseBadRequest("No wallet for user")

        sender = wallet.Wallet(wallet_name, existing_wallet.wallet_password)
        # We only need the receiver's address not their wallet name and password
        receiver = wallet.Wallet("", "")
        receiver.address = address

        amount_sent = client.transfer_money(float(amount), sender, receiver)

        existing_wallet.balance = client.get_balance_of_wallet(sender)

        existing_wallet.save()

        # We explicitly send no fields when sending denarii because the client should know how much was sent
        # based on whether the call was successful or not.
        response = Response.objects.create()

        serialized_response_list = serializers.serialize('json', [response], fields=())

        if amount_sent is True:
            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("Could not send denarii")

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_prices(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        filtered_by_escrow = DenariiAsk.objects.filter(in_escrow=False)
        filtered_by_user_id = filtered_by_escrow.exclude(denarii_user=existing)
        total_asks = filtered_by_user_id.count()
        lowest_priced_asks = None
        if total_asks < 100:
            lowest_priced_asks = filtered_by_user_id.order_by("asking_price")[:]
        else:
            lowest_priced_asks = filtered_by_user_id.order_by("asking_price")[:100]

        responses = []
        for ask in lowest_priced_asks:
            response = Response.objects.create(ask_id=ask.ask_id, asking_price=ask.asking_price, amount=ask.amount)
            responses.append(response)

        serialized_response_list = serializers.serialize('json', responses,
                                                         fields=('ask_id', 'asking_price', 'amount'))

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def buy_denarii(request, user_id, amount, bid_price, buy_regardless_of_price, fail_if_full_amount_isnt_met):
    existing = get_user_with_id(user_id)

    if existing is not None:
        filtered_asks = DenariiAsk.objects.filter(in_escrow=False)
        ordered_asks = filtered_asks.order_by('asking_price')

        _, error_message, asks_met = try_to_buy_denarii(ordered_asks, amount, bid_price,
                                                        buy_regardless_of_price,
                                                        fail_if_full_amount_isnt_met)

        responses = []
        for ask in asks_met:
            response = Response.objects.create(ask_id=ask.ask_id)
            responses.append(response)

        if error_message is None:
            serialized_response_list = serializers.serialize('json', responses, fields='ask_id')
            return JsonResponse({'response_list': serialized_response_list})
        elif len(asks_met) == 0:
            return HttpResponseBadRequest("No asks could be met with the bid price")
        else:
            if fail_if_full_amount_isnt_met == "True":
                return HttpResponseBadRequest("Could not buy the asked amount")
            else:
                serialized_response_list = serializers.serialize('json', responses, fields='ask_id')
                return JsonResponse({'response_list': serialized_response_list})

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

                if sender_user_wallet is None:
                    return HttpResponseBadRequest("No wallet for sending user")

                receiver_wallet = get_wallet(existing)

                if receiver_wallet is None:
                    return HttpResponseBadRequest("No wallet for receiving user")

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

                response = Response.objects.create(ask_id=ask.ask_id, amount_bought=ask.amount_bought)

                serialized_response_list = serializers.serialize('json', [response], fields=('ask_id', 'amount_bought'))

                # An ask is always settled if the transfer worked. It will get unsettled later on if it still has money in it. 
                ask.is_settled = True
                ask.save()

                if amount_sent is True:
                    return JsonResponse({'response_list': serialized_response_list})
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
        new_ask = existing.denariiask_set.create()
        new_ask.in_escrow = False
        new_ask.amount = amount
        new_ask.asking_price = asking_price
        new_ask.ask_id = new_ask.primary_key
        new_ask.is_settled = False
        new_ask.has_been_seen_by_seller = False
        new_ask.save()

        response = Response.objects.create(ask_id=new_ask.ask_id, asking_price=new_ask.asking_price,
                                           amount=new_ask.amount)

        serialized_response_list = serializers.serialize('json', [response],
                                                         fields=('ask_id', 'amount', 'asking_price'))
        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def poll_for_completed_transaction(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        # We only want their transactions that are settled.
        settled_asks = existing.denariiask_set.filter(is_settled=True)
        responses = []

        for ask in settled_asks:
            ask.has_been_seen_by_seller = True
            response = Response.objects.create(ask_id=ask.ask_id, asking_price=ask.asking_price, amount=ask.amount)
            responses.append(response)
            ask.save()

        serialized_response_list = serializers.serialize('json', responses,
                                                         fields=('ask_id', 'amount', 'asking_price'))
        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def cancel_ask(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)
        if ask is not None:

            if ask.in_escrow or ask.is_settled:
                return HttpResponseBadRequest('Ask was already purchased')
            else:
                response = Response.objects.create(ask_id=ask.ask_id)

                ask.delete()

                serialized_response_list = serializers.serialize('json', [response], fields='ask_id')
                return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def has_credit_card_info(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:

        if len(existing.creditcard_set.all()) == 0:
            response = Response.objects.create(has_credit_card_info=False)
        else:
            response = Response.objects.create(has_credit_card_info=True)
        serialized_response_list = serializers.serialize('json', [response], fields='has_credit_card_info')

        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def set_credit_card_info(request, user_id, card_number, expiration_date_month, expiration_date_year, security_code):
    existing = get_user_with_id(user_id)

    if existing is not None:

        if len(existing.creditcard_set.all()) != 0:
            return HttpResponseBadRequest("User already has credit card info")

        new_credit_card = existing.creditcard_set.create()

        try:
            source_token = stripe.Token.create(
                card={
                    "number": card_number,
                    "exp_month": int(expiration_date_month),
                    "exp_year": int(expiration_date_year),
                    "cvc": security_code,
                },
            )
        except Exception as token_create_error:
            print(token_create_error)
            return HttpResponseBadRequest("Could not create customer card")

        try:
            # Credit a new customer since we delete them when we clear their credit card info.
            customer = stripe.Customer.create(
                description=f"Customer with id{user_id}",
                email=existing.email,
                name=existing.username,
                source=source_token['id']
            )
        except Exception as customer_create_error:
            print(customer_create_error)
            return HttpResponseBadRequest("Could not create customer")

        # Create their setup so we can charge them.
        try:
            stripe.SetupIntent.create(
                payment_method_types=["card"],
                customer=customer['id']
            )
        except Exception as setup_intent_create_error:
            print(setup_intent_create_error)
            return HttpResponseBadRequest("Could not create setup intent")

        new_credit_card.customer_id = customer['id']
        new_credit_card.source_token_id = source_token['id']
        new_credit_card.save()

        response = Response.objects.create()

        # Just send a successful response
        serialized_response_list = serializers.serialize('json', [response], fields=())
        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def clear_credit_card_info(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        existing_credit_card = get_credit_card(existing)

        if existing_credit_card is None:
            return HttpResponseBadRequest("No credit card for user")

        try:
            stripe.Customer.delete(existing_credit_card.customer_id)

        except Exception as customer_delete_error:
            print(customer_delete_error)
            return HttpResponseBadRequest("Could not delete customer")

        existing_credit_card.delete()

        response = Response.objects.create()

        # Just send a successful response
        serialized_response_list = serializers.serialize('json', [response], fields=())
        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_money_from_buyer(request, user_id, amount, currency):
    existing = get_user_with_id(user_id)

    if existing is not None:
        existing_credit_card = get_credit_card(existing)

        if existing_credit_card is None:
            return HttpResponseBadRequest("No credit card for user")

        try:
            payment_intent = stripe.PaymentIntent.create(
                amount=int(amount),
                currency=currency,
                automatic_payment_methods={"enabled": True},
                customer=existing_credit_card.customer_id,
                receipt_email=existing.email
            )
        except Exception as payment_intent_create_error:
            print(payment_intent_create_error)
            return HttpResponseBadRequest("Could not create payment intent")

        try:
            payment_intent_confirm = stripe.PaymentIntent.confirm(
                payment_intent['id'],
                payment_method=existing_credit_card.source_token_id,
            )

        except Exception as payment_intent_confirm_error:
            print(payment_intent_confirm_error)
            return HttpResponseBadRequest("Could not confirm payment intent")

        # TODO handle next actions
        if payment_intent_confirm['next_action'] is not None:
            try:
                stripe.PaymentIntent.cancel(
                    payment_intent['id'],
                    cancellation_reason="Failed to confirm payment"
                )
            except Exception as payment_intent_cancel_error:
                print(payment_intent_cancel_error)
                return HttpResponseBadRequest("Could not cancel payment intent")

            return HttpResponseBadRequest("Canceled payment intent because of an error confirming it")

        response = Response.objects.create()

        # Just send a successful response
        serialized_response_list = serializers.serialize('json', [response], fields=())
        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def send_money_to_seller(request, user_id, amount, currency):
    existing = get_user_with_id(user_id)

    if existing is not None:
        existing_credit_card = get_credit_card(existing)

        if existing_credit_card is None:
            return HttpResponseBadRequest("No credit card for user")

        try:
            stripe.Payout.create(
                amount=int(amount),
                currency=currency,
                destination=existing_credit_card.source_token_id
            )
        except Exception as payout_create_error:
            print(payout_create_error)
            return HttpResponseBadRequest("Could not create payout")

        response = Response.objects.create()

        # Just send a successful response
        serialized_response_list = serializers.serialize('json', [response], fields=())
        return JsonResponse({'response_list': serialized_response_list})

    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def is_transaction_settled(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)
        if ask is not None:

            if not ask.is_settled or not ask.has_been_seen_by_seller:
                response = Response.objects.create(ask_id=ask_id, transaction_was_settled=False)

                serialized_response_list = serializers.serialize('json', [response],
                                                                 fields=('ask_id', 'transaction_was_settled'))
                return JsonResponse({'response_list': serialized_response_list})
            else:
                response = Response.objects.create(ask_id=ask_id, transaction_was_settled=True)
                if ask.amount == 0:
                    ask.delete()
                else:
                    ask.is_settled = False
                    ask.has_been_seen_by_seller = False
                    ask.save()

                serialized_response_list = serializers.serialize('json', [response],
                                                                 fields=('ask_id', 'transaction_was_settled'))
                return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def delete_user(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        existing.delete()

        response = Response.objects.create()

        serialized_response_list = serializers.serialize('json', [response], fields=())

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_ask_with_identifier(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)

        if ask is not None:
            response = Response.objects.create(ask_id=ask.ask_id, amount=ask.amount, amount_bought=ask.amount_bought)

            serialized_response_list = serializers.serialize('json', [response],
                                                             fields=('ask_id', 'amount', 'amount_bought'))

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def transfer_denarii_back_to_seller(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)

        if ask is not None:
            if ask.in_escrow is True:
                asking_user = ask.denarii_user

                receiver_wallet = get_wallet(asking_user)

                if receiver_wallet is None:
                    return HttpResponseBadRequest("No wallet for receiving user")

                sender_user_wallet = get_wallet(existing)

                if sender_user_wallet is None:
                    return HttpResponseBadRequest("No wallet for sending user")

                sender = wallet.Wallet(sender_user_wallet.wallet_name, sender_user_wallet.wallet_password)
                receiver = wallet.Wallet(receiver_wallet.wallet_name, receiver_wallet.wallet_password)
                receiver.address = receiver_wallet.wallet_address

                amount_sent = client.transfer_money(float(ask.amount_bought), sender, receiver)

                if not amount_sent:
                    return HttpResponseBadRequest("Could not transfer denarii")

                sender_user_wallet.balance = client.get_balance_of_wallet(sender)

                sender_user_wallet.save()

                receiver_wallet.balance = client.get_balance_of_wallet(receiver)

                receiver_wallet.save()

                ask.in_escrow = False
                ask.amount_bought = 0

                response = Response.objects.create(ask_id=ask.ask_id)

                serialized_response_list = serializers.serialize('json', [response], fields='ask_id')

                ask.save()

                return JsonResponse({'response_list': serialized_response_list})
            else:
                return HttpResponseBadRequest("Ask was not bought")
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def send_money_back_to_buyer(request, user_id, amount, currency):
    existing = get_user_with_id(user_id)

    if existing is not None:
        existing_credit_card = get_credit_card(existing)

        if existing_credit_card is None:
            return HttpResponseBadRequest("No credit card for user")

        try:
            stripe.Payout.create(
                amount=int(amount),
                currency=currency,
                destination=existing_credit_card.source_token_id
            )
        except Exception as payout_create_error:
            print(payout_create_error)
            return HttpResponseBadRequest("Could not create payout")

        response = Response.objects.create()

        # Just send a successful response
        serialized_response_list = serializers.serialize('json', [response], fields=())
        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def cancel_buy_of_ask(request, user_id, ask_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        ask = get_ask_with_id(ask_id)

        if ask is not None:
            if ask.in_escrow:
                if not ask.is_settled:

                    ask.in_escrow = False
                    ask.amount_bought = 0

                    ask.save()

                    response = Response.objects.create()

                    # Just send a successful response
                    serialized_response_list = serializers.serialize('json', [response], fields=())
                    return JsonResponse({'response_list': serialized_response_list})
                else:
                    return HttpResponseBadRequest("Ask is settled it cannot be cancelled")
            else:
                return HttpResponseBadRequest("Ask is not in escrow nothing to do")
        else:
            return HttpResponseBadRequest("No ask with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def verify_identity(request, user_id, first_name, middle_name, last_name, email, dob, ssn, zipcode, phone,
                    work_locations):
    existing = get_user_with_id(user_id)

    if existing is not None:

        success, res = checkr_client.create_candidate(first_name, last_name, middle_name, email, dob, ssn, zipcode,
                                                      phone, work_locations)

        if success:

            success_two, res_two = checkr_client.create_invitation(res["id"], work_locations)

            if success_two:
                existing.report_id = res_two["report_id"]
                existing.verification_report_status = "pending"

                status = ""

                update_user_verification_status(existing)

                if existing.identity_is_verified:
                    status = "is_verified"
                    existing.verification_report_status = "complete"
                elif not existing.identity_is_verified and existing.verification_report_status == "complete":
                    status = "failed_verification"
                elif existing.verification_report_status == "pending":
                    status = "verification_pending"
                else:
                    status = "is_not_verified"

                response = Response.objects.create(verification_status=status)

                serialized_response_list = serializers.serialize('json', [response], fields="verification_status")

                existing.save()

                return JsonResponse({'response_list': serialized_response_list})
            else:
                return HttpResponseBadRequest("Could not create invitation")
        else:
            return HttpResponseBadRequest("Could not create candidate")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def is_a_verified_person(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:
        status = ""

        update_user_verification_status(existing)

        if existing.identity_is_verified:
            status = "is_verified"
            existing.verification_report_status = "complete"
        elif not existing.identity_is_verified and existing.verification_report_status == "complete":
            status = "failed_verification"
        elif existing.verification_report_status == "pending":
            status = "verification_pending"
        else:
            status = "is_not_verified"

        response = Response.objects.create(verification_status=status)

        serialized_response_list = serializers.serialize('json', [response], fields="verification_status")

        existing.save()

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_all_asks(request, user_id):
    existing = get_user_with_id(user_id)

    if existing is not None:

        ask_set = existing.denariiask_set.all().filter(in_escrow=False).filter(is_settled=False)

        response_list = []
        for ask in ask_set:
            response = Response.objects.create(ask_id=ask.ask_id)

            response_list.append(response)

        serialized_response_list = serializers.serialize('json', response_list,
                                                         fields='ask_id')

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def create_support_ticket(request, user_id, title, description):
    existing = get_user_with_id(user_id)

    if existing is not None:

        support_ticket = existing.supportticket_set.create(title=title, description=description)
        support_ticket.support_id = support_ticket.primary_key
        support_ticket.resolved = False

        response = Response.objects.create(support_ticket_id=support_ticket.support_id,
                                           creation_time_body=support_ticket.creation_time)

        serialized_response_list = serializers.serialize('json', [response],
                                                         fields=('support_ticket_id', 'creation_time_body'))
        support_ticket.save()

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def update_support_ticket(request, user_id, support_ticket_id, comment):
    existing = get_user_with_id(user_id)

    if existing is not None:

        support_ticket = get_support_ticket_with_id(support_ticket_id)

        if support_ticket is not None:

            comment = support_ticket.supportticketcomment_set.create(content=comment, author=existing.username)

            response = Response.objects.create(support_ticket_id=support_ticket.support_id,
                                               updated_time_body=comment.updated_time)

            serialized_response_list = serializers.serialize('json', [response],
                                                             fields=('support_ticket_id', 'updated_time_body'))
            support_ticket.save()
            comment.save()

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No support ticket with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def delete_support_ticket(request, user_id, support_ticket_id):
    existing = get_user_with_id(user_id)

    if existing is not None:

        support_ticket = get_support_ticket_with_id(support_ticket_id)

        if support_ticket is not None:

            response = Response.objects.create(support_ticket_id=support_ticket.support_id)

            serialized_response_list = serializers.serialize('json', [response],
                                                             fields='support_ticket_id')
            support_ticket.delete()

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No support ticket with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_support_tickets(request, user_id, can_be_resolved):
    existing = get_user_with_id(user_id)

    if existing is not None:

        response_list = []

        support_tickets = None
        if can_be_resolved == "True":
            support_tickets = existing.supportticket_set.all()
        else:
            support_tickets = existing.supportticket_set.all().filter(resolved=False)

        for ticket in support_tickets:
            response = Response.objects.create(support_ticket_id=ticket.support_id, author=existing.username,
                                               title=ticket.title, description=ticket.description,
                                               updated_time_body=ticket.updated_time,
                                               creation_time_body=ticket.creation_time,
                                               is_resolved=ticket.resolved)

            response_list.append(response)

        serialized_response_list = serializers.serialize('json', response_list,
                                                         fields=('support_ticket_id', 'author', 'title',
                                                                 'description', 'updated_time_body',
                                                                 'creation_time_body', 'resolved'))

        return JsonResponse({'response_list': serialized_response_list})
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def get_comments_on_ticket(request, user_id, support_ticket_id):
    existing = get_user_with_id(user_id)

    if existing is not None:

        support_ticket = get_support_ticket_with_id(support_ticket_id)

        if support_ticket is not None:

            response_list = []

            for comment in support_ticket.supportticketcomment_set.all():
                response = Response.objects.create(author=comment.author, content=comment.content,
                                                   creation_time_body=comment.creation_time,
                                                   updated_time_body=comment.updated_time)

                response_list.append(response)

            serialized_response_list = serializers.serialize('json', response_list,
                                                             fields=('author', 'content', 'updated_time_body',
                                                                     'creation_time_body'))

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No support ticket with id")
    else:
        return HttpResponseBadRequest("No user with id")


@login_required
def resolve_support_ticket(request, user_id, support_ticket_id):
    existing = get_user_with_id(user_id)

    if existing is not None:

        support_ticket = get_support_ticket_with_id(support_ticket_id)

        if support_ticket is not None:

            support_ticket.resolved = True

            response = Response.objects.create(support_ticket_id=support_ticket.support_id,
                                               updated_time_body=support_ticket.updated_time)

            serialized_response_list = serializers.serialize('json', [response],
                                                             fields=('support_ticket_id', 'updated_time_body'))
            support_ticket.save()

            return JsonResponse({'response_list': serialized_response_list})
        else:
            return HttpResponseBadRequest("No support ticket with id")
    else:
        return HttpResponseBadRequest("No user with id")
