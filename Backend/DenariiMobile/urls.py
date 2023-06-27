from django.urls import path

from . import views

# URLConf
urlpatterns = [
    path('<str:username>/<str:email>/<str:password>/', views.get_user_id),
    path('<str:username>/<str:email>/<str:password>/reset/', views.reset_password),
    path('<str:username_or_email>/request_reset/', views.request_reset),
    path('<str:username_or_email>/<int:reset_id>/verify_reset/', views.verify_reset),
    path('<str:user_id>/<str:wallet_name>/<str:password>/create/', views.create_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/<str:seed>/restore/', views.restore_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/open/', views.open_wallet),
    path('<str:user_id>/str:wallet_name>/balance/', views.get_balance),
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:wallet_name>/<str:address>/<str:amount>/send/', views.send_denarii),
    path('<str:user_id>/get_prices/', views.get_prices),
    # This one the amount is a string but really is a double. buy_regardless_of_price is a boolean.
    path('<str:user_id>/<str:amount>/<str:bid_price>/<str:buy_regardless_of_price>/buy_denarii/', views.buy_denarii),
    path('<str:user_id>/<str:ask_id>/transfer_denarii/', views.transfer_denarii),
    # This one the amount is a string but really is a double. The asking price is also really a double.
    path('<str:user_id>/<str:amount>/<str:asking_price>/make_denarii_ask/', views.make_denarii_ask),
    path('<str:user_id>/poll_for_completed_transaction/', views.poll_for_completed_transaction),
    path('<str:user_id>/<str:ask_id>/cancel_ask/cancel_denaii_ask/', views.cancel_ask)
]
