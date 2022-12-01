from django.db import models


class User(models.Model):
    wallet_id = models.TextField()
    name = models.TextField()
    email = models.EmailField()

    def __str__(self):
        return f"{self.name}::{self.email}"
