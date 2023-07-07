from django.urls import path

from . import views

# URLConf
urlpatterns = [
    # Retrieves the user identifier if the user exists. Creates a user and retrieves identifier if they do not.
    path('<str:username>/<str:email>/<str:password>/', views.get_user_id),
    # Resets the user's password to the passed one. Assumes it has already been confirmed as the desired password.
    path('<str:username>/<str:email>/<str:password>/reset/', views.reset_password),
    # Requests a password reset and sends the user an email.
    path('<str:username_or_email>/request_reset/', views.request_reset),
    # Verify reset by checking that the reset identifier matches what was sent in the email.
    path('<str:username_or_email>/<int:reset_id>/verify_reset/', views.verify_reset),
    # Creates a wallet using the name and password. Users right now can only have one wallet.
    path('<str:user_id>/<str:wallet_name>/<str:password>/create/', views.create_wallet),
    # Restores the wallet using the name, password, and seed.
    path('<str:user_id>/<str:wallet_name>/<str:password>/<str:seed>/restore/', views.restore_wallet),
    # Opens the wallet with the name and password passed.
    path('<str:user_id>/<str:wallet_name>/<str:password>/open/', views.open_wallet),
    # Retrives the balance for the current open wallet.
    path('<str:user_id>/str:wallet_name>/balance/', views.get_balance),
    # Sends denarii between two wallets.
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:wallet_name>/<str:address>/<str:amount>/send/', views.send_denarii),
    # Gets all the lowest priced denarii asks that are not the user's.
    path('<str:user_id>/get_prices/', views.get_prices),
    # Puts the denarii someone has put up for sale into escrow. This can be either a full amount or a partial amount.
    # This one the amount is a string but really is a double. buy_regardless_of_price is a boolean. fail_if_full_amount_isnt_met is a boolean
    path('<str:user_id>/<str:amount>/<str:bid_price>/<str:buy_regardless_of_price>/<str:fail_if_full_amount_isnt_met>/buy_denarii/', views.buy_denarii),
    # Transfers denarii from an ask to the given user since they purchased it.
    path('<str:user_id>/<str:ask_id>/transfer_denarii/', views.transfer_denarii),
    # Make a new denarii ask.
    # This one the amount is a string but really is a double. The asking price is also really a double.
    path('<str:user_id>/<str:amount>/<str:asking_price>/make_denarii_ask/', views.make_denarii_ask),
    # Retrieves all the current denarii asks for a user. Use this to diff against the current list to see which have been settled so they can be removed.
    path('<str:user_id>/poll_for_completed_transaction/', views.poll_for_completed_transaction),
    # Cancel an ask.
    path('<str:user_id>/<str:ask_id>/cancel_denaii_ask/', views.cancel_ask),
    # Check whether the current user has any credit card info on file.
    path('<str:user_id>/has_credit_card_info/', views.has_credit_card_info),
    # Set the user's credit card info.
    path('<str:user_id>/<str:card_number>/<str:expiration_date_month>/<str:expiration_date_year>/<str:security_code/set_credit_card_info/', views.set_credit_card_info),
    # Clear credit card info. Note this will also delete them from our stripe database.
    path('<str:user_id>/clear_credit_card_info', views.clear_credit_card_info),
    # Get the money from the buying user and send it to us.
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:amount>/<str:currency>/get_money_from_buyer/', views.get_money_from_buyer),
    # Send the money from us to the selling user.
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:amount>/<str:currency>/send_money_to_seller/', views.send_money_to_seller)
]
