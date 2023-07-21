import random

import pandas as pd

from django.http import HttpRequest
from django.test import TestCase

from DenariiMobile.views import *
from DenariiMobile.interface.ask import *


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
        for _, content in df['fields'].items():
            field_values.append(content[field])
    return field_values


def create_user(name, email, password):
    response = get_user_id(None, name, email, password)

    fields = get_json_fields(response)
    return fields['user_identifier']


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

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        new_user = get_user(self.user, self.email, password)
        new_wallet_fields = get_json_fields(response)
        self.assertEqual(new_wallet_fields['user_identifier'], str(new_user.id))

    def test_get_user_id_old_user_returns_existing_wallet(self):
        password = "other_password"
        _ = get_user_id(None, self.user, self.email, password)
        new_user = get_user(self.user, self.email, password)

        response = get_user_id(None, self.user, self.email, password)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        wallet_fields = get_json_fields(response)
        self.assertEqual(wallet_fields['user_identifier'], str(new_user.id))

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

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_open_wallet_attempts_to_open_wallet(self):
        dict_of_values = get_all_test_values("open_wallet")

        response = open_wallet(dict_of_values['request'], dict_of_values['user_id'],
                               dict_of_values['wallet_name'], dict_of_values['wallet_password'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertTrue('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_restore_wallet_attempts_to_restore_wallet(self):
        dict_of_values = get_all_test_values("restore_wallet")

        response = restore_wallet(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                  dict_of_values['wallet_password'], self.seed)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertFalse('seed' in fields)
        self.assertTrue('wallet_address' in fields)

    def test_get_balance_attempts_to_get_balance(self):
        dict_of_values = get_all_test_values("get_balance")

        response = get_balance(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertEquals(fields['balance'], 2.0)

    def test_send_denarii_attempts_to_send_denarii(self):
        dict_of_values = get_all_test_values("send_denarii")

        response = send_denarii(dict_of_values['request'], dict_of_values['user_id'], dict_of_values['wallet_name'],
                                self.other_address, self.amount_to_send)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        fields = get_json_fields(response)

        self.assertFalse("balance" in fields)

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

    def test_buy_denarii_considering_price_with_zero_matches(self):
        test_values = get_all_test_values("buy_denarii_considering_price_with_zero_matches")

        asks = create_asks(test_values['request'], test_values['user_id'])
        asks.sort(key=lambda x: x.asking_price)

        amount_to_buy = 10000.0
        response = buy_denarii(test_values['request'], test_values['user_id'], amount_to_buy, 0.0, "False", "True")

        self.assertEqual(type(response), HttpResponseBadRequest)

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

        response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], -1)

        self.assertEqual(type(response), HttpResponseBadRequest)

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

    def test_make_denarii_ask(self):
        test_values = get_all_test_values('make_denarii_ask')

        response = make_denarii_ask(test_values['request'], test_values['user_id'], 1.0, 10.0)

        self.assertNotEqual(type(response), HttpResponseBadRequest)

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
        ask_id = 0
        for ask in asks:
            if ask.amount == amount_to_buy and ask.asking_price == bid_price:
                ask_id = ask.ask_id
                break

        self.assertNotEqual(ask_id, 0)

        transfer_response = transfer_denarii(buyer_test_values['request'], buyer_test_values['user_id'], ask_id)

        self.assertNotEqual(type(transfer_response), HttpResponseBadRequest)

        poll_response = poll_for_completed_transaction(seller_test_values['request'], seller_test_values['user_id'])

        self.assertNotEqual(type(poll_response), HttpResponseBadRequest)

        ask_ids = get_all_json_objects_field(poll_response, 'ask_id')

        self.assertNotEqual(len(asks), len(ask_ids))

        for ask_id in ask_ids:
            self.assertTrue(get_ask_with_id(ask_id).is_settled)

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
        response = cancel_ask(test_values['request'], test_values['user_id'], -1)

        self.assertEqual(type(response), HttpResponseBadRequest)

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

    def test_has_credit_card_info_with_info(self):
        test_values = get_all_test_values("has_credit_card_info_with_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
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

    def test_set_credit_card_info(self):
        test_values = get_all_test_values("set_credit_card_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

    def test_set_credit_card_info_fail_token_create(self):
        test_values = get_all_test_values("set_credit_card_info_fail_token_create")

        # A -1 card number will cause token creation to fail
        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "-1", "2", "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

    def test_set_credit_card_info_fail_customer_create(self):
        # The prefix will cause a failure to create a customer
        test_values = get_all_test_values("fail")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

    def test_set_credit_card_info_fail_setup_intent_create(self):
        # The prefix will cause a failure to create a setup intent
        test_values = get_all_test_values("fail_setup_intent")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertEqual(type(set_response), HttpResponseBadRequest)

    def test_set_credit_card_info_with_info_already_set(self):
        test_values = get_all_test_values("set_credit_card_info_with_info_already_set")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        set_response_two = set_credit_card_info(test_values['request'], test_values['user_id'], "other_card_number",
                                                "3",
                                                "2002", "456")

        self.assertEqual(type(set_response_two), HttpResponseBadRequest)

    def test_clear_credit_card_info(self):
        test_values = get_all_test_values("clear_credit_card_info")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(clear_response), HttpResponseBadRequest)

    def test_clear_credit_card_info_fail_customer_delete(self):
        # This prefix causes the customer deletion to fail
        test_values = get_all_test_values("fail_delete")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertEqual(type(clear_response), HttpResponseBadRequest)

    def test_clear_credit_card_info_without_credit_card(self):
        test_values = get_all_test_values("clear_credit_card_info_without_credit_card")

        clear_response = clear_credit_card_info(test_values['request'], test_values['user_id'])

        self.assertEqual(type(clear_response), HttpResponseBadRequest)

    def test_get_money_from_buyer(self):
        test_values = get_all_test_values("get_money_from_buyer")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(get_money_response), HttpResponseBadRequest)

    def test_get_money_from_buyer_fail_payment_intent_create(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # -3 amount will cause a payment intent creation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "-3", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

    def test_get_money_from_buyer_fail_payment_intent_confirm(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_confirm")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # -2 amount will cause a payment intent confirmation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "-2", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

    def test_get_money_from_buyer_fail_payment_intent_cancel(self):
        test_values = get_all_test_values("get_money_from_buyer_fail_payment_intent_cancel")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # -1 amount will cause a payment intent cancellation failure
        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "-1", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

    def test_get_money_from_buyer_without_credit_card(self):
        test_values = get_all_test_values("get_money_from_buyer_without_credit_card")

        get_money_response = get_money_from_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertEqual(type(get_money_response), HttpResponseBadRequest)

    def test_send_money_to_seller(self):
        test_values = get_all_test_values("send_money_to_seller")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_to_seller_fail_payout_create(self):
        test_values = get_all_test_values("send_money_to_seller_fail_payout_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        # -1 amount will cause a payout creation failure
        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "-1", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_to_seller_without_credit_card(self):
        test_values = get_all_test_values("send_money_to_seller_without_credit_card")

        send_response = send_money_to_seller(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertEqual(type(send_response), HttpResponseBadRequest)

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

    def test_delete_user_with_existing_user(self):
        test_values = get_all_test_values("delete_user_with_existing_user")

        response = delete_user(test_values['request'], test_values['user_id'])

        self.assertNotEqual(type(response), HttpResponseBadRequest)

        self.assertIsNone(get_user_with_id(test_values['user_id']))

    def test_delete_user_with_non_existent_user(self):
        test_values = get_all_test_values("delete_user_with_non_existent_user")

        response = delete_user(test_values['request'], -1)

        self.assertEqual(type(response), HttpResponseBadRequest)

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

        response = get_ask_with_identifier(test_values['request'], test_values['user_id'], -1)

        self.assertEqual(type(response), HttpResponseBadRequest)

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
                                                            -1)

        self.assertEqual(type(transfer_response), HttpResponseBadRequest)

    def test_transfer_denarii_back_to_seller_with_ask_not_in_escrow(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_ask_not_in_escrow_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("transfer_denarii_back_to_seller_with_ask_not_in_escrow_buyer")

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

        self.assertEqual(type(transfer_response), HttpResponseBadRequest)

    def test_send_money_back_to_buyer(self):
        test_values = get_all_test_values("send_money_back_to_buyer")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_back_to_buyer_fail_payout_create(self):
        test_values = get_all_test_values("send_money_back_to_buyer_fail_payout_create")

        set_response = set_credit_card_info(test_values['request'], test_values['user_id'], "card_number", "2", "1997",
                                            "123")

        self.assertNotEqual(type(set_response), HttpResponseBadRequest)

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "-1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

    def test_send_money_back_to_buyer_without_credit_card(self):
        test_values = get_all_test_values("send_money_back_to_buyer_without_credit_card")

        send_response = send_money_back_to_buyer(test_values['request'], test_values['user_id'], "1", "usd")

        self.assertNotEqual(type(send_response), HttpResponseBadRequest)

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

        cancel_response = cancel_buy_of_ask(test_values['request'], test_values['user_id'], -1)

        self.assertNotEqual(type(cancel_response), HttpResponseBadRequest)

    def test_cancel_buy_of_ask_not_in_escrow(self):
        # First we create the seller and some asks
        seller_test_values = get_all_test_values("cancel_buy_of_ask_not_in_escrow_seller")

        asks = create_asks(seller_test_values['request'], seller_test_values['user_id'])

        # Then we create the buyer
        buyer_test_values = get_all_test_values("cancel_buy_of_ask_not_in_escrow_buyer")

        cancel_response = cancel_buy_of_ask(buyer_test_values['request'], buyer_test_values['user_id'], asks[0].ask_id)

        self.assertEqual(type(cancel_response), HttpResponseBadRequest)

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

    def test_verify_identity(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "first_name",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        fields = get_json_fields(verify_identity_response)

        self.assertEqual(fields['verification_status'], "is_verified")

    def test_verify_identity_but_cannot_create_candidate(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "fail_candidate",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertEqual(type(verify_identity_response), HttpResponseBadRequest)

    def test_verify_identity_but_cannot_create_invitation(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "fail_invitation",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertEqual(type(verify_identity_response), HttpResponseBadRequest)

    def test_is_a_verified_person_where_identity_is_verified(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "first_name",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "is_verified")

    def test_is_a_verified_person_where_identity_was_not_verified(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "report_not_clear",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "failed_verification")

    def test_is_a_verified_person_where_identity_verification_is_pending(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "report_pending",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "verification_pending")

    def test_is_a_verified_person_with_no_verification_pending_at_all(self):
        test_values = get_all_test_values("verify_identity")

        verify_identity_response = verify_identity(test_values['request'], test_values['user_id'], "fail_report",
                                                   "middle", "last_name", "email@email.com", "1999-02-07",
                                                   "123-45-2134", "22102", "2035408926", "[{\"country\":\"US\"}]")

        self.assertNotEqual(type(verify_identity_response), HttpResponseBadRequest)

        is_verified_response = is_a_verified_person(test_values['request'], test_values['user_id'])

        fields = get_json_fields(is_verified_response)

        self.assertEqual(fields['verification_status'], "is_not_verified")
