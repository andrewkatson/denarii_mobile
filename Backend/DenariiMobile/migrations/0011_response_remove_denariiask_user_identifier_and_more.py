# Generated by Django 4.1.3 on 2023-07-06 08:22

import DenariiMobile.models
from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0010_alter_denariiask_primary_key_and_more'),
    ]

    operations = [
        migrations.CreateModel(
            name='Response',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('has_credit_card_info', models.BooleanField(null=True)),
                ('user_identifier', models.TextField(null=True)),
                ('seed', models.TextField(null=True)),
                ('wallet_address', models.TextField(null=True)),
                ('balance', models.FloatField(null=True)),
                ('ask_id', models.TextField(null=True)),
                ('amount', models.FloatField(null=True)),
                ('asking_price', models.FloatField(null=True)),
                ('amount_bought', models.FloatField(null=True)),
            ],
        ),
        migrations.RemoveField(
            model_name='denariiask',
            name='user_identifier',
        ),
        migrations.RemoveField(
            model_name='walletdetails',
            name='user_identifier',
        ),
        migrations.CreateModel(
            name='CreditCard',
            fields=[
                ('credit_card_number', models.TextField(null=True)),
                ('expiration_date_month', models.IntegerField(default=-1)),
                ('expiration_date_year', models.IntegerField(default=-1)),
                ('primary_key', models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False)),
                ('denarii_user', models.ForeignKey(default=DenariiMobile.models.get_default_denarii_user, on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
    ]