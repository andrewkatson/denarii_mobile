# Generated by Django 4.1.3 on 2023-07-03 16:29

from django.db import migrations, models
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0009_rename_wallet_details_id_walletdetails_primary_key_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='denariiask',
            name='primary_key',
            field=models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False),
        ),
        migrations.AlterField(
            model_name='walletdetails',
            name='primary_key',
            field=models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False),
        ),
    ]
