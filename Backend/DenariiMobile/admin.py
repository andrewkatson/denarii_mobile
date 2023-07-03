from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from DenariiMobile.models import DenariiUser, WalletDetails, DenariiAsk

admin.site.register(WalletDetails)
admin.site.register(DenariiAsk)


class DenariiUserAdmin(UserAdmin):
    pass


admin.site.register(DenariiUser, DenariiUserAdmin)
