import random

import pandas as pd
from django.contrib.sessions.backends.db import SessionStore
from django.http import HttpRequest, HttpResponseRedirect
from django.test import TestCase

from DenariiMobile.interface.ask import *
from DenariiMobile.views import *


def get_json_fields(response):
    series = get_all_json_objects(response)
    df = pd.read_json(series.values[0])
    for _, content in df['fields'].items():
        return content


def get_all_json_objects(response):
    assert type(response) != HttpResponseBadRequest
    content = response.content
    text = content.decode('utf8')
    # TODO(katsonandrew): Figure out a better way to unpack the response.
    return pd.read_json(text, typ='series', orient='records')


def get_all_json_objects_field(response, field):
    series = get_all_json_objects(response)
    field_values = []
    for i in range(len(series.values)):
        df = pd.read_json(series.values[i])
        if df.empty:
            continue
        for _, content in df['fields'].items():
            field_values.append(content[field])
    return field_values


def create_user(name, email, password):
    response = register(None, name, email, password)

    fields = get_json_fields(response)
    return fields['user_identifier']


def login_the_user(username_or_email, password, user_id):
    request = create_user_request(user_id)
    response = login_user(request, username_or_email, password)

    assert type(response) != HttpResponseBadRequest

    return response


def create_ask(request, user_id, amount, asking_price):
    response = make_denarii_ask(request, user_id, amount, asking_price)
    fields = get_json_fields(response)
    return Ask(fields['ask_id'], amount, asking_price)


def create_asks(request, user_id):
    length = 6
    asks = []
    amount_and_asking_price = {
        'amounts': [1.0, 2.0, 34.0, 10.0, 2.5, 9.0],
        'asking_prices': [100.3, 10.0, 11.5, 12.0, 78.0, 23.1]
    }

    for i in range(length):
        amount = amount_and_asking_price.get('amounts')[i]
        asking_price = amount_and_asking_price.get('asking_prices')[i]

        asks.append(create_ask(request, user_id, amount, asking_price))

    return asks


def create_random_asks(request, user_id, num):
    asks = []
    for i in range(num):
        amount = random.uniform(0.0, 100.0)
        asking_price = random.uniform(0.0, 100.0)

        asks.append(create_ask(request, user_id, amount, asking_price))

    return asks


def create_user_request(user_id):
    user = DenariiUser.objects.get(id=user_id)
    request = HttpRequest()
    request.user = user
    request.session = SessionStore()
    request.META["SERVER_NAME"] = "localhost"
    request.META["SERVER_PORT"] = "8000"
    return request


def request_a_reset(username):
    request_reset(None, username)


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
    _ = login_the_user(user_name, user_password, user_id)
    output = make_wallet(user_id, wallet_name, wallet_password)
    if output is False:
        raise Exception("No output from make_wallet")
    return user_id


def get_all_test_values(prefix):
    new_user = f"{prefix}_user"
    new_email = f"{prefix}_email@email.com"
    new_password = f"{prefix}_password1$A"
    new_wallet_name = f"{prefix}_name"
    new_wallet_password = f"{prefix}_password2#B"
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


class ViewsTestCase(TestCase):
    user = "username_is"
    email = "email@email.com"
    wallet_name = "new_wallet"
    wallet_password = "new_password4%C"
    seed = "Person Man Camera Tv"
    other_address = "ADJAFSDJFGADSLFJDASKLFKDSA"
    amount_to_send = 1.0

    def test_register_new_user_returns_empty_user(self):
        password = "my_password1@O"
        response = register(None, self.user, self.email, password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        new_user = get_user(self.user, self.email, password)
        new_user_fields = get_json_fields(response)
        self.assertEqual(new_user_fields['user_identifier'], str(new_user.id))

    def test_register_existing_username_clashes_returns_error(self):
        password = "lastpass9$G"
        _ = register(None, self.user, self.email, password)

        response = register(None, self.user, "otheremail@email.com", "otherpassword9$G")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "User already exists", status_code=400)

    def test_register_existing_email_clashes_returns_error(self):
        password = "lastpass3#M"
        _ = register(None, self.user, self.email, password)

        response = register(None, "otheruserhere", self.email, "otherpassword3#M")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "User already exists", status_code=400)

    def test_register_invalid_params_returns_all_invalid_params(self):
        response = register(None, "124", "1", "a")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.username, status_code=400)
        self.assertContains(response, Params.email, status_code=400)
        self.assertContains(response, Params.password, status_code=400)

    def test_login_old_user_with_username_returns_existing_user(self):
        password = "other_password8=F"
        _ = register(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)

        request = create_user_request(new_user.id)

        response = login_user(request, self.user, password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)
        self.assertEqual(fields['user_identifier'], str(new_user.id))

    def test_login_old_user_with_email_returns_existing_user(self):
        password = "other_password8=F"
        _ = register(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)

        request = create_user_request(new_user.id)

        response = login_user(request, self.email, password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)
        self.assertEqual(fields['user_identifier'], str(new_user.id))

    def test_login_old_user_with_wrong_password_returns_existing_user(self):
        password = "other_password8=F"
        _ = register(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)

        request = create_user_request(new_user.id)

        response = login_user(request, self.user, f"f{password}_1")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Password was not correct", status_code=400)

    def test_login_invalid_params_returns_all_invalid_params(self):
        response = login_user(None, "124", "a")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.username_or_email, status_code=400)
        self.assertContains(response, Params.password, status_code=400)

    def test_create_wallet_attempts_to_create_wallet(self):
        password = "other_other_passwordD%6"
        user_id = create_user(self.user, self.email, password)
        _ = login_the_user(self.user, password, user_id)

        request = create_user_request(user_id)
        response = create_wallet(request, user_id, self.wallet_name, self.wallet_password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_create_wallet_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("create_wallet_invalid_params_returns_all_invalid_params")

        response = create_wallet(dict_of_values['request'], "123", "123", "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.wallet_name, status_code=400)
        self.assertContains(response, Params.password, status_code=400)

    def test_open_wallet_attempts_to_open_wallet(self):
        dict_of_values = get_all_test_values("open_wallet")

        response = open_wallet(dict_of_values['request'], dict_of_values['user_id'],
                               dict_of_values['wallet_name'], dict_of_values['wallet_password'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_open_wallet_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("open_wallet_invalid_params_returns_all_invalid_params")

        response = open_wallet(dict_of_values['request'], "123", "123", "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.wallet_name, status_code=400)
        self.assertContains(response, Params.password, status_code=400)

    def test_restore_wallet_attempts_to_restore_wallet(self):
        dict_of_values = get_all_test_values("restore_wallet")

        response = restore_wallet(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                  dict_of_values['wallet_password'], self.seed)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertFalse('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_restore_wallet_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("restore_wallet_invalid_params_returns_all_invalid_params")

        response = restore_wallet(dict_of_values['request'], "123", "123", "123", "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.wallet_name, status_code=400)
        self.assertContains(response, Params.password, status_code=400)
        self.assertContains(response, Params.seed, status_code=400)

    def test_get_balance_attempts_to_get_balance(self):
        dict_of_values = get_all_test_values("get_balance")

        response = get_balance(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertEquals(fields['balance'], 2.0)

    def test_get_balance_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_balance_invalid_params_returns_all_invalid_params")

        response = get_balance(dict_of_values['request'], "123", "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.wallet_name, status_code=400)

    def test_send_denarii_attempts_to_send_denarii(self):
        dict_of_values = get_all_test_values("send_denarii")

        response = send_denarii(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                self.other_address, self.amount_to_send)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertFalse("balance" in fields)

    def test_send_denarii_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("send_denarii_invalid_params_returns_all_invalid_params")

        response = send_denarii(dict_of_values['request'], "123", "123", "a", "b")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.wallet_name, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.address, status_code=400)

    def test_request_reset_with_username(self):
        dict_of_values = get_all_test_values("request_reset_with_username")

        _ = get_user_with_username(dict_of_values['username'])

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

    def test_request_reset_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("request_reset_invalid_params_returns_all_invalid_params")

        response = request_reset(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.username_or_email, status_code=400)

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

    def test_verify_reset_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("verify_reset_invalid_params_returns_all_invalid_params")

        response = verify_reset(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.username_or_email, status_code=400)
        self.assertContains(response, Params.reset_id, status_code=400)

    def test_reset_password(self):
        dict_of_values = get_all_test_values("reset_password")

        new_password = "newpassword#2E"
        response = reset_password(dict_of_values['request'], dict_of_values['username'], dict_of_values['email'],
                                  new_password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        user = get_user_with_username_and_email(dict_of_values['username'], dict_of_values['email'])

        self.assertEqual(user.password, new_password)

    def test_reset_password_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("reset_password_invalid_params_returns_all_invalid_params")

        response = reset_password(dict_of_values['request'], "123", "A", "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.username, status_code=400)
        self.assertContains(response, Params.email, status_code=400)
        self.assertContains(response, Params.password, status_code=400)

    def test_get_prices_with_less_than_num_asks(self):
        seller_test_values = get_all_test_values("get_prices_with_less_than_num_asks_seller")

        asks = create_random_asks(seller_test_values['request'], seller_test_values['user_id'], 10)

        buyer_test_values = get_all_test_values("get_prices_with_less_than_num_asks_buyer")

        response = get_prices(buyer_test_values['request'], buyer_test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        # every ask should be present since we only made 10
        for ask in asks:
            self.assertIn(ask.ask_id, ask_ids)

    def test_get_prices_with_more_than_num_asks(self):
        seller_test_values = get_all_test_values("get_prices_with_more_than_num_asks_seller")

        asks = create_random_asks(seller_test_values['request'], seller_test_values['user_id'], 200)

        # sort and keep only the top 100 by price
        asks.sort(key=lambda x: x.asking_price)
        low_asks = asks[:100]

        buyer_test_values = get_all_test_values("get_prices_with_more_than_num_asks_buyer")

        response = get_prices(buyer_test_values['request'], buyer_test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        for ask in low_asks:
            self.assertIn(ask.ask_id, ask_ids)

    def test_get_prices_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_prices_invalid_params_returns_all_invalid_params")

        response = get_prices(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_buy_denarii_regardless_of_price_with_enough_to_buy(self):
        test_values = get_all_test_values("buy_denarii_regardless_of_price_with_enough_to_buy")

        asks = create_random_asks(test_values['request'], test_values['user_id'], 200)
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 1.0, "True", "True")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        total_bought = 0
        for ask in asks:
            if ask.ask_id in ask_ids:
                total_bought += ask.amount

        self.assertGreaterEqual(total_bought, amount_to_buy)

    def test_buy_denarii_regardless_of_price_without_enough_to_buy_fail_on_not_enough_to_buy_but_some_bought(self):
        test_values = get_all_test_values(
            "buy_denarii_regardless_of_price_without_enough_to_buy_fail_on_not_enough_to_buy_but_some_bought")

        asks = create_random_asks(test_values['request'], test_values['user_id'], 2)
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 1.0, "True", "True")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Could not buy the asked amount", status_code=400)

    def test_buy_denarii_regardless_of_price_without_enough_to_buy_succeed_on_not_enough_to_buy_but_some_bought(self):
        test_values = get_all_test_values(
            "buy_denarii_regardless_of_price_without_enough_to_buy_succeed_on_not_enough_to_buy_but_some_bought")

        asks = create_random_asks(test_values['request'], test_values['user_id'], 2)
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 1.0, "True", "False")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        total_bought = 0
        for ask in asks:
            if ask.ask_id in ask_ids:
                total_bought += ask.amount

        self.assertGreater(total_bought, 0)
        self.assertLessEqual(total_bought, amount_to_buy)

    def test_buy_denarii_considering_price_with_enough_to_buy_within_asking_price(self):
        test_values = get_all_test_values("buy_denarii_considering_price_with_enough_to_buy_within_asking_price")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 100.0, "False", "True")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        total_bought = 0
        for ask in asks:
            if ask.ask_id in ask_ids:
                total_bought += ask.amount

        self.assertGreaterEqual(total_bought, amount_to_buy)

    def test_buy_denarii_considering_price_with_enough_to_buy_outside_of_asking_price_fail_on_not_enough_in_asking_price(
            self):
        test_values = get_all_test_values(
            "buy_denarii_considering_price_with_enough_to_buy_outside_of_asking_price_fail_on_not_enough_in_asking_price")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 30.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 10.0, "False", "True")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Could not buy the asked amount", status_code=400)

    def test_buy_denarii_considering_price_with_enough_to_buy_outside_of_asking_price_succeed_on_not_enough_in_asking_price(
            self):
        test_values = get_all_test_values(
            "buy_denarii_considering_price_with_enough_to_buy_outside_of_asking_price_succeed_on_not_enough_in_asking_price")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 30.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 10.0, "False", "False")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        total_bought = 0
        for ask in asks:
            if ask.ask_id in ask_ids:
                total_bought += ask.amount

        self.assertGreater(total_bought, 0)
        self.assertLessEqual(total_bought, amount_to_buy)

    def test_buy_denarii_considering_price_without_enough_to_buy_succeed_on_not_enough_to_buy_but_some_bought(self):
        test_values = get_all_test_values(
            "buy_denarii_considering_price_without_enough_to_buy_succeed_on_not_enough_to_buy_but_some_bought")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 100.0, "False", "False")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, 'ask_id')

        total_bought = 0
        for ask in asks:
            if ask.ask_id in ask_ids:
                total_bought += ask.amount

        self.assertGreater(total_bought, 0)
        self.assertLessEqual(total_bought, amount_to_buy)

    def test_buy_denarii_considering_price_without_enough_to_buy_fail_on_not_enough_to_buy_but_some_bought(self):
        test_values = get_all_test_values(
            "buy_denarii_considering_price_without_enough_to_buy_fail_on_not_enough_to_buy_but_some_bought")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 100.0, "False", "True")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Could not buy the asked amount", status_code=400)

    def test_buy_denarii_considering_price_with_zero_matches(self):
        test_values = get_all_test_values("buy_denarii_considering_price_with_zero_matches")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 0.0, "False", "True")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "No asks could be met with the bid price", status_code=400)

    def test_buy_denarii_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("buy_denarii_invalid_params_returns_all_invalid_params")

        response = buy_denarii(dict_of_values['request'], "123", "A", "B", "C", "D")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.bid_price, status_code=400)
        self.assertContains(response, Params.buy_regardless_of_price, status_code=400)
        self.assertContains(response, Params.fail_if_full_amount_isnt_met, status_code=400)

    def test_transfer_denarii_with_exactly_amount(self):

        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_with_exactly_amount_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_with_exactly_amount_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        ask = get_ask_with_id(ask_id)

        self.assertTrue(ask.is_settled)
        self.assertFalse(ask.has_been_seen_by_seller)

    def test_transfer_denarii_with_less_than_amount(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_with_less_than_amount_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_with_less_than_amount_buyer")

        # Buy exactly half of one of the sellers lowest prices asks
        amount_to_buy = 1.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == 2.0 and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        first_transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(first_transfer_response), HttpResponseBadRequest)

        # If we try to look up ask it should be there still.
        poll_response = poll_for_completed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        all_remaining_ask_ids = get_all_json_objects_field(poll_response, 'ask_id')

        self.assertIn(ask_id, all_remaining_ask_ids)

    def test_transfer_denarii_with_ask_that_doesnt_exist(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_with_ask_that_doesnt_exist_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_with_ask_that_doesnt_exist_buyer")

        response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'],
                                    "349424ec-17d2-41d4-8bd2-4ff8de3de4a4")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "No ask with id", status_code=400)

    def test_transfer_denarii_with_ask_not_in_escrow(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_with_ask_not_in_escrow_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_with_ask_not_in_escrow_buyer")

        # Buy exactly half of one of the sellers lowest prices asks
        amount_to_buy = 1.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == 2.0 and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        first_transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(first_transfer_response), HttpResponseBadRequest)

        # Transferring the ask a second time in a row will cause an error because it is already out of escrow
        second_transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertEqual(type(second_transfer_response), HttpResponseBadRequest)

        self.assertContains(second_transfer_response, "Ask was not bought", status_code=400)

    def test_transfer_denarii_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("transfer_denarii_invalid_params_returns_all_invalid_params")

        response = transfer_denarii(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_make_denarii_ask(self):
        test_values = get_all_test_values('make_denarii_ask')

        response = make_denarii_ask(test_values['request'], test_values['user_id'], 1.0, 10.0)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

    def test_make_denarii_ask_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("make_denarii_ask_invalid_params_returns_all_invalid_params")

        response = make_denarii_ask(dict_of_values['request'], "123", "A", "B")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.asking_price, status_code=400)

    def test_poll_for_completed_transaction(self):
        seller_test_values = get_all_test_values("poll_for_completed_transaction_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("poll_for_completed_transaction_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # We want to buy (but not transfer) an ask with 34.0 at 11.5 price
        amount_to_buy_two = 34.0
        bid_price_two = 11.5
        buy_response_two = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy_two,
                                       bid_price_two,
                                       "False", "False")

        self.assertNotEqual(type(buy_response_two), HttpResponseBadRequest)

        # We want the ask id of the ask with 34.0 to offer (at 11.5 price)
        second_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy_two and ask.asking_price == bid_price_two:
                second_ask_id = ask.ask_id
                break

        self.assertNotEqual(second_ask_id, 0)

        poll_response = poll_for_completed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(poll_response, 'ask_id')

        self.assertNotEqual(len(asks), len(ask_ids))

        for ask_id in ask_ids:
            self.assertTrue(get_ask_with_id(ask_id).is_settled)
            self.assertFalse(get_ask_with_id(ask_id).in_escrow)

    def test_poll_for_completed_transaction_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("poll_for_completed_transaction_invalid_params_returns_all_invalid_params")

        response = poll_for_completed_transaction(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_cancel_ask_that_still_exists_but_isnt_settled(self):
        test_values = get_all_test_values("cancel_ask_that_still_exists")

        asks = create_asks(test_values['request'], test_values['user_id'])
        ask_id = asks[0].ask_id
        response = cancel_ask(test_values['request'], test_values['user_id'], ask_id)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        self.assertEqual(get_ask_with_id(ask_id), None)

    def test_cancel_ask_that_does_not_exist(self):
        test_values = get_all_test_values("cancel_ask_that_does_not_exist")

        _ = create_asks(test_values['request'], test_values['user_id'])
        response = cancel_ask(test_values['request'], test_values['user_id'], "a0c3f8f1-6f4d-4b00-822c-58c7c313ac79")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "No ask with id", status_code=400)

    def test_cancel_ask_that_is_in_escrow(self):
        seller_test_values = get_all_test_values("cancel_ask_that_is_in_escrow_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("cancel_ask_that_is_in_escrow_buyer")

        amount_to_buy = 1.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 10000.0,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        cancel_response = cancel_ask(seller_test_values['request'], seller_test_values['user_id'], ask_ids[0])

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

        self.assertContains(cancel_response, "Ask was already purchased", status_code=400)

    def test_cancel_ask_that_was_settled(self):
        seller_test_values = get_all_test_values("cancel_ask_that_was_settled_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("cancel_ask_that_was_settled_buyer")

        amount_to_buy = 1.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 10000.0,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_ids[0])

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        cancel_response = cancel_ask(seller_test_values['request'], seller_test_values['user_id'], ask_ids[0])

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

        self.assertContains(cancel_response, "Ask was already purchased", status_code=400)

    def test_cancel_ask_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("cancel_ask_invalid_params_returns_all_invalid_params")

        response = cancel_ask(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_has_credit_card_info_with_info(self):
        test_values = get_all_test_values("has_credit_card_info_with_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        has_response = has_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(has_response), HttpResponseBadRequest)

        fields = get_json_fields(has_response)

        self.assertEqual(fields['has_credit_card_info'], True)

    def test_has_credit_card_info_without_info(self):
        test_values = get_all_test_values("has_credit_card_info_without_info")

        has_response = has_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(has_response), HttpResponseBadRequest)

        fields = get_json_fields(has_response)

        self.assertEqual(fields['has_credit_card_info'], False)

    def test_has_credit_card_info_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("has_credit_card_info_invalid_params_returns_all_invalid_params")

        response = has_credit_card_info(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_set_credit_card_info(self):
        test_values = get_all_test_values("set_credit_card_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

    def test_set_credit_card_info_fail_token_create(self):
        test_values = get_all_test_values("set_credit_card_info_fail_token_create")

        # A 1999-999-9999 card number will cause token creation to fail
        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "1999-999-9999", "2",
                                            "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

        self.assertContains(set_response, "Could not create customer card", status_code=400)

    def test_set_credit_card_info_fail_customer_create(self):
        # The prefix will cause a failure to create a customer
        test_values = get_all_test_values("fail_create_customer")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

        self.assertContains(set_response, "Could not create customer", status_code=400)

    def test_set_credit_card_info_fail_setup_intent_create(self):
        # The prefix will cause a failure to create a setup intent
        test_values = get_all_test_values("fail_setup_intent")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

        self.assertContains(set_response, "Could not create setup intent", status_code=400)

    def test_set_credit_card_info_with_info_already_set(self):
        test_values = get_all_test_values("set_credit_card_info_with_info_already_set")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        set_response_two = set_credit_card_info(test_values['request'], test_values['user_id'], "223-456-7890",
                                                "3",
                                                "2002", "456")

        self.assertEqual(type(set_response_two), HttpResponseBadRequest)

        self.assertContains(set_response_two, "User already has credit card info", status_code=400)

    def test_set_credit_card_info_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("set_credit_card_info_invalid_params_returns_all_invalid_params")

        response = set_credit_card_info(dict_of_values['request'], "123", "A", "B", "C", "D")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.card_number, status_code=400)
        self.assertContains(response, Params.expiration_date_month, status_code=400)
        self.assertContains(response, Params.expiration_date_year, status_code=400)
        self.assertContains(response, Params.security_code, status_code=400)

    def test_clear_credit_card_info(self):
        test_values = get_all_test_values("clear_credit_card_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(clear_response), HttpResponseBadRequest)

    def test_clear_credit_card_info_fail_customer_delete(self):
        # This prefix causes the customer deletion to fail
        test_values = get_all_test_values("fail_delete")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertEqual(type(clear_response), HttpResponseBadRequest)

        self.assertContains(clear_response, "Could not delete customer", status_code=400)

    def test_clear_credit_card_info_without_credit_card(self):
        test_values = get_all_test_values("clear_credit_card_info_without_credit_card")

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertEqual(type(clear_response), HttpResponseBadRequest)

        self.assertContains(clear_response, "No credit card for user", status_code=400)

    def test_clear_credit_card_info_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("clear_credit_card_info_invalid_params_returns_all_invalid_params")

        response = clear_credit_card_info(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_get_money_from_buyer(self):
        test_values = get_all_test_values("get_money_from_buyer")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(get_money_response), HttpResponseBadRequest)

    def test_get_money_from_buyer_fail_payment_intent_create(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # 103 amount will cause a payment intent creation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "103", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

        self.assertContains(get_money_response, "Could not create payment intent", status_code=400)

    def test_get_money_from_buyer_fail_payment_intent_confirm(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_confirm")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # 102 amount will cause a payment intent confirmation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "102", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

        self.assertContains(get_money_response, "Could not confirm payment intent", status_code=400)

    def test_get_money_from_buyer_fail_payment_intent_cancel(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_cancel")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # 101 amount will cause a payment intent cancellation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "101", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

        self.assertContains(get_money_response, "Could not cancel payment intent", status_code=400)

    def test_get_money_from_buyer_without_credit_card(self):
        test_values = get_all_test_values("get_money_from_buyer_without_credit_card")

        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

        self.assertContains(get_money_response, "No credit card for user", status_code=400)

    def test_get_money_from_buyer_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_money_from_buyer_invalid_params_returns_all_invalid_params")

        response = get_money_from_buyer(dict_of_values['request'], "123", "A", "1")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.currency, status_code=400)

    def test_send_money_to_seller(self):
        test_values = get_all_test_values("send_money_to_seller")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_to_seller_fail_payout_create(self):
        test_values = get_all_test_values("send_money_to_seller_fail_payout_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # 101 amount will cause a payout creation failure
        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "101", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

        self.assertContains(send_response, "Could not create payout", status_code=400)

    def test_send_money_to_seller_without_credit_card(self):
        test_values = get_all_test_values("send_money_to_seller_without_credit_card")

        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

        self.assertContains(send_response, "No credit card for user", status_code=400)

    def test_send_money_to_seller_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("send_money_to_seller_invalid_params_returns_all_invalid_params")

        response = send_money_to_seller(dict_of_values['request'], "123", "A", "1")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.currency, status_code=400)

    def test_is_transaction_settled_that_is_settled_with_remaining_ask(self):
        seller_test_values = get_all_test_values("is_transaction_settled_that_is_settled_with_remaining_ask_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("is_transaction_settled_that_is_settled_with_remaining_ask")

        amount_to_buy = 0.5
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 1000,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_ids[0])

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        poll_response = poll_for_completed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        is_settled_response = is_transaction_settled(buyer_test_values['request'], buyer_test_values['user_id'],
                                                     ask_ids[0])

        self.assertNotEqual(type(is_settled_response), HttpResponseBadRequest)

        fields = get_json_fields(is_settled_response)
        self.assertTrue(fields['transaction_was_settled'])

    def test_is_transaction_settled_that_is_settled_with_deleted_ask(self):
        seller_test_values = get_all_test_values("is_transaction_settled_that_is_settled_with_deleted_ask_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("is_transaction_settled_that_is_settled_with_deleted_ask")

        amount_to_buy = 2.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 1000,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_ids[0])

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        poll_response = poll_for_completed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        is_settled_response = is_transaction_settled(buyer_test_values['request'], buyer_test_values['user_id'],
                                                     ask_ids[0])

        self.assertNotEqual(type(is_settled_response), HttpResponseBadRequest)

        fields = get_json_fields(is_settled_response)
        self.assertTrue(fields['transaction_was_settled'])

    def test_is_transaction_settled_that_is_not_settled_yet(self):
        seller_test_values = get_all_test_values("is_transaction_settled_that_is_not_settled_yet_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("is_transaction_settled_that_is_not_settled_yet_buyer")

        amount_to_buy = 1.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 1000,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        is_settled_response = is_transaction_settled(buyer_test_values['request'], buyer_test_values['user_id'],
                                                     ask_ids[0])

        self.assertNotEqual(type(is_settled_response), HttpResponseBadRequest)

        fields = get_json_fields(is_settled_response)
        self.assertFalse(fields['transaction_was_settled'])

    def test_is_transaction_settled_that_buyer_hasnt_seen(self):
        seller_test_values = get_all_test_values("is_transaction_settled_that_buyer_hasnt_seen_seller")

        _ = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("is_transaction_settled_that_buyer_hasnt_seen_buyer")

        amount_to_buy = 1.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, 1000,
                                   "True", "True")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(buy_response, 'ask_id')

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_ids[0])

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        is_settled_response = is_transaction_settled(buyer_test_values['request'], buyer_test_values['user_id'],
                                                     ask_ids[0])

        self.assertNotEqual(type(is_settled_response), HttpResponseBadRequest)

        fields = get_json_fields(is_settled_response)
        self.assertFalse(fields['transaction_was_settled'])

    def test_is_transaction_settled_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("is_transaction_settled_invalid_params_returns_all_invalid_params")

        response = is_transaction_settled(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_delete_user_with_existing_user(self):
        test_values = get_all_test_values("delete_user_with_existing_user")

        response = delete_user(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        self.assertIsNone(get_user_with_id(test_values['user_id']))

    def test_delete_user_with_outstanding_asks(self):
        seller_test_values = get_all_test_values("delete_user_with_outstanding_asks_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # buy one and settle it
        buyer_test_values = get_all_test_values("delete_user_with_outstanding_asks_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # buy another so it is in escrow
        amount_to_buy = 20
        bid_price = 11.5
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        response = delete_user(seller_test_values['request'], seller_test_values['user_id'])

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Outstanding asks or buys so cannot delete user", status_code=400)

    def test_delete_user_with_outstanding_buys(self):
        seller_test_values = get_all_test_values("delete_user_with_outstanding_buys_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # buy one and settle it
        buyer_test_values = get_all_test_values("delete_user_with_outstanding_buys_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # buy another so it is in escrow
        amount_to_buy = 20
        bid_price = 11.5
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        response = delete_user(buyer_test_values['request'], buyer_test_values['user_id'])

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "Outstanding asks or buys so cannot delete user", status_code=400)

    def test_delete_user_with_non_existent_user(self):
        test_values = get_all_test_values("delete_user_with_non_existent_user")

        response = delete_user(test_values['request'], "2c19381a-ce88-49a0-bace-e27d57e2a5d0")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "No user with id", status_code=400)

    def test_delete_user_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("delete_user_invalid_params_returns_all_invalid_params")

        response = delete_user(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_get_ask_with_identifier_with_existing_ask(self):
        test_values = get_all_test_values("get_ask_with_identifier_with_existing_ask")

        create_ask_response = make_denarii_ask(test_values['request'], test_values['user_id'], 10, 1.0)

        self.assertNotEqual(type(create_ask_response), HttpResponseBadRequest)

        fields = get_json_fields(create_ask_response)

        response = get_ask_with_identifier(test_values['request'], test_values['user_id'], fields['ask_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        new_fields = get_json_fields(response)

        self.assertEqual(fields['ask_id'], new_fields['ask_id'])

    def test_get_ask_with_identifier_with_non_existent_ask(self):
        test_values = get_all_test_values("get_ask_with_identifier_with_non_existent_ask")

        response = get_ask_with_identifier(test_values['request'], test_values['user_id'],
                                           "8c9d49f5-096a-45a3-b29a-0294d447bb64")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, "No ask with id", status_code=400)

    def test_get_ask_with_identifier_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_ask_with_identifier_invalid_params_returns_all_invalid_params")

        response = get_ask_with_identifier(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_transfer_denarii_back_to_seller_with_exactly_amount(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_exactly_amount_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_exactly_amount_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii_back_to_seller(buyer_test_values['request'], buyer_test_values['user_id'],
                                                            ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        ask = get_ask_with_id(ask_id)

        self.assertFalse(ask.is_settled)
        self.assertFalse(ask.in_escrow)
        self.assertEqual(ask.amount_bought, 0)
        self.assertFalse(ask.has_been_seen_by_seller)

    def test_transfer_denarii_back_to_seller_with_less_than_amount(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_less_than_amount_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_less_than_amount_buyer")

        # Buy exactly half of one of the sellers lowest prices asks
        amount_to_buy = 1.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == 2.0 and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii_back_to_seller(buyer_test_values['request'], buyer_test_values['user_id'],
                                                            ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        ask = get_ask_with_id(ask_id)

        self.assertFalse(ask.is_settled)
        self.assertFalse(ask.in_escrow)
        self.assertEqual(ask.amount_bought, 0)
        self.assertFalse(ask.has_been_seen_by_seller)

    def test_transfer_denarii_back_to_seller_with_ask_that_doesnt_exist(self):
        # We create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_ask_that_doesnt_exist_buyer")

        transfer_response = transfer_denarii_back_to_seller(buyer_test_values['request'], buyer_test_values['user_id'],
                                                            "92a5e976-21b5-4f6e-8e8d-058b4029a8a8")

        self.assertEqual(type(transfer_response), HttpResponseBadRequest)

        self.assertContains(transfer_response, "No ask with id", status_code=400)

    def test_transfer_denarii_back_to_seller_with_ask_not_in_escrow(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_ask_not_in_escrow_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_ask_not_in_escrow_buyer")

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == 2.0 and ask.asking_price == 10.0:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii_back_to_seller(buyer_test_values['request'], buyer_test_values['user_id'],
                                                            ask_id)

        self.assertEqual(type(transfer_response), HttpResponseBadRequest)

        self.assertContains(transfer_response, "Ask was not bought", status_code=400)

    def test_transfer_denarii_back_to_seller_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values(
            "transfer_denarii_back_to_seller_invalid_params_returns_all_invalid_params")

        response = transfer_denarii_back_to_seller(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_send_money_back_to_buyer(self):
        test_values = get_all_test_values("send_money_back_to_buyer")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_back_to_buyer_fail_payout_create(self):
        test_values = get_all_test_values("send_money_back_to_buyer_fail_payout_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "123-456-7890", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "101", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

        self.assertContains(send_response, "Could not create payout", status_code=400)

    def test_send_money_back_to_buyer_without_credit_card(self):
        test_values = get_all_test_values("send_money_back_to_buyer_without_credit_card")

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

        self.assertContains(send_response, "No credit card for user", status_code=400)

    def test_send_money_back_to_buyer_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("send_money_back_to_buyer_invalid_params_returns_all_invalid_params")

        response = send_money_back_to_buyer(dict_of_values['request'], "123", "A", "1")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.amount, status_code=400)
        self.assertContains(response, Params.currency, status_code=400)

    def test_cancel_buy_of_ask(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("cancel_buy_of_ask_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("cancel_buy_of_ask_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        cancel_response = cancel_buy_of_ask(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(cancel_response), HttpResponseBadRequest)

        ask = get_ask_with_id(ask_id)

        self.assertFalse(ask.is_settled)
        self.assertFalse(ask.in_escrow)
        self.assertEqual(ask.amount_bought, 0)
        self.assertFalse(ask.has_been_seen_by_seller)

    def test_cancel_buy_of_ask_that_doesnt_exist(self):

        test_values = get_all_test_values("cancel_buy_of_ask_that_doesnt_exist")

        cancel_response = cancel_buy_of_ask(test_values['request'], test_values['user_id'],
                                            "3675a5e5-70cf-4e48-9c65-feac9dbc705f")

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

        self.assertContains(cancel_response, "No ask with id", status_code=400)

    def test_cancel_buy_of_ask_not_in_escrow(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("cancel_buy_of_ask_not_in_escrow_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("cancel_buy_of_ask_not_in_escrow_buyer")

        cancel_response = cancel_buy_of_ask(buyer_test_values['request'], buyer_test_values['user_id'], asks[0].ask_id)

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

        self.assertContains(cancel_response, "Ask is settled and not in escrow", status_code=400)

    def test_cancel_buy_of_ask_that_is_settled(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("cancel_buy_of_ask_that_is_settled_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("cancel_buy_of_ask_that_is_settled_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        ask = get_ask_with_id(ask_id)

        self.assertTrue(ask.is_settled)
        self.assertFalse(ask.has_been_seen_by_seller)

        cancel_response = cancel_buy_of_ask(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

        self.assertContains(cancel_response, "Ask is settled and not in escrow", status_code=400)

    def test_cancel_buy_of_ask_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("cancel_buy_of_ask_invalid_params_returns_all_invalid_params")

        response = cancel_buy_of_ask(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.ask_id, status_code=400)

    def test_verify_identity(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "firstname",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        fields = get_json_fields(verify_identity_response)

        self.assertEqual(fields['verification_status'], "is_verified")

    def test_verify_identity_but_cannot_create_candidate(self):
        test_values = get_all_test_values("verify_identity_but_cannot_create_candidate")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "failcandidate",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertEqual(type(verify_identity_response), HttpResponseBadRequest)

        self.assertContains(verify_identity_response, "Could not create candidate", status_code=400)

    def test_verify_identity_but_cannot_create_invitation(self):
        test_values = get_all_test_values("verify_identity_but_cannot_create_invitation")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "failinvitation",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertEqual(type(verify_identity_response), HttpResponseBadRequest)

        self.assertContains(verify_identity_response, "Could not create invitation", status_code=400)

    def test_verify_identity_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("verify_identity_invalid_params_returns_all_invalid_params")

        response = verify_identity(dict_of_values['request'], "123", "A", "2", "C", "D", "E", "F", "H", "G", "I")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.first_name, status_code=400)
        self.assertContains(response, Params.middle_name, status_code=400)
        self.assertContains(response, Params.last_name, status_code=400)
        self.assertContains(response, Params.email, status_code=400)
        self.assertContains(response, Params.dob, status_code=400)
        self.assertContains(response, Params.ssn, status_code=400)
        self.assertContains(response, Params.zipcode, status_code=400)
        self.assertContains(response, Params.phone, status_code=400)
        self.assertContains(response, Params.work_locations, status_code=400)

    def test_is_a_verified_person_where_identity_is_verified(self):
        test_values = get_all_test_values("is_a_verified_person_where_identity_is_verified")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "andrew",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(is_verified_response), HttpResponseBadRequest)

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "is_verified")

    def test_is_a_verified_person_where_identity_was_not_verified(self):
        test_values = get_all_test_values("is_a_verified_person_where_identity_was_not_verified")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "reportnotclear",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(is_verified_response), HttpResponseBadRequest)

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "failed_verification")

    def test_is_a_verified_person_where_identity_verification_is_pending(self):
        test_values = get_all_test_values("is_a_verified_person_where_identity_verification_is_pending")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "reportpending",
                                                   "m", "lastname", "email@email.com", "1999/02/07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(is_verified_response), HttpResponseBadRequest)

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "verification_pending")

    def test_is_a_verified_person_with_no_verification_pending_at_all(self):
        test_values = get_all_test_values("is_a_verified_person_with_no_verification_pending_at_all")

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(is_verified_response), HttpResponseBadRequest)

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "is_not_verified")

    def test_is_a_verified_person_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("is_a_verified_person_invalid_params_returns_all_invalid_params")

        response = is_a_verified_person(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_get_all_asks(self):
        seller_test_values = get_all_test_values("get_all_asks_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # buy one and settle it
        buyer_test_values = get_all_test_values("get_all_asks_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # buy another so it is in escrow
        amount_to_buy = 20
        bid_price = 11.5
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 34.0 to offer (at 11.5 price)
        second_ask_id = 0
        for ask in asks:
            # we only bought part of it
            if ask.amount == 34.0 and ask.asking_price == bid_price:
                second_ask_id = ask.ask_id
                break

        response = get_all_asks(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, "ask_id")

        for ask in asks:

            if ask.ask_id == first_ask_id or ask.ask_id == second_ask_id:
                self.assertNotIn(ask.ask_id, ask_ids)
            else:
                self.assertIn(ask.ask_id, ask_ids)

    def test_get_all_asks_with_no_asks(self):
        test_values = get_all_test_values("get_all_asks_with_no_asks")

        response = get_all_asks(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, "ask_id")

        self.assertEqual(len(ask_ids), 0)

    def test_get_all_asks_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_all_asks_invalid_params_returns_all_invalid_params")

        response = get_all_asks(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_get_all_buys(self):
        seller_test_values = get_all_test_values("get_all_buys_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # buy one and settle it
        buyer_test_values = get_all_test_values("get_all_buys_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # buy another so it is in escrow
        amount_to_buy = 20
        bid_price = 11.5
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 34.0 to offer (at 11.5 price)
        second_ask_id = 0
        for ask in asks:
            # we only bought part of it
            if ask.amount == 34.0 and ask.asking_price == bid_price:
                second_ask_id = ask.ask_id
                break

        response = get_all_buys(buyer_test_values['request'], buyer_test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, "ask_id")

        for ask in asks:

            if ask.ask_id == first_ask_id:
                self.assertNotIn(ask.ask_id, ask_ids)
            elif ask.ask_id == second_ask_id:
                self.assertIn(ask.ask_id, ask_ids)
            else:
                self.assertNotIn(ask.ask_id, ask_ids)

    def test_get_all_buys_with_no_buys(self):
        test_values = get_all_test_values("get_all_buys_with_no_buys")

        response = get_all_buys(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(response, "ask_id")

        self.assertEqual(len(ask_ids), 0)

    def test_get_all_buys_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_all_buys_invalid_params_returns_all_invalid_params")

        response = get_all_buys(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_create_support_ticket(self):
        test_values = get_all_test_values("create_support_ticket")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

    def test_create_support_ticket_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("create_support_ticket_invalid_params_returns_all_invalid_params")

        response = create_support_ticket(dict_of_values['request'], "123", "A", "2")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.title, status_code=400)
        self.assertContains(response, Params.description, status_code=400)

    def test_update_support_ticket(self):
        test_values = get_all_test_values("update_support_ticket")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        update_response = update_support_ticket(test_values['request'], test_values['user_id'],
                                                fields['support_ticket_id'], "update comment")

        self.assertNotEqual(type(update_response), HttpResponseBadRequest)

    def test_update_non_existent_support_ticket(self):
        test_values = get_all_test_values("update_non_existent_support_ticket")

        update_response = update_support_ticket(test_values['request'], test_values['user_id'],
                                                "8a99c23a-a0fd-4542-91a3-ff1aebc413f7", "update comment")

        self.assertEqual(type(update_response), HttpResponseBadRequest)

        self.assertContains(update_response, "No support ticket with id", status_code=400)

    def test_update_support_ticket_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("update_support_ticket_invalid_params_returns_all_invalid_params")

        response = update_support_ticket(dict_of_values['request'], "123", "A", "2")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.support_ticket_id, status_code=400)
        self.assertContains(response, Params.comment, status_code=400)

    def test_delete_support_ticket(self):
        test_values = get_all_test_values("delete_support_ticket")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        delete_response = delete_support_ticket(test_values['request'], test_values['user_id'],
                                                fields['support_ticket_id'])

        self.assertNotEqual(type(delete_response), HttpResponseBadRequest)

    def test_delete_non_existent_support_ticket(self):
        test_values = get_all_test_values("delete_non_existent_support_ticket")

        delete_response = delete_support_ticket(test_values['request'], test_values['user_id'],
                                                "98756ed1-e1b0-41a6-abd7-5e3a675ea5c0")

        self.assertEqual(type(delete_response), HttpResponseBadRequest)

        self.assertContains(delete_response, "No support ticket with id", status_code=400)

    def test_delete_support_ticket_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("delete_support_ticket_invalid_params_returns_all_invalid_params")

        response = delete_support_ticket(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.support_ticket_id, status_code=400)

    def test_get_all_tickets(self):
        test_values = get_all_test_values("get_all_tickets")

        response_one = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response_one), HttpResponseBadRequest)

        fields = get_json_fields(response_one)

        response_two = create_support_ticket(test_values['request'], test_values['user_id'], "Other Title",
                                             "other description")

        self.assertNotEqual(type(response_two), HttpResponseBadRequest)

        fields_two = get_json_fields(response_two)

        get_all_response = get_support_tickets(test_values['request'], test_values['user_id'], "True")

        self.assertNotEqual(type(get_all_response), HttpResponseBadRequest)

        ticket_ids = get_all_json_objects_field(get_all_response, 'support_ticket_id')

        self.assertIn(fields['support_ticket_id'], ticket_ids)
        self.assertIn(fields_two['support_ticket_id'], ticket_ids)

    def test_get_all_non_resolved_tickets(self):
        test_values = get_all_test_values("get_all_non_resolved_tickets")

        response_one = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response_one), HttpResponseBadRequest)

        fields = get_json_fields(response_one)

        response_two = create_support_ticket(test_values['request'], test_values['user_id'], "Other Title",
                                             "other description")

        self.assertNotEqual(type(response_two), HttpResponseBadRequest)

        fields_two = get_json_fields(response_two)

        # Resolve the second ticket
        resolve_response = resolve_support_ticket(test_values['request'], test_values['user_id'],
                                                  fields_two['support_ticket_id'])

        self.assertNotEqual(type(resolve_response), HttpResponseBadRequest)

        get_all_response = get_support_tickets(test_values['request'], test_values['user_id'], "False")

        self.assertNotEqual(type(get_all_response), HttpResponseBadRequest)

        ticket_ids = get_all_json_objects_field(get_all_response, 'support_ticket_id')

        self.assertIn(fields['support_ticket_id'], ticket_ids)
        self.assertNotIn(fields_two['support_ticket_id'], ticket_ids)

    def test_get_all_tickets_when_there_are_none(self):
        test_values = get_all_test_values("get_all_tickets_when_there_are_none")

        get_all_response = get_support_tickets(test_values['request'], test_values['user_id'], "True")

        self.assertNotEqual(type(get_all_response), HttpResponseBadRequest)

        ticket_ids = get_all_json_objects_field(get_all_response, 'support_ticket_id')

        self.assertEqual(len(ticket_ids), 0)

    def test_get_all_tickets_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_all_tickets_invalid_params_returns_all_invalid_params")

        response = get_support_tickets(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.can_be_resolved, status_code=400)

    def test_get_support_ticket_comments(self):
        test_values = get_all_test_values("get_support_ticket_comments")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        response_fields = get_json_fields(response)

        comment_response_one = update_support_ticket(test_values['request'], test_values['user_id'],
                                                     response_fields['support_ticket_id'], "some_comment")

        self.assertNotEqual(type(comment_response_one), HttpResponseBadRequest)

        comment_response_two = update_support_ticket(test_values['request'], test_values['user_id'],
                                                     response_fields['support_ticket_id'], "other_comment")

        self.assertNotEqual(type(comment_response_two), HttpResponseBadRequest)

        get_comments_response = get_comments_on_ticket(test_values['request'], test_values['user_id'],
                                                       response_fields['support_ticket_id'])

        self.assertNotEqual(type(get_comments_response), HttpResponseBadRequest)

        contents = get_all_json_objects_field(get_comments_response, 'content')

        self.assertIn("some_comment", contents)
        self.assertIn("other_comment", contents)

    def test_get_support_ticket_comments_when_there_are_none(self):
        test_values = get_all_test_values("get_support_ticket_comments_when_there_are_none")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        response_fields = get_json_fields(response)

        get_comments_response = get_comments_on_ticket(test_values['request'], test_values['user_id'],
                                                       response_fields['support_ticket_id'])

        self.assertNotEqual(type(get_comments_response), HttpResponseBadRequest)

        support_ticket_ids = get_all_json_objects_field(get_comments_response, 'support_ticket_id')

        self.assertEqual(len(support_ticket_ids), 0)

    def test_get_support_ticket_comments_for_non_existent_support_ticket(self):
        test_values = get_all_test_values("get_support_ticket_comments_for_non_existent_support_ticket")

        get_comments_response = get_comments_on_ticket(test_values['request'], test_values['user_id'],
                                                       "3aae1872-1b53-42ce-9bb9-0a6079760017")

        self.assertEqual(type(get_comments_response), HttpResponseBadRequest)

        self.assertContains(get_comments_response, "No support ticket with id", status_code=400)

    def test_get_support_ticket_comments_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_support_ticket_comments_invalid_params_returns_all_invalid_params")

        response = get_comments_on_ticket(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.support_ticket_id, status_code=400)

    def test_resolve_support_ticket(self):
        test_values = get_all_test_values("resolve_support_ticket")

        response = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        resolve_response = resolve_support_ticket(test_values['request'], test_values['user_id'],
                                                  fields['support_ticket_id'])
        self.assertNotEqual(type(resolve_response), HttpResponseBadRequest)

    def test_resolve_non_existent_support_ticket(self):
        test_values = get_all_test_values("resolve_non_existent_support_ticket")

        resolve_response = resolve_support_ticket(test_values['request'], test_values['user_id'],
                                                  "5d179258-e7b6-4567-b084-55f12b895fa6")
        self.assertEqual(type(resolve_response), HttpResponseBadRequest)

        self.assertContains(resolve_response, "No support ticket with id", status_code=400)

    def test_resolve_ticket_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("resolve_ticket_invalid_params_returns_all_invalid_params")

        response = resolve_support_ticket(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.support_ticket_id, status_code=400)

    def test_poll_for_escrowed_transaction(self):
        seller_test_values = get_all_test_values("poll_for_escrowed_transaction_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        buyer_test_values = get_all_test_values("poll_for_escrowed_transaction_buyer")

        # Buy exactly one of the sellers lowest prices asks
        amount_to_buy = 2.0
        bid_price = 10.0
        buy_response = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy, bid_price,
                                   "False", "False")

        self.assertNotEqual(type(buy_response), HttpResponseBadRequest)

        # We want the ask id of the ask with 2.0 to offer (at 10.0 price)
        first_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                first_ask_id = ask.ask_id
                break

        self.assertNotEqual(first_ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], first_ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        # We want to buy (but not transfer) an ask with 34.0 at 11.5 price
        amount_to_buy_two = 34.0
        bid_price_two = 11.5
        buy_response_two = buy_denarii(buyer_test_values['request'], buyer_test_values['user_id'], amount_to_buy_two,
                                       bid_price_two,
                                       "False", "False")

        self.assertNotEqual(type(buy_response_two), HttpResponseBadRequest)

        # We want the ask id of the ask with 34.0 to offer (at 11.5 price)
        second_ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy_two and ask.asking_price == bid_price_two:
                second_ask_id = ask.ask_id
                break

        self.assertNotEqual(second_ask_id, 0)

        poll_response = poll_for_escrowed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(poll_response, 'ask_id')

        self.assertNotEqual(len(asks), len(ask_ids))

        for ask_id in ask_ids:
            self.assertFalse(get_ask_with_id(ask_id).is_settled)
            self.assertTrue(get_ask_with_id(ask_id).in_escrow)

    def test_poll_for_escrowed_transaction_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("poll_for_escrowed_transaction_invalid_params_returns_all_invalid_params")

        response = poll_for_escrowed_transaction(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)

    def test_get_support_ticket(self):

        test_values = get_all_test_values("get_support_ticket")

        response_one = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response_one), HttpResponseBadRequest)

        fields = get_json_fields(response_one)

        response_two = create_support_ticket(test_values['request'], test_values['user_id'], "Other Title",
                                             "other description")

        self.assertNotEqual(type(response_two), HttpResponseBadRequest)

        fields_two = get_json_fields(response_two)

        get_all_response = get_support_ticket(test_values['request'], test_values['user_id'],
                                              fields['support_ticket_id'])

        self.assertNotEqual(type(get_all_response), HttpResponseBadRequest)

        ticket_ids = get_all_json_objects_field(get_all_response, 'support_ticket_id')

        self.assertIn(fields['support_ticket_id'], ticket_ids)
        self.assertNotIn(fields_two['support_ticket_id'], ticket_ids)

    def test_get_support_ticket_does_not_exist(self):
        test_values = get_all_test_values("get_support_ticket_does_not_exist")

        response_one = create_support_ticket(test_values['request'], test_values['user_id'], "Title", "description")

        self.assertNotEqual(type(response_one), HttpResponseBadRequest)

        response_two = create_support_ticket(test_values['request'], test_values['user_id'], "Other Title",
                                             "other description")

        self.assertNotEqual(type(response_two), HttpResponseBadRequest)

        get_all_response = get_support_ticket(test_values['request'], test_values['user_id'],
                                              "a095b1c7-b230-4e56-91ac-ed3052bacf90")

        self.assertEqual(type(get_all_response), HttpResponseBadRequest)

        self.assertContains(get_all_response, "No support ticket with id", status_code=400)

    def test_get_support_ticket_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("get_support_ticket_invalid_params_returns_all_invalid_params")

        response = get_support_ticket(dict_of_values['request'], "123", "A")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
        self.assertContains(response, Params.support_ticket_id, status_code=400)

    def test_logout_user_logs_them_out(self):
        test_values = get_all_test_values("logout_user_logs_them_out")

        response_one = logout_user(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(response_one), HttpResponseBadRequest)

        response_two = create_support_ticket(test_values['request'], test_values['user_id'], "Other Title",
                                             "other description")

        self.assertEqual(type(response_two), HttpResponseRedirect)

    def test_logout_invalid_params_returns_all_invalid_params(self):
        dict_of_values = get_all_test_values("logout_invalid_params_returns_all_invalid_params")

        response = logout_user(dict_of_values['request'], "123")

        self.assertEqual(type(response), HttpResponseBadRequest)

        self.assertContains(response, Params.user_id, status_code=400)
