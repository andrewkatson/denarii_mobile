from django.db import models


class WalletDetails(models.Model):
    user_id = models.TextField()
    wallet_name = models.TextField()
    wallet_password = models.TextField()
    seed = models.TextField()
    balance = models.FloatField()
    wallet_address = models.TextField()

    def __str__(self):
        return self.wallet_name
