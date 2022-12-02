from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from DenariiMobile.models import DenariiUser, WalletDetails

admin.site.register(WalletDetails)


class DenariiUserAdmin(UserAdmin):
    pass


admin.site.register(DenariiUser, DenariiUserAdmin)
