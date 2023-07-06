from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from DenariiMobile.models import DenariiUser, WalletDetails, DenariiAsk, Response, CreditCard

admin.site.register(WalletDetails)
admin.site.register(DenariiAsk)
admin.site.register(Response)
admin.site.register(CreditCard)


class DenariiUserAdmin(UserAdmin):
    pass


admin.site.register(DenariiUser, DenariiUserAdmin)
