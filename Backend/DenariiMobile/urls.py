from django.urls import path

from . import views

# URLConf
urlpatterns = [
    path('<str:username>/<str:email>/<std:password>/', views.get_user_id),
    path('<str:username>/<str:email>/<str:password>/reset/', views.reset_password),
    path('<str:username_or_email>/request_reset/', views.request_reset),
    path('<str:username_or_email>/<int:reset_id>/verify_reset/', views.verify_reset),
    path('<str:user_id>/<str:wallet_name>/<str:password>/create/', views.create_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/<str:seed>/restore/', views.restore_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/open/', views.open_wallet),
    path('<str:user_id>/str:wallet_name>/balance/', views.get_balance),
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:wallet_name>/<str:address>/<str:amount>/send/', views.send_denarii),
]
