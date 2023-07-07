import uuid

from django.contrib.auth.models import AbstractUser
from django.db import models


def get_default_denarii_user():
    return DenariiUser(username="test", email="test@test.com")


class DenariiUser(AbstractUser):
    reset_id = models.IntegerField(default=0)

    def __str__(self):
        if self.username is None or self.email is None:
            return 'generic_user::user@email.com'
        return f"{self.username}::{self.email}"


class CreditCard(models.Model):
    customer_id = models.TextField(null=True)
    source_token_id = models.TextField(null=True)
    denarii_user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    primary_key = models.UUIDField(primary_key=True,
                                   default=uuid.uuid4,
                                   editable=False)


class Response(models.Model):
    has_credit_card_info = models.BooleanField(null=True)
    user_identifier = models.TextField(null=True)
    seed = models.TextField(null=True)
    wallet_address = models.TextField(null=True)
    balance = models.FloatField(null=True)
    ask_id = models.TextField(null=True)
    amount = models.FloatField(null=True)
    asking_price = models.FloatField(null=True)
    amount_bought = models.FloatField(null=True)


class WalletDetails(models.Model):
    wallet_name = models.TextField(null=True)
    wallet_password = models.TextField(null=True)
    seed = models.TextField(null=True)
    balance = models.FloatField(null=True)
    wallet_address = models.TextField(null=True)
    denarii_user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    primary_key = models.UUIDField(primary_key=True,
                                   default=uuid.uuid4,
                                   editable=False)

    def __str__(self):
        if self.wallet_name is None:
            return 'generic_wallet'
        return self.wallet_name


class DenariiAsk(models.Model):
    primary_key = models.UUIDField(primary_key=True,
                                   default=uuid.uuid4,
                                   editable=False)
    denarii_user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    ask_id = models.TextField(null=True)
    amount = models.FloatField(null=True)
    asking_price = models.FloatField(null=True)
    in_escrow = models.BooleanField(null=True)
    amount_bought = models.FloatField(null=True)

    def __str__(self):
        return self.ask_id
