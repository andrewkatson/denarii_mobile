from django.urls import path

from . import views

# URLConf
urlpatterns = [
    path('<str:user>/<str:email>/', views.get_user_id),
    path('<str:id>/<str:wallet>/<str:password>/create', views.create_wallet),
    path('<str:id>/<str:wallet>/<str:password>/<str:seed>/restore/', views.restore_wallet),
    path('<str:id>/<str:wallet>/<str:password>/open/', views.open_wallet),
    path('<str:id>/<str:wallet>/balance/', views.get_balance),
    # This one the amount is a string but really is a double.
    path('<str:id>/<str:wallet>/<str:address>/<str:amount>/send')
]
