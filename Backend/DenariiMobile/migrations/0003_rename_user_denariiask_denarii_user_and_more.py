# Generated by Django 4.1.3 on 2023-07-03 15:02

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0002_remove_walletdetails_id_and_more'),
    ]

    operations = [
        migrations.RenameField(
            model_name='denariiask',
            old_name='user',
            new_name='denarii_user',
        ),
        migrations.RenameField(
            model_name='walletdetails',
            old_name='user',
            new_name='denarii_user',
        ),
    ]
