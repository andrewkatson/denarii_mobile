package com.denarii.android.ui.containers;

import android.widget.Button;
import android.widget.TextView;

public class DenariiAskArtifacts {
  private String askId;
  private TextView amountTextView;
  private TextView askingPriceTextView;
  private TextView amountBoughtTextView;
  private Button cancelButton;

  public void setAskId(String askId) {
    this.askId = askId;
  }

  public void setAmountTextView(TextView amountTextView) {
    this.amountTextView = amountTextView;
  }

  public void setAskingPriceTextView(TextView askingPriceTextView) {
    this.askingPriceTextView = askingPriceTextView;
  }

  public void setAmountBoughtTextView(TextView amountBoughtTextView) {
    this.amountBoughtTextView = amountBoughtTextView;
  }

  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

  public String getAskId() {
    return askId;
  }

  public TextView getAmountTextView() {
    return amountTextView;
  }

  public TextView getAskingPriceTextView() {
    return askingPriceTextView;
  }

  public TextView getAmountBoughtTextView() {
    return amountBoughtTextView;
  }

  public Button getCancelButton() {
    return cancelButton;
  }
}
