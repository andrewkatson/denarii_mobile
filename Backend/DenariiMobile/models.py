import uuid

from django.contrib.auth.models import AbstractUser
from django.db import models


def get_default_denarii_user():
    return DenariiUser(username="test", email="test@test.com")


class DenariiUser(AbstractUser):
    reset_id = models.IntegerField(default=0)

    def __str__(self):
        return f"{self.username}::{self.email}"


class WalletDetails(models.Model):
    wallet_name = models.TextField(null=True)
    wallet_password = models.TextField(null=True)
    seed = models.TextField(null=True)
    balance = models.FloatField(null=True)
    wallet_address = models.TextField(null=True)
    user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    user_identifier = models.UUIDField(primary_key=True, default=uuid.uuid5, editable=False)

    def __str__(self):
        return self.wallet_name


class DenariiAsk(models.Model):
    ask_id = models.UUIDField(primary_key=True, default=uuid.uuid5, editable=False)
    user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    amount = models.FloatField(null=True)
    asking_price = models.FloatField(null=True)
    in_escrow = models.BooleanField(null=True)
    amount_bought = models.FloatField(null=True)

    def __str__(self):
        return self.ask_id
