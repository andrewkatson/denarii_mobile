"""
Class that emulates a denarii_client for testing.
"""

import random
import requests
import string

word_site = "https://www.mit.edu/~ecprice/wordlist.10000"

response = requests.get(word_site)
WORDS = response.content.splitlines()


def generate_phrase(num_words):
    list_of_words = random.choices(WORDS, k=num_words)
    list_of_strings = [word.decode('utf8') for word in list_of_words]
    return ' '.join(list_of_strings)


def generate_address(num_letters):
    list_of_letters = random.choices(string.ascii_letters, k=num_letters)
    return ''.join(list_of_letters)


class Wallet:

    def __init__(self, name, password, seed=''):
        self.name = name
        self.password = password
        self.seed = seed
        self.address = ''
        # We make the balance 1 so that we can transfer some money.
        self.balance = 1.0


class DenariiClient:

    def __init__(self, wallets=None):
        if wallets is None:
            wallets = {}
        self.wallets = wallets

    def create_wallet(self, wallet):
        if wallet.name in self.wallets:
            return False

        self.wallets[wallet.name] = Wallet(wallet.name, wallet.password)
        return True

    def restore_wallet(self, wallet):
        if wallet.name in self.wallets:
            existing_wallet = self.wallets.get(wallet.name)
            if wallet.password == existing_wallet.password:
                return True
            return False
        return False

    def get_address(self, wallet):

        if wallet.name in self.wallets:
            wallet.address = self.wallets.get(wallet.name).address
            return True

        wallet.address = generate_address(15)
        existing_wallet = self.wallets.get(wallet.name)
        existing_wallet.address = wallet.address
        return True

    def transfer_money(self, amount, sender, receiver):
        if sender.name in self.wallets:
            existing_wallet = self.wallets.get(sender.name)

            if existing_wallet.balance >= amount:
                existing_wallet.balance -= amount
                return True
            return False
        return False

    def get_balance_of_wallet(self, wallet):
        if wallet.name in self.wallets:
            return self.wallets.get(wallet.name).balance
        return 0.0

    def set_current_wallet(self, wallet):
        if wallet.name in self.wallets:
            existing_wallet = self.wallets.get(wallet.name)
            if existing_wallet.password == wallet.password:
                return True
            return False
        return False

    def query_seed(self, wallet):
        if wallet.name in self.wallets:
            wallet.phrase = self.wallets.get(wallet.name).seed
            return True

        existing_wallet = self.wallets.get(wallet.name)
        wallet.phrase = generate_phrase(4)
        existing_wallet.seed = wallet.phrase
        return True
