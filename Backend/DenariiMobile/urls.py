from django.urls import path

from . import views

# URLConf
urlpatterns = [
    path('<str:username>/<str:email>/<std:password>/', views.get_user_id),
    path('<str:user_id>/<str:wallet_name>/<str:password>/create/', views.create_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/<str:seed>/restore/', views.restore_wallet),
    path('<str:user_id>/<str:wallet_name>/<str:password>/open/', views.open_wallet),
    path('<str:user_id>/str:wallet_name>/balance/', views.get_balance),
    # This one the amount is a string but really is a double.
    path('<str:user_id>/<str:wallet_name>/<str:address>/<str:amount>/send/', views.send_denarii)
]
