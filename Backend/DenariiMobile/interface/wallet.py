class Wallet:

    def __init__(self, name, password, phrase='', address='', sub_addresses=None, balance=0.0):
        if sub_addresses is None:
            sub_addresses = []
        self.name = name
        self.password = password
        self.phrase = phrase
        self.address = address
        self.sub_addresses = sub_addresses
        self.balance = balance
