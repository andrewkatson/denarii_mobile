# Generated by Django 4.1.3 on 2023-07-22 22:05

import uuid

import django.db.models.deletion
from django.conf import settings
from django.db import migrations, models

import DenariiMobile.models


class Migration(migrations.Migration):
    dependencies = [
        ('DenariiMobile', '0014_denariiuser_identity_is_verified_and_more'),
    ]

    operations = [
        migrations.CreateModel(
            name='SupportTicket',
            fields=[
                (
                'primary_key', models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False)),
                ('support_id', models.TextField(null=True)),
                ('description', models.TextField(null=True)),
                ('title', models.TextField(null=True)),
                ('creation_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('updated_time', models.DateTimeField(auto_now=True, null=True)),
                ('resolved', models.BooleanField(default=False)),
            ],
        ),
        migrations.AlterModelOptions(
            name='denariiuser',
            options={},
        ),
        migrations.AddField(
            model_name='creditcard',
            name='creation_time',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='creditcard',
            name='updated_time',
            field=models.DateTimeField(auto_now=True, null=True),
        ),
        migrations.AddField(
            model_name='denariiask',
            name='creation_time',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='denariiask',
            name='updated_time',
            field=models.DateTimeField(auto_now=True, null=True),
        ),
        migrations.AddField(
            model_name='denariiuser',
            name='creation_time',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='denariiuser',
            name='updated_time',
            field=models.DateTimeField(auto_now=True, null=True),
        ),
        migrations.AddField(
            model_name='denariiuser',
            name='verification_report_status',
            field=models.TextField(default='never_run'),
        ),
        migrations.AddField(
            model_name='response',
            name='author',
            field=models.TextField(null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='content',
            field=models.TextField(null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='creation_time',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='creation_time_body',
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='description',
            field=models.TextField(null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='is_resolved',
            field=models.BooleanField(default=False),
        ),
        migrations.AddField(
            model_name='response',
            name='support_ticket_id',
            field=models.TextField(null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='title',
            field=models.TextField(null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='updated_time',
            field=models.DateTimeField(auto_now=True, null=True),
        ),
        migrations.AddField(
            model_name='response',
            name='updated_time_body',
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='walletdetails',
            name='creation_time',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='walletdetails',
            name='updated_time',
            field=models.DateTimeField(auto_now=True, null=True),
        ),
        migrations.AlterField(
            model_name='denariiask',
            name='has_been_seen_by_seller',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='denariiask',
            name='in_escrow',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='denariiask',
            name='is_settled',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='denariiuser',
            name='identity_is_verified',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='response',
            name='has_credit_card_info',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='response',
            name='transaction_was_settled',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterUniqueTogether(
            name='creditcard',
            unique_together={('customer_id', 'source_token_id')},
        ),
        migrations.AlterUniqueTogether(
            name='denariiuser',
            unique_together={('username', 'email')},
        ),
        migrations.AlterUniqueTogether(
            name='walletdetails',
            unique_together={('wallet_name', 'wallet_password', 'seed')},
        ),
        migrations.CreateModel(
            name='SupportTicketComment',
            fields=[
                (
                'primary_key', models.UUIDField(default=uuid.uuid4, editable=False, primary_key=True, serialize=False)),
                ('author', models.TextField(null=True)),
                ('content', models.TextField(null=True)),
                ('creation_time', models.DateTimeField(auto_now_add=True, null=True)),
                ('updated_time', models.DateTimeField(auto_now=True, null=True)),
                ('support_ticket', models.ForeignKey(default=DenariiMobile.models.SupportTicket.get_default_pk,
                                                     on_delete=django.db.models.deletion.CASCADE,
                                                     to='DenariiMobile.supportticket')),
            ],
        ),
        migrations.AddField(
            model_name='supportticket',
            name='denarii_user',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE,
                                    to=settings.AUTH_USER_MODEL),
        ),
    ]
