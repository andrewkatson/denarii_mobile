from django.urls import path

from . import views

# URLConf
urlpatterns = [
    # Retrieves the user identifier if the user exists. Creates a user and retrieves identifier if they do not.
    path('<str:username>/<str:email>/<str:password>/', views.get_user_id),
    # Resets the user's password to the passed one. Assumes it has already been confirmed as the desired password.
    path('reset/<str:username>/<str:email>/<str:password>/', views.reset_password),
    # Requests a password reset and sends the user an email.
    path('request_reset/<str:username_or_email>/', views.request_reset),
    # Verify reset by checking that the reset identifier matches what was sent in the email.
    path('verify_reset/<str:username_or_email>/<int:reset_id>/', views.verify_reset),
    # Creates a wallet using the name and password. Users right now can only have one wallet.
    path('create/<str:user_id>/<str:wallet_name>/<str:password>/', views.create_wallet),
    # Restores the wallet using the name, password, and seed.
    path('restore/<str:user_id>/<str:wallet_name>/<str:password>/<str:seed>/', views.restore_wallet),
    # Opens the wallet with the name and password passed.
    path('open/<str:user_id>/<str:wallet_name>/<str:password>/', views.open_wallet),
    # Retrives the balance for the current open wallet.
    path('balance/<str:user_id>/str:wallet_name>/', views.get_balance),
    # Sends denarii between two wallets.
    # This one the amount is a string but really is a double.
    path('send/<str:user_id>/<str:wallet_name>/<str:address>/<str:amount>/', views.send_denarii),
    # Gets all the lowest priced denarii asks that are not the user's.
    path('get_prices/<str:user_id>/', views.get_prices),
    # Puts the denarii someone has put up for sale into escrow. This can be either a full amount or a partial amount.
    # This one the amount is a string but really is a double. buy_regardless_of_price is a boolean. fail_if_full_amount_isnt_met is a boolean
    path('buy_denarii/<str:user_id>/<str:amount>/<str:bid_price>/<str:buy_regardless_of_price>/<str:fail_if_full_amount_isnt_met>/', views.buy_denarii),
    # Transfers denarii from an ask to the given user since they purchased it.
    path('transfer_denarii/<str:user_id>/<str:ask_id>/', views.transfer_denarii),
    # Make a new denarii ask.
    # This one the amount is a string but really is a double. The asking price is also really a double.
    path('make_denarii_ask/<str:user_id>/<str:amount>/<str:asking_price>/', views.make_denarii_ask),
    # Retrieves all the current denarii asks that are settled so you can proceed with transaction -- i.e. send_money_to_seller.
    path('poll_for_completed_transaction<str:user_id>//', views.poll_for_completed_transaction),
    # Cancel an ask. Asks that are in escrow or settled cannot be cancelled.
    path('cancel_denaii_ask/<str:user_id>/<str:ask_id>/', views.cancel_ask),
    # Check whether the current user has any credit card info on file.
    path('has_credit_card_info/<str:user_id>/', views.has_credit_card_info),
    # Set the user's credit card info.
    path('set_credit_card_info/<str:user_id>/<str:card_number>/<str:expiration_date_month>/<str:expiration_date_year>/<str:security_code/', views.set_credit_card_info),
    # Clear credit card info. Note this will also delete them from our stripe database.
    path('clear_credit_card_info/<str:user_id>/', views.clear_credit_card_info),
    # Get the money from the buying user and send it to us.
    # This one the amount is a string but really is a double.
    path('get_money_from_buyer/<str:user_id>/<str:amount>/<str:currency>/', views.get_money_from_buyer),
    # Send the money from us to the selling user.
    # This one the amount is a string but really is a double.
    path('send_money_to_seller/<str:user_id>/<str:amount>/<str:currency>/', views.send_money_to_seller),
    # Checks if the ask passed is settled
    path('is_transaction_settled/<str:user_id>/<str:ask_id>/', views.is_transaction_settled),
    # Allows a user to delete their account
    path('delete_user/<str:user_id>/', views.delete_user),
    # Gets the ask with the given identifier
    path('get_ask_with_identifier/<str:user_id>/<str:ask_id>/', views.get_ask_with_identifier),
    # Transfers denarii back to the original seller since a transaction failed
    path('transfer_denarii_back_to_seller/<str:user_id>/<str:ask_id>/', views.transfer_denarii_back_to_seller),
    # Sends the money put in escrow from a denarii buyer back  to them since a transaction failed
    # Amount is really a double
    path('send_money_back_to_buyer/<str:user_id>/<str:amount>/<str:currency>/', views.send_money_back_to_buyer),
    # Cancel a buy (by cancelling the purchase of one of the asks). Only asks in escrow that are not settled can be cancelled.
    path('cancel_buy_of_ask/<str:user_id>/<str:ask_id>/', views.cancel_buy_of_ask),
    # Returns if the individual user has been verified. It can will return one of three answers. "is_verified", "is_not_verified", "failed_verification", and "verification_pending".
    path('is_a_verified_person/<str:user_id>/', views.is_a_verified_person),
    # Tries to verify the person based on their status
    # Work locations is assumed to be a list of dicts as a json dump.
    # AKA [{"country": "US", "state": "VA", "city": "McLean"},{"country": "US", "state": "CA", "city": "San Francisco"}]
    path('verify_identity/<str:user_id>/<str:first_name>/<str:middle_name>/<str:last_name>/<str:email>/<str:phone>/<str:zipcode>/<str:dob>/<str:ssn>/<str:work_locations>/', views.verify_identity),
    # Tries to get all the asks for a user
    path('get_all_asks/<str:user_id>/', views.get_all_asks),
    # Creates a customer support ticket
    path('create_support_ticket/<str:user_id>/<str:title>/<str:description>/', views.create_support_ticket),
    # Updates a customer support ticket
    path('update_support_ticket/<str:user_id>/<str:support_ticket_id>/str:comment>/', views.update_support_ticket),
    # Deletes a customer support ticket
    path('delete_support_ticket/<str:user_id>/<str:support_ticket_id>/', views.delete_support_ticket),
    # Gets all customer support tickets for a user
    path('get_support_tickets/<str:user_id>/<str:can_be_resolved>/', views.get_support_tickets),
    # Get all comments on a support ticket
    path('get_comments_on_ticket/<str:user_id>/<str:support_ticket_id>/', views.get_comments_on_ticket),
    # Mark a support ticket as resolved
    path('resolve_support_ticket/<str:user_id>/<str:support_ticket_id>/', views.resolve_support_ticket)
]
