import pandas as pd

from django.http import HttpRequest
from django.test import TestCase

from DenariiMobile.views import *


def get_json_fields(response):
    content = response.content
    text = content.decode('utf8')
    # TODO(katsonandrew): Figure out a better way to unpack the response.
    series = pd.read_json(text, typ='series', orient='records')
    df = pd.read_json(series.values[0])
    for _, content in df['fields'].items():
        return content


def create_user(name, email, password):
    response = get_user_id(None, name, email, password)

    fields = get_json_fields(response)
    return fields['user_identifier']


def make_wallet(user_id, wallet_name, password):
    request = create_user_request(user_id)
    response = create_wallet(request, user_id, wallet_name, password)
    try:
        fields = get_json_fields(response)
        return 'seed' in fields and 'wallet_address' in fields
    except Exception as error:
        print(error)
        return False


def run_general_setup(user_name, email, user_password, wallet_name, wallet_password):
    user_id = create_user(user_name, email, user_password)
    output = make_wallet(user_id, wallet_name, wallet_password)
    if output is False:
        raise Exception("No output from make_wallet")
    return user_id


def create_user_request(user_id):
    user = DenariiUser.objects.get(id=user_id)
    request = HttpRequest()
    request.user = user
    return request


class ViewsTestCase(TestCase):
    user = "user"
    email = "email@email.com"
    wallet_name = "new_wallet"
    wallet_password = "new_password"
    seed = "Person Man Camera Tv"
    other_address = "ADJAFSDJFGADSLFJDASKLFKDSA"
    amount_to_send = 1.0

    def test_get_user_id_new_user_returns_empty_wallet(self):
        password = "my_password"
        response = get_user_id(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)
        new_wallet_fields = get_json_fields(response)

        self.assertEqual(new_wallet_fields['user_identifier'], new_user.id)

    def test_get_user_id_old_user_returns_existing_wallet(self):
        password = "other_password"
        _ = get_user_id(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)

        response = get_user_id(None, self.user, self.email, password)

        wallet_fields = get_json_fields(response)

        self.assertEqual(wallet_fields['user_identifier'], new_user.id)

    def test_get_user_id_existing_username_clashes_returns_error(self):
        pass

    def test_get_user_id_existing_email_clashes_returns_error(self):
        pass

    def test_create_wallet_attempts_to_create_wallet(self):
        password = "other_other_password"
        user_id = create_user(self.user, self.email, password)

        request = create_user_request(user_id)
        response = create_wallet(request, user_id, self.wallet_name, self.wallet_password)
        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_open_wallet_attempts_to_open_wallet(self):
        new_user = "open_wallet_user"
        new_email = "open_wallet_email@email.com"
        new_password = "open_wallet_password"
        new_wallet_name = "open_wallet_name"
        new_wallet_password = "open_wallet_password"
        user_id = run_general_setup(new_user, new_email, new_password, new_wallet_name, new_wallet_password)

        request = create_user_request(user_id)
        response = open_wallet(request, user_id, new_wallet_name, new_wallet_password)
        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_restore_wallet_attempts_to_restore_wallet(self):
        new_user = "restore_wallet_user"
        new_email = "restore_wallet_email@email.com"
        new_password = "restore_wallet_password"
        new_wallet_name = "restore_wallet_name"
        new_wallet_password = "restore_wallet_password"
        user_id = run_general_setup(new_user, new_email, new_password, new_wallet_name, new_wallet_password)

        request = create_user_request(user_id)
        response = restore_wallet(request, user_id, new_wallet_name, new_wallet_password, self.seed)
        fields = get_json_fields(response)

        self.assertFalse('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_get_balance_attempts_to_get_balance(self):
        new_user = "get_balance_user"
        new_email = "get_balance_email@email.com"
        new_password = "get_balance_password"
        new_wallet_name = "get_balance_name"
        new_wallet_password = "get_balance_password"
        user_id = run_general_setup(new_user, new_email, new_password, new_wallet_name, new_wallet_password)

        request = create_user_request(user_id)
        response = get_balance(request, user_id, new_wallet_name)
        fields = get_json_fields(response)

        self.assertEquals(fields['balance'], 1.0)

    def test_send_denarii_attempts_to_send_denarii(self):
        new_user = "send_denarii_user"
        new_email = "send_denarii_email@email.com"
        new_password = "send_denarii_password"
        new_wallet_name = "send_denarii_name"
        new_wallet_password = "send_denarii_password"
        user_id = run_general_setup(new_user, new_email, new_password, new_wallet_name, new_wallet_password)

        request = create_user_request(user_id)
        response = send_denarii(request, user_id, new_wallet_name, self.other_address, self.amount_to_send)
        fields = get_json_fields(response)

        self.assertFalse("balance" in fields)

    def test_request_reset(self):
        pass

    def verify_reset(self):
        pass

    def test_reset_password(self):
        pass
