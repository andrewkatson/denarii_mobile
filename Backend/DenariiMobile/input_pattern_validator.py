import re
from uuid import UUID

from DenariiMobile.constants import Patterns


def is_valid_uuid(uuid_to_test):
    try:
        uuid_obj = UUID(uuid_to_test, version=4)
    except ValueError:
        return False
    return str(uuid_obj) == uuid_to_test


def is_valid_pattern(text, pattern_str):
    if pattern_str == Patterns.uuid4:
        return is_valid_uuid(text)
    matches_list = re.findall(str(pattern_str), str(text))
    return len(matches_list) > 0
