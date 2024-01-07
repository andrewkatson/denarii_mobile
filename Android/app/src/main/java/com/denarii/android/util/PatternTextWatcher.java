package com.denarii.android.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternTextWatcher implements TextWatcher {

  private final List<Pattern> patterns = new ArrayList<>();

  private CharSequence mText;

  private final EditText editText;

  public PatternTextWatcher(EditText editText, String... patternStr) {
    this.editText = editText;
    for (String pattern : patternStr) {
      this.patterns.add(Pattern.compile(pattern));
    }
  }

  private boolean isValid(CharSequence s) {
    return patterns.stream().anyMatch(pattern -> pattern.matcher(s).matches());
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {}

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    mText = isValid(s) ? s : "";
  }

  @Override
  public void afterTextChanged(Editable s) {
    if (!isValid(s)) {
      editText.setText(mText);
    }
    mText = null;
  }
}
