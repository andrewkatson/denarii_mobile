from django.contrib.auth import authenticate
from django.contrib.auth.decorators import login_required
from django.core import serializers
from django.http import JsonResponse, HttpResponseBadRequest

try:
    from Backend.settings import DEBUG
except ImportError as e:
    from test.settings import DEBUG

from DenariiMobile.interface import wallet
from DenariiMobile.models import WalletDetails, DenariiUser

if DEBUG:
    import DenariiMobile.testing.testing_denarii_client as denarii_client
else:
    import denarii_client

client = denarii_client.DenariiClient()


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


def get_wallet(user):
    # Only get the first wallet for now. In theory there could be many, but
    # we want to enforce one username + email per wallet.
    return user.walletdetails_set.all()[0]


def get_user_id(request, username, email, password):
    existing = get_user(username, email, password)
    if existing is not None:

        existing_wallet = get_wallet(existing)
        serialized_wallet = serializers.serialize('json', [existing_wallet], fields='user_identifier')
        return JsonResponse({'wallet': serialized_wallet})
    else:
        new_user = DenariiUser.objects.create_user(username=username, email=email, password=password)
        new_user.save()

        new_wallet_details = new_user.walletdetails_set.create(user_identifier=new_user.id)
        new_wallet_details.save()

        serialized_wallet = serializers.serialize('json', [new_wallet_details], fields='user_identifier')
        return JsonResponse({'wallet': serialized_wallet})


@login_required
def create_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing)

        wallet_interface = wallet.Wallet(wallet_name, password)
        res = client.create_wallet(wallet_interface)
        if res is True:
            existing_wallet.wallet_name = wallet
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

        if amount_sent == amount:
            return JsonResponse({'wallet': serialized_wallet})
        else:
            return HttpResponseBadRequest("Could not send denarii")

    else:
        return HttpResponseBadRequest("No user with id")
