# Generated by Django 4.1.3 on 2023-09-05 19:04

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0015_supportticket_alter_denariiuser_options_and_more'),
    ]

    operations = [
        migrations.AddField(
            model_name='denariiask',
            name='buyer',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.SET_NULL, related_name='denarii_ask_buyer', to=settings.AUTH_USER_MODEL),
        ),
    ]
