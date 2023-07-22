import datetime
import uuid

from django.contrib.auth.models import AbstractUser
from django.db import models


def get_default_denarii_user():
    user, created = DenariiUser.objects.get_or_create(username="Test", email="test@email.com")
    return user.id


def get_default_support_ticket():
    ticket, created = SupportTicket.objects.get_or_create(title="Title", description="description")
    return ticket.primary_key


class DenariiUser(AbstractUser):
    reset_id = models.IntegerField(default=0)
    report_id = models.TextField(null=True)
    identity_is_verified = models.BooleanField(null=True)
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)

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
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)


class SupportTicket(models.Model):
    primary_key = models.UUIDField(primary_key=True,
                                   default=uuid.uuid4,
                                   editable=False)
    support_id = models.TextField(null=True)
    description = models.TextField(null=True)
    title = models.TextField(null=True)
    denarii_user = models.ForeignKey(DenariiUser, on_delete=models.CASCADE, default=get_default_denarii_user)
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)


class SupportTicketComment(models.Model):
    primary_key = models.UUIDField(primary_key=True,
                                   default=uuid.uuid4,
                                   editable=False)
    support_ticket = models.ForeignKey(SupportTicket, on_delete=models.CASCADE, default=get_default_support_ticket)
    # Same as username of a DenariiUser
    author = models.TextField(null=True)
    content = models.TextField(null=True)
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)


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
    transaction_was_settled = models.BooleanField(null=True)
    verification_status = models.TextField(null=True)
    author = models.TextField(null=True)
    content = models.TextField(null=True)
    description = models.TextField(null=True)
    title = models.TextField(null=True)
    support_ticket_id = models.TextField(null=True)

    # Creation and update times of the message being sent back (i.e. a comment on a support ticket)
    creation_time_body = models.DateTimeField(null=True, blank=True)
    updated_time_body = models.DateTimeField(null=True, blank=True)

    # Creation and update times of the response
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)


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
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)

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
    is_settled = models.BooleanField(null=True)
    has_been_seen_by_seller = models.BooleanField(null=True)
    creation_time = models.DateTimeField(auto_now_add=True, null=True, blank=True)
    updated_time = models.DateTimeField(auto_now=True, null=True, blank=True)

    def __str__(self):
        return self.ask_id
