package com.denarii.android.util;

import com.denarii.android.user.CreditCard;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.DenariiUser;
import com.denarii.android.user.SupportTicket;
import com.denarii.android.user.SupportTicketComment;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.WalletDetails;

import java.util.ArrayList;
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

    public static boolean unpackLoginOrRegister(UserDetails user, List<DenariiResponse> responses) {
        if (responses.isEmpty()) {
            return false;
        }

        user.setUserID(responses.get(0).userIdentifier);
        // creating a wallet details so we can set its values in the other scenes
        WalletDetails newWallet = new WalletDetails();
        user.setWalletDetails(newWallet);
        return true;
    }

    public static boolean unpackGetBalance(UserDetails user, List<DenariiResponse> responses) {
        if (responses.isEmpty()) {
            return false;
        }
        user.getWalletDetails().setBalance(responses.get(0).balance);
        return true;
    }

    public static boolean unpackOpenWallet(UserDetails user, List<DenariiResponse> responses) {
        if (responses.isEmpty()) {
            return false;
        }

        user.getWalletDetails().setWalletAddress(responses.get(0).walletAddress);
        user.getWalletDetails().setSeed(responses.get(0).seed);
        return true;
    }

    public static boolean unpackRestoreDeterministicWallet(
            UserDetails user, List<DenariiResponse> responses) {
        if (responses.isEmpty()) {
            return false;
        }
        user.getWalletDetails().setWalletAddress(responses.get(0).walletAddress);

        return true;
    }

    public static boolean unpackCreateWallet(UserDetails user, List<DenariiResponse> responses) {

        if (responses.isEmpty()) {
            return false;
        }
        user.getWalletDetails().setWalletAddress(responses.get(0).walletAddress);
        user.getWalletDetails().setSeed(responses.get(0).seed);
        return true;
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
            user.getCreditCard().setHasCreditCardInfo(false);
            return;
        }

        if (responses.isEmpty()) {
            user.getCreditCard().setHasCreditCardInfo(false);
            return;
        }

        DenariiResponse response = responses.get(0);

        user.getCreditCard().setHasCreditCardInfo(response.hasCreditCardInfo);
    }

    public static void unpackIsAVerifiedPerson(UserDetails user, List<DenariiResponse> responses) {
        if (Objects.equals(user.getDenariiUser(), null)) {
            user.setDenariiUser(new DenariiUser());
        }

        if (responses.isEmpty()) {
            user.getDenariiUser().setVerified(false);
            user.getDenariiUser().setVerificationReportStatus("unknown");
        } else {

            DenariiResponse response = responses.get(0);

            user.getDenariiUser().setVerified(Objects.equals(response.verificationStatus, "is_verified"));
            user.getDenariiUser().setVerificationReportStatus(response.verificationStatus);
        }
    }

    public static void unpackVerifyIdentity(UserDetails user, List<DenariiResponse> responses) {
        if (Objects.equals(user.getDenariiUser(), null)) {
            user.setDenariiUser(new DenariiUser());
        }

        if (responses.isEmpty()) {
            user.getDenariiUser().setVerified(false);
            user.getDenariiUser().setVerificationReportStatus("unknown");
        } else {

            DenariiResponse response = responses.get(0);

            user.getDenariiUser().setVerified(Objects.equals(response.verificationStatus, "is_verified"));
            user.getDenariiUser().setVerificationReportStatus(response.verificationStatus);
        }
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

    public static boolean unpackCreateSupportTicket(
            UserDetails userDetails, List<DenariiResponse> responses) {

        if (responses.isEmpty()) {
            return false;
        }

        DenariiResponse response = responses.get(0);

        SupportTicket newTicket = new SupportTicket();
        newTicket.setSupportID(response.supportTicketID);
        userDetails.setCurrentTicket(newTicket);
        userDetails.addSupportTicket(newTicket);
        return true;
    }

    public static boolean unpackSetCreditCardInfo(
            UserDetails userDetails, List<DenariiResponse> responses) {
        if (Objects.equals(userDetails.getCreditCard(), null)) {
            userDetails.setCreditCard(new CreditCard());
        }

        if (responses.isEmpty()) {
            userDetails.getCreditCard().setHasCreditCardInfo(false);
            return false;
        } else {
            userDetails.getCreditCard().setHasCreditCardInfo(true);
            return true;
        }
    }

    public static boolean unpackClearCreditCardInfo(
            UserDetails userDetails, List<DenariiResponse> responses) {
        if (responses.isEmpty()) {
            return false;
        } else {
            userDetails.setCreditCard(null);
            return true;
        }
    }

    public static boolean unpackUpdateSupportTicket(
            UserDetails userDetails, List<DenariiResponse> responses) {
        if (Objects.equals(userDetails.getCurrentTicket(), null)) {
            userDetails.setCurrentTicket(new SupportTicket());
        }

        if (responses.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean unpackGetSupportTicket(
            UserDetails userDetails, List<DenariiResponse> responses) {
        if (Objects.equals(userDetails.getCurrentTicket(), null)) {
            userDetails.setCurrentTicket(new SupportTicket());
        }

        if (responses.isEmpty()) {
            return false;
        } else {
            DenariiResponse response = responses.get(0);

            SupportTicket currentTicket = userDetails.getCurrentTicket();
            currentTicket.setTitle(response.title);
            currentTicket.setDescription(response.description);

            return true;
        }
    }

    public static boolean unpackGetCommentsOnTicket(
            UserDetails userDetails, List<DenariiResponse> responses) {
        if (Objects.equals(userDetails.getCurrentTicket(), null)) {
            userDetails.setCurrentTicket(new SupportTicket());
        }

        if (!responses.isEmpty()) {
            SupportTicket currentTicket = userDetails.getCurrentTicket();
            currentTicket.clearSupportTicketCommentList();

            for (DenariiResponse response : responses) {
                SupportTicketComment newComment = new SupportTicketComment();
                newComment.setAuthor(response.author);
                newComment.setContent(response.content);

                currentTicket.addComment(newComment);
            }
        }
        // Even no comments is success
        return true;
    }

    public static boolean unpackResolveSupportTicket(UserDetails userDetails, List<DenariiResponse> responses) {
        boolean succeeded = !responses.isEmpty();
        if (succeeded) {
            userDetails.getCurrentTicket().setResolved(true);
        }
        return succeeded;
    }

    public static boolean unpackDeleteSupportTicket(UserDetails userDetails, List<DenariiResponse> responses) {
        List<SupportTicket> remainingTickets = new ArrayList<>();

        if (!responses.isEmpty()) {

            for (SupportTicket ticket : userDetails.getSupportTicketList()) {
                if (!Objects.equals(ticket.getSupportID(), userDetails.getCurrentTicket().getSupportID())) {
                    remainingTickets.add(ticket);
                }
            }
        }
        userDetails.clearSupportTicketList();
        userDetails.addAllSupportTicket(remainingTickets);
        return !responses.isEmpty();
    }

    public static boolean unpackGetSupportTickets(
            UserDetails userDetails, List<DenariiResponse> responses) {
        userDetails.clearSupportTicketList();

        if (!responses.isEmpty()) {

            for (DenariiResponse response : responses) {
                SupportTicket newTicket = new SupportTicket();
                newTicket.setTitle(response.title);
                newTicket.setDescription(response.description);

                userDetails.addSupportTicket(newTicket);
            }

        }
        // This is true because no support tickets is valid
        return true;
    }

    public static boolean unpackLogout(List<DenariiResponse> responses) {
        return !responses.isEmpty();
    }

    public static boolean unpackDeleteUser(List<DenariiResponse> responses) {
        return !responses.isEmpty();
    }
}
