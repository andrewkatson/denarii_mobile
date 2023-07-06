default_http_client = None


class StripeTestingClient:

    def __init__(self):
        pass


class CustomerClass:

    def __init__(self):
        self.description = None
        self.email = None
        self.name = None
        self.source = None
        self.customer_id = None

    def create(self, description, email, name, source):
        self.description = description
        self.email = email
        self.name = name
        self.source = source

        if name == "fail_user":
            raise Exception("Failed to create customer")
        if name == "fail_setup_intent":
            return {'id': "cus_fail_intent"}
        if name == "fail_delete":
            return {'id': "cus_fail_delete"}

        return {'id': "cus_something"}

    def delete(self, customer_id):
        self.customer_id = customer_id
        if customer_id == 'cus_fail_delete':
            raise Exception("Failed to delete customer")


Customer = CustomerClass()


class SetupIntentClass:

    def __init__(self):
        self.customer = None
        self.payment_method_types = None

    def create(self, payment_method_types, customer):
        self.customer = customer
        self.payment_method_types = payment_method_types

        if customer == "cus_fail_intent":
            raise Exception("Failed to create setup intent")


SetupIntent = SetupIntentClass()


class TokenClass:

    def __init__(self):
        self.card_number = None
        self.expiration_month = None
        self.expiration_year = None
        self.cvc = None

    def create(self, card):
        self.card_number = card['number']
        self.expiration_month = card['exp_month']
        self.expiration_year = card['exp_year']
        self.cvc = card['cvc']

        if self.card_number == 0:
            raise Exception("Failed to create credit card token")

        return {'id': "tok_something"}


Token = TokenClass()


class PaymentIntentClass:

    def __init__(self):
        self.amount = None
        self.currency = None
        self.automatic_payment_methods = None
        self.customer = None
        self.receipt_email = None
        self.payment_id = None
        self.payment_method = None
        self.cancellation_reason = None

    def create(self, amount, currency, automatic_payment_methods, customer, receipt_email):
        self.amount = amount
        self.currency = currency
        self.automatic_payment_methods = automatic_payment_methods
        self.customer = customer
        self.receipt_email = receipt_email

        if self.amount == -3:
            raise Exception("Failed to create payment intent")

        if self.amount == -2:
            return {'id': 'pi_fail_confirm'}

        if self.amount == -1:
            return {'id': 'pi_fail_cancel'}

        return {'id': "pi_something"}

    def confirm(self, payment_id, payment_method):
        self.payment_id = payment_id
        self.payment_method = payment_method

        if self.payment_id == 'pi_fail_confirm':
            raise Exception("Failed to confirm payment intent")

        return {'id': self.payment_id, 'next_action': None}

    def cancel(self, payment_id, cancellation_reason):
        assert payment_id == self.payment_id

        self.cancellation_reason = cancellation_reason

        if self.payment_id == 'pi_fail_cancel':
            raise Exception("Failed to cancel payment intent")

        return {'id': self.payment_id}


PaymentIntent = PaymentIntentClass()


class PayoutClass:

    def __init__(self):
        self.amount = None
        self.currency = None
        self.destination = None

    def create(self, amount, currency, destination):
        self.amount = amount
        self.currency = currency
        self.destination = destination

        if amount == -1:
            raise Exception("Failed to create payout")

        return {'id': "po_something"}


Payout = PayoutClass()
