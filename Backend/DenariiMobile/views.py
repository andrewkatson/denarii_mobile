from django.http import JsonResponse, HttpResponseBadRequest
from django.shortcuts import render

import denarii_client

from interface import wallet
from models import wallet_details, user


def get_user(username, email):
    try:
        existing = user.User.objects.get(name=username, email=email)
        return existing
    except user.User.DoesNotExist:
        return None


def get_user_with_id(user_id):
    try:
        existing = user.User.objects.get(id=user_id)
        return existing
    except user.User.DoesNotExist:
        return None


def get_wallet(wallet_id):
    try:
        existing = wallet_details.WalletDetails.objects.get(id=wallet_id)
        return existing
    except wallet_details.WalletDetails.DoesNotExist:
        return None


def get_user_id(request, username, email):
    existing = get_user(username, email)
    if existing is not None:

        existing_wallet = get_wallet(existing.wallet_id)

        return JsonResponse({'wallet': existing_wallet})
    else:
        new_wallet_details = wallet_details.WalletDetails()
        new_user = user.User(name=username, email=email)

        new_user.wallet_id = new_wallet_details.id
        new_user.save()

        new_wallet_details.user_id = new_user.id
        new_wallet_details.save()

        return JsonResponse({'wallet': new_wallet_details})


def create_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing.wallet_id)

        client = denarii_client.DenariiClient()
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

                    existing_wallet.wallet_password = ""

                    return JsonResponse({'wallet': existing_wallet})
                else:
                    return HttpResponseBadRequest("Could not get wallet address")
            else:
                return HttpResponseBadRequest("Could not get wallet seed")
        else:
            return HttpResponseBadRequest("Cannot create wallet")
    else:
        return HttpResponseBadRequest("No user with id")


def restore_wallet(request, user_id, wallet_name, password, seed):
    existing = get_user_with_id(user_id)
    if existing is not None:

        existing_wallet = get_wallet(existing.wallet_id)

        client = denarii_client.DenariiClient()
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

                existing_wallet.wallet_password = ""
                existing_wallet.seed = ""

                return JsonResponse({'wallet': existing_wallet})
            else:
                return HttpResponseBadRequest("Could not get wallet address")
        else:
            return HttpResponseBadRequest("Could not restore wallet")
    else:
        return HttpResponseBadRequest("No user with id")


def open_wallet(request, user_id, wallet_name, password):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing.wallet_id)

        client = denarii_client.DenariiClient()
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

                    existing_wallet.wallet_password = ""

                    return JsonResponse({'wallet': existing_wallet})
                else:
                    return HttpResponseBadRequest("Could not get wallet address")
            else:
                return HttpResponseBadRequest("Could not get wallet seed")
        else:
            return HttpResponseBadRequest("Could not open wallet")
    else:
        return HttpResponseBadRequest("No user with id")


def get_balance(request, user_id, wallet_name):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing.wallet_id)

        client = denarii_client.DenariiClient()
        wallet_interface = wallet.Wallet(wallet_name, existing_wallet.wallet_password)

        wallet_interface.balance = client.get_balance_of_wallet(wallet_interface)
        existing_wallet.balance = wallet_interface.balance

        existing_wallet.save()

        return JsonResponse({'wallet': existing_wallet})

    else:
        return HttpResponseBadRequest("No user with id")


def send_denarii(request, user_id, wallet_name, address, amount):
    existing = get_user_with_id(user_id)
    if existing is not None:
        existing_wallet = get_wallet(existing.wallet_id)

        client = denarii_client.DenariiClient()
        sender = wallet.Wallet(wallet_name, existing_wallet.wallet_password)
        # We only need the receiver's address not their wallet name and password
        receiver = wallet.Wallet("", "")
        receiver.address = address

        amount_sent = client.transfer_money(float(amount), sender, receiver)

        existing_wallet.balance = client.get_balance_of_wallet(sender)

        existing_wallet.save()

        if amount_sent == amount:
            return JsonResponse({'wallet': existing_wallet})
        else:
            return HttpResponseBadRequest("Could not send denarii")

    else:
        return HttpResponseBadRequest("No user with id")
