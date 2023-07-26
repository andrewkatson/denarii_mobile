# Generated by Django 4.1.3 on 2023-06-27 02:00

import DenariiMobile.models
from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0001_initial'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='walletdetails',
            name='id',
        ),
        migrations.AlterField(
            model_name='walletdetails',
            name='user_identifier',
            field=models.UUIDField(default=uuid.uuid5, editable=False, primary_key=True, serialize=False),
        ),
        migrations.CreateModel(
            name='DenariiAsk',
            fields=[
                ('ask_id', models.UUIDField(default=uuid.uuid5, editable=False, primary_key=True, serialize=False)),
                ('amount', models.FloatField(null=True)),
                ('asking_price', models.FloatField(null=True)),
                ('in_escrow', models.BooleanField(null=True)),
                ('amount_bought', models.FloatField(null=True)),
                ('user', models.ForeignKey(default=DenariiMobile.models.DenariiUser.get_default_pk, on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
    ]
