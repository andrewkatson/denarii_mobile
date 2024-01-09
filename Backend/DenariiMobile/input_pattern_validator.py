import re


def is_valid_pattern(text, pattern_str):
    matches_list = re.findall(pattern_str, text)
    return len(matches_list) > 0
