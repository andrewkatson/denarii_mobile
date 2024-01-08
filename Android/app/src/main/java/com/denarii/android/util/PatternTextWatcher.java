package com.denarii.android.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PatternTextWatcher implements TextWatcher {

  private final List<Pattern> patterns = new ArrayList<>();

  private CharSequence mText;

  private final EditText editText;
  private final TextView textView;

  public PatternTextWatcher(EditText editText, String... patternStr) {
    this.editText = editText;
    this.textView = null;
    for (String pattern : patternStr) {
      this.patterns.add(Pattern.compile(pattern));
    }
  }

  public PatternTextWatcher(TextView textView, String ... patternStr) {
    this.editText = null;
    this.textView = null;
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
      if (Objects.equals(editText, null)) {
        textView.setText(mText);
      } else {
        editText.setText(mText);
      }
    }
    mText = "";
  }
}
