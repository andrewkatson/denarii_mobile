package com.denarii.android.util;

import com.denarii.android.user.CreditCard;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.DenariiUser;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.WalletDetails;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UnpackDenariiResponse {

  /**
   * Supplies a default instantiated UserDetails as if there was nothing in the DenariiResposnse
   * list*
   */
  public static UserDetails validUserDetails() {
    UserDetails userDetails = new UserDetails();
    userDetails.setWalletDetails(new WalletDetails());
    userDetails.setDenariiUser(new DenariiUser());
    return userDetails;
  }

  public static void unpackLoginOrRegister(UserDetails user, List<DenariiResponse> responses) {
    user.setUserID(responses.get(0).userIdentifier);
    // creating a wallet details so we can set its values in the other scenes
    WalletDetails newWallet = new WalletDetails();
    user.setWalletDetails(newWallet);
  }

  public static void unpackOpenedWallet(UserDetails user, List<DenariiResponse> responses) {
    user.getWalletDetails().setBalance(responses.get(0).balance);
  }

  public static void unpackOpenWallet(UserDetails user, List<DenariiResponse> responses) {
    user.getWalletDetails().setWalletAddress(responses.get(0).walletAddress);
    user.getWalletDetails().setSeed(responses.get(0).seed);
  }

  public static void unpackRestoreDeterministicWallet(
      UserDetails user, List<DenariiResponse> response) {
    user.getWalletDetails().setWalletAddress(response.get(0).walletAddress);
  }

  public static void unpackCreateWallet(UserDetails user, List<DenariiResponse> responses) {
    user.getWalletDetails().setWalletAddress(responses.get(0).walletAddress);
    user.getWalletDetails().setSeed(responses.get(0).seed);
  }

  public static void unpackGetPrices(List<DenariiAsk> asks, List<DenariiResponse> responses) {
    for (DenariiResponse response : responses) {
      DenariiAsk ask = new DenariiAsk();
      ask.setAskID(response.askID);
      ask.setAskingPrice(response.askingPrice);
      ask.setAmount(response.amount);

      asks.add(ask);
    }
  }

  public static void unpackIsTransactionSettled(
      Set<String> askIdsToRemove, List<DenariiResponse> responses) {
    for (DenariiResponse response : responses) {
      if (response.transactionWasSettled) {
        askIdsToRemove.add(response.askID);
      }
    }
  }

  public static void unpackHasCreditCardInfo(UserDetails user, List<DenariiResponse> responses) {
    if (Objects.equals(user.getCreditCard(), null)) {
      user.setCreditCard(new CreditCard());
    }

    DenariiResponse response = responses.get(0);

    user.getCreditCard().setHasCreditCardInfo(response.hasCreditCardInfo);
  }

  public static void unpackIsAVerifiedPerson(UserDetails user, List<DenariiResponse> responses) {
    if (Objects.equals(user.getDenariiUser(), null)) {
      user.setDenariiUser(new DenariiUser());
    }

    DenariiResponse response = responses.get(0);

    user.getDenariiUser().setVerified(Objects.equals(response.verificationStatus, "is_verified"));
  }

  public static boolean unpackBuyDenarii(
      List<DenariiAsk> newQueuedBuys, List<DenariiResponse> responses) {

    if (responses.isEmpty()) {
      return false;
    }

    for (DenariiResponse response : responses) {
      DenariiAsk ask = new DenariiAsk();
      ask.setAskID(response.askID);

      newQueuedBuys.add(ask);
    }

    return true;
  }

  public static boolean unpackGetMoneyFromBuyer(List<DenariiResponse> responses) {
    return !responses.isEmpty();
  }

  public static boolean unpackTransferDenarii(
      double[] amountBought, List<DenariiResponse> responses) {
    if (responses.isEmpty()) {
      return false;
    }
    DenariiResponse response = responses.get(0);
    amountBought[0] = response.amountBought;
    return true;
  }

  public static boolean unpackCancelBuyOfAsk(List<DenariiResponse> responses) {
    return !responses.isEmpty();
  }

  public static boolean unpackSendMoneyBackToBuyer(List<DenariiResponse> responses) {
    return !responses.isEmpty();
  }

  public static void unpackGetAskWithIdentifier(DenariiAsk ask, List<DenariiResponse> responses) {
    if (responses.isEmpty()) {
      return;
    }

    DenariiResponse response = responses.get(0);

    ask.setAskID(response.askID);
    ask.setAmountBought(response.amountBought);
    ask.setAmount(response.amount);
  }

  public static void unpackGetAllAsks(List<DenariiAsk> ownAsks, List<DenariiResponse> responses) {
    for (DenariiResponse response : responses) {
      DenariiAsk ask = new DenariiAsk();
      ask.setAskID(response.askID);
      ask.setAmount(response.amount);
      ask.setAskingPrice(response.askingPrice);
      ask.setAmountBought(response.amountBought);

      ownAsks.add(ask);
    }
  }

  public static void unpackPollForEscrowedTransaction(
      List<DenariiAsk> ownBoughtAsks, List<DenariiResponse> responses) {
    for (DenariiResponse response : responses) {
      DenariiAsk ask = new DenariiAsk();
      ask.setAskID(response.askID);
      ask.setAmount(response.amount);
      ask.setAskingPrice(response.askingPrice);
      ask.setAmountBought(response.amountBought);

      ownBoughtAsks.add(ask);
    }
  }

  public static boolean unpackMakeDenariiAsk(List<DenariiResponse> responses) {
    return !responses.isEmpty();
  }

  public static boolean unpackCancelAsk(List<DenariiResponse> responses) {
    return !responses.isEmpty();
  }
}
