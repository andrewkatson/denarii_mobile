# Generated by Django 4.1.3 on 2022-12-02 15:18

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('DenariiMobile', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='walletdetails',
            name='test',
            field=models.IntegerField(default=0),
        ),
    ]
