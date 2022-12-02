from django.db.migrations.serializer import BaseSerializer
from django.db.migrations.writer import MigrationWriter

from DenariiMobile.models import DenariiUser


class DenariiUserSerializer(BaseSerializer):

    def serialize(self):
        return str(self.value), 'from DenariiMobile.models import DenariiUser'


MigrationWriter.register_serializer(DenariiUser, DenariiUserSerializer)
