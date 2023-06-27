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


def get_all_test_values(prefix):
    new_user = f"{prefix}_user"
    new_email = f"{prefix}_email@email.com"
    new_password = f"{prefix}_password"
    new_wallet_name = f"{prefix}_name"
    new_wallet_password = f"{prefix}_password"
    user_id = run_general_setup(new_user, new_email, new_password, new_wallet_name, new_wallet_password)

    request = create_user_request(user_id)

    return {
        'request': request,
        'username': new_user,
        'email': new_email,
        'password': new_password,
        'wallet_name': new_wallet_name,
        'wallet_password': new_wallet_password,
        'user_id': user_id
    }


def create_user_request(user_id):
    user = DenariiUser.objects.get(id=user_id)
    request = HttpRequest()
    request.user = user
    return request


def request_a_reset(username):
    request_reset(None, username)


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
        password = "lastpass"
        _ = get_user_id(None, self.user, self.email, password)

        response = get_user_id(None, self.user, "otheremail@email.com", "otherpassword")

        self.assertEqual(type(response), HttpResponseBadRequest)

    def test_get_user_id_existing_email_clashes_returns_error(self):
        password = "lastpass"
        _ = get_user_id(None, self.user, self.email, password)

        response = get_user_id(None, "otheruser", self.email, "otherpassword")

        self.assertEqual(type(response), HttpResponseBadRequest)

    def test_create_wallet_attempts_to_create_wallet(self):
        password = "other_other_password"
        user_id = create_user(self.user, self.email, password)

        request = create_user_request(user_id)
        response = create_wallet(request, user_id, self.wallet_name, self.wallet_password)
        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_open_wallet_attempts_to_open_wallet(self):
        dict_of_values = get_all_test_values("open_wallet")

        response = open_wallet(dict_of_values['request'], dict_of_values['user_id'],
                               dict_of_values['wallet_name'], dict_of_values['wallet_password'])
        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_restore_wallet_attempts_to_restore_wallet(self):
        dict_of_values = get_all_test_values("restore_wallet")

        response = restore_wallet(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                  dict_of_values['wallet_password'], self.seed)
        fields = get_json_fields(response)

        self.assertFalse('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_get_balance_attempts_to_get_balance(self):
        dict_of_values = get_all_test_values("get_balance")

        response = get_balance(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'])
        fields = get_json_fields(response)

        self.assertEquals(fields['balance'], 1.0)

    def test_send_denarii_attempts_to_send_denarii(self):
        dict_of_values = get_all_test_values("send_denarii")

        response = send_denarii(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                self.other_address, self.amount_to_send)
        fields = get_json_fields(response)

        self.assertFalse("balance" in fields)

    def test_request_reset_with_username(self):
        dict_of_values = get_all_test_values("request_reset_with_username")

        user = get_user_with_username(dict_of_values['username'])

        print(user.username)

        response = request_reset(dict_of_values['request'], dict_of_values['username'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertNotEqual(user.reset_id, 0)

    def test_request_reset_with_email(self):
        dict_of_values = get_all_test_values("request_reset_with_email")

        response = request_reset(dict_of_values['request'], dict_of_values['email'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertNotEqual(user.reset_id, 0)

    def test_verify_reset_with_username(self):
        dict_of_values = get_all_test_values("verify_reset_with_username")

        request_a_reset(dict_of_values['username'])

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        response = verify_reset(dict_of_values['request'], dict_of_values['username'], user.reset_id)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertEqual(user.reset_id, 0)

    def test_verify_reset_with_email(self):
        dict_of_values = get_all_test_values("verify_reset_with_email")

        request_a_reset(dict_of_values['email'])

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        response = verify_reset(dict_of_values['request'], dict_of_values['email'], user.reset_id)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertEqual(user.reset_id, 0)

    def test_reset_password(self):
        dict_of_values = get_all_test_values("reset_password")

        new_password = "newpassword"
        response = reset_password(dict_of_values['request'], dict_of_values['username'], dict_of_values['email'],
                                  new_password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertEqual(user.password, new_password)

    def test_get_prices_with_less_than_num_asks(self):
        pass

    def test_get_prices_with_more_than_num_asks(self):
        pass

    def test_buy_denarii_regardless_of_price_with_enough_to_buy(self):
        pass

    def test_buy_denarii_regardless_of_price_without_enough_to_buy(self):
        pass

    def test_buy_denarii_considering_price_with_enough_to_buy_within_asking_price(self):
        pass

    def test_buy_denarii_considering_price_with_enough_to_buy_outside_of_asking_price(self):
        pass

    def test_buy_denarii_considering_price_without_enough_to_buy(self):
        pass

    def test_transfer_denarii_with_exactly_amount(self):
        pass

    def test_transfer_denarii_with_less_than_amount(self):
        pass

    def test_transfer_denarii_with_ask_that_doesnt_exist(self):
        pass

    def test_make_denarii_ask(self):
        pass

    def test_poll_for_completed_transaction(self):
        pass

    def test_cancel_ask_that_still_exists(self):
        pass

    def test_cancel_ask_that_does_not_exist(self):
        pass
