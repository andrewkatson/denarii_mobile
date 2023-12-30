package com.denarii.android.network;

import com.denarii.android.user.CreditCard;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.DenariiUser;
import com.denarii.android.user.SupportTicket;
import com.denarii.android.user.SupportTicketComment;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.WalletDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import retrofit2.Call;

public class StubbedDenariiService implements DenariiService {

    private Map<String, UserDetails> users = new HashMap<>();

    private UserDetails loggedInUser = null;

    private int lastAskId = 1;

    private int lastUserId = 1;

    private int nextAddress = 1;

    private static class BuyDenariiTry {
        public String errorMessage;
        public List<DenariiAsk> asksMet = new ArrayList<>();
    }

    private Optional<UserDetails> getUserFromUsernameOrEmail(String usernameOrEmail) {
        Optional<UserDetails> userFromUsername = getUserFromUsername(usernameOrEmail);

        if (userFromUsername.isPresent()) {
            return userFromUsername;
        } else {
            return getUserFromEmail(usernameOrEmail);
        }
    }

    private Optional<UserDetails> getUserFromUsername(String username) {
        for (UserDetails user : users.values()) {
            if (Objects.equals(user.getUserName(), username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private Optional<UserDetails> getUserFromEmail(String email) {
        for (UserDetails user : users.values()) {
            if (Objects.equals(user.getUserEmail(), email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private Optional<UserDetails> getUserWithId(String userIdentifier) {
        if (users.containsKey(userIdentifier)) {
            return Optional.ofNullable(users.get(userIdentifier));
        } else {
            return Optional.empty();
        }
    }

    private Optional<UserDetails> getUserWithWalletAddress(String walletAddress) {
        for (UserDetails user : users.values()) {
            if (Objects.equals(user.getWalletDetails().getWalletAddress(), walletAddress)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private List<DenariiAsk> getAsksOfOtherUsers(String userIdentifier) {
        List<DenariiAsk> asks = new ArrayList<>();

        for (UserDetails user : users.values()) {
            if (!Objects.equals(user.getUserID(), userIdentifier)) {
                asks.addAll(user.getDenariiAskList());
            }
        }
        return asks;
    }

    private Optional<UserDetails> getUserWithAsk(String askIdentifier) {
        for (UserDetails user : users.values()) {
            for (DenariiAsk ask : user.getDenariiAskList()) {
                if (Objects.equals(ask.getAskID(), askIdentifier)) {
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<DenariiAsk> getAskWithId(String askIdentifier) {
        for (UserDetails user : users.values()) {
            for (DenariiAsk ask : user.getDenariiAskList()) {
                if (Objects.equals(ask.getAskID(), askIdentifier)) {
                    return Optional.of(ask);
                }
            }
        }
        return Optional.empty();
    }

    private List<DenariiAsk> getAsksOfUser(
            String userIdentifier, boolean isSettled, boolean inEscrow) {
        Optional<UserDetails> user = getUserWithId(userIdentifier);
        List<DenariiAsk> filteredAsks = new ArrayList<>();
        if (user.isPresent()) {
            for (DenariiAsk ask : user.get().getDenariiAskList()) {
                if (Objects.equals(isSettled, ask.getIsSettled())
                        && Objects.equals(inEscrow, ask.getInEscrow())) {
                    filteredAsks.add(ask);
                }
            }
        }
        return filteredAsks;
    }

    private boolean hasRemainingAsks(String userIdentifier) {
        Optional<UserDetails> user = getUserWithId(userIdentifier);
        if (user.isPresent()) {
            for (DenariiAsk ask : user.get().getDenariiAskList()) {
                if (ask.getIsSettled() && ask.getInEscrow()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasRemainingBuys(String userIdentifier) {
        for (UserDetails user : users.values()) {
            if (!Objects.equals(user.getUserID(), userIdentifier)) {
                for (DenariiAsk ask : user.getDenariiAskList()) {
                    if (Objects.equals(ask.getBuyer().getUserID(), userIdentifier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Optional<SupportTicket> getSupportTicketWithId(String supportTicketIdentifier) {
        for (SupportTicket supportTicket : loggedInUser.getSupportTicketList()) {
            if (Objects.equals(supportTicket.getSupportID(), supportTicketIdentifier)) {
                return Optional.of(supportTicket);
            }
        }
        return Optional.empty();
    }

    private void requireLogin(String userIdentifier) {
        if (users.containsKey(userIdentifier)) {
            if (!Objects.equals(loggedInUser.getUserID(), userIdentifier)) {
                throw new IllegalArgumentException(
                        String.format(Locale.ENGLISH, "User with id: %s is not logged in", userIdentifier));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format(Locale.ENGLISH, "No user with id: %s", userIdentifier));
        }
    }

    private BuyDenariiTry tryToBuyDenarii(
            List<DenariiAsk> orderedAsks,
            double amount,
            double bidPrice,
            boolean buyRegardlessOfPrice,
            boolean failIfFullAmountIsntMet) {
        double currentBoughtAmount = 0.0;
        double amountToBuyLeft = amount;
        double currentAskPrice = 0.0;
        List<DenariiAsk> asksMet = new ArrayList<>();

        for (DenariiAsk ask : orderedAsks) {
            currentAskPrice = ask.getAskingPrice();

            if (!buyRegardlessOfPrice && currentAskPrice > bidPrice) {

                if (failIfFullAmountIsntMet) {
                    for (DenariiAsk reprocessedAsk : orderedAsks) {
                        reprocessedAsk.setInEscrow(false);
                        reprocessedAsk.setAmountBought(0.0);
                    }
                }
                BuyDenariiTry buyDenariiTry = new BuyDenariiTry();
                buyDenariiTry.errorMessage = "Asking price was higher than bid price";
                buyDenariiTry.asksMet.addAll(asksMet);

                return buyDenariiTry;
            }

            ask.setInEscrow(true);

            ask.setAmountBought(Math.min(ask.getAmount(), amountToBuyLeft));

            currentBoughtAmount += ask.getAmountBought();
            amountToBuyLeft -= ask.getAmountBought();

            asksMet.add(ask);

            if (currentBoughtAmount >= amount) {
                BuyDenariiTry buyDenariiTry = new BuyDenariiTry();
                buyDenariiTry.errorMessage = "";
                buyDenariiTry.asksMet.addAll(asksMet);
                return buyDenariiTry;
            }
        }

        BuyDenariiTry buyDenariiTry = new BuyDenariiTry();
        buyDenariiTry.errorMessage = "Reached end of asks so not enough was bought";
        buyDenariiTry.asksMet.addAll(asksMet);

        return buyDenariiTry;
    }

    @Override
    public Call<List<DenariiResponse>> getUserId(String userName, String email, String password) {
        List<DenariiResponse> responses = new ArrayList<>();

        UserDetails foundUser = null;
        for (UserDetails user : users.values()) {
            if (Objects.equals(user.getUserName(), userName)
                    && Objects.equals(user.getUserEmail(), email)
                    && Objects.equals(user.getUserPassword(), password)) {
                foundUser = user;
                break;
            }
        }

        DenariiResponse response = new DenariiResponse();
        if (foundUser != null) {
            response.userIdentifier = foundUser.getUserID();
            loggedInUser = foundUser;
        } else if (loggedInUser != null) {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        } else {
            // This is the same as registering
            UserDetails newUser = new UserDetails();
            newUser.setUserName(userName);
            newUser.setUserEmail(email);
            newUser.setUserPassword(password);
            newUser.setUserID(String.valueOf(lastUserId));
            newUser.setDenariiUser(new DenariiUser());

            response.userIdentifier = newUser.getUserID();

            users.put(newUser.getUserID(), newUser);

            lastUserId += 1;
        }

        responses.add(response);
        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> requestPasswordReset(String usernameOrEmail) {
        List<DenariiResponse> responses = new ArrayList<>();

        Optional<UserDetails> user = getUserFromUsernameOrEmail(usernameOrEmail);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();
            // Static reset id to make testing easier.
            userDetails.getDenariiUser().setResetID(123);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        responses.add(new DenariiResponse());
        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> verifyReset(String usernameOrEmail, int resetId) {
        List<DenariiResponse> responses = new ArrayList<>();

        Optional<UserDetails> user = getUserFromUsernameOrEmail(usernameOrEmail);

        if (user.isPresent()) {
            if (user.get().getDenariiUser().getResetID() == resetId) {
                responses.add(new DenariiResponse());
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> resetPassword(String username, String email, String password) {
        List<DenariiResponse> responses = new ArrayList<>();

        Optional<UserDetails> user = getUserFromUsername(username);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            if (Objects.equals(userDetails.getUserEmail(), email)) {
                userDetails.setUserPassword(password);
                responses.add(new DenariiResponse());
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> createWallet(
            String userIdentifier, String walletName, String password) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            if (Objects.equals(userDetails.getWalletDetails(), null)) {
                DenariiResponse response = new DenariiResponse();

                response.walletAddress = String.valueOf(nextAddress);
                nextAddress += 1;
                // static seed so it is easier to restore
                response.seed = "some seed here";

                WalletDetails walletDetails = new WalletDetails();
                walletDetails.setWalletName(walletName);
                walletDetails.setWalletPassword(password);
                walletDetails.setSeed(response.seed);
                walletDetails.setWalletAddress(response.walletAddress);
                // Just give the wallet some balance so we can test
                walletDetails.setBalance(10.0);

                userDetails.setWalletDetails(walletDetails);

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> restoreWallet(
            String userIdentifier, String walletName, String password, String seed) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            UserDetails userDetails = user.get();

            if (Objects.equals(userDetails.getWalletDetails().getWalletPassword(), password)
                    && Objects.equals(userDetails.getWalletDetails().getWalletName(), walletName)
                    && Objects.equals(userDetails.getWalletDetails().getSeed(), seed)) {

                DenariiResponse response = new DenariiResponse();

                Random rand = new Random();

                response.walletAddress = String.valueOf(rand.nextInt(100));

                WalletDetails walletDetails = new WalletDetails();
                walletDetails.setWalletName(walletName);
                walletDetails.setWalletPassword(password);
                walletDetails.setSeed(seed);
                walletDetails.setWalletAddress(response.walletAddress);
                // Just give the wallet some balance so we can test
                walletDetails.setBalance(10.0);

                userDetails.setWalletDetails(walletDetails);

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> openWallet(
            String userIdentifier, String walletName, String password) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            if (Objects.equals(userDetails.getWalletDetails().getWalletPassword(), password)
                    && Objects.equals(userDetails.getWalletDetails().getWalletName(), walletName)) {
                DenariiResponse response = new DenariiResponse();

                WalletDetails walletDetails = userDetails.getWalletDetails();

                response.walletAddress = walletDetails.getWalletAddress();
                response.seed = walletDetails.getSeed();

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getBalance(String userIdentifier, String walletName) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            if (!Objects.equals(userDetails.getWalletDetails(), null)) {
                DenariiResponse response = new DenariiResponse();

                response.balance = userDetails.getWalletDetails().getBalance();

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> sendDenarii(
            String userIdentifier, String walletName, String addressToSendTo, double amountToSend) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);
        Optional<UserDetails> receivingUser = getUserWithWalletAddress(addressToSendTo);

        if (user.isPresent() && receivingUser.isPresent()) {
            UserDetails userDetails = user.get();
            UserDetails receivingUserDetails = receivingUser.get();

            if (userDetails.getWalletDetails().getBalance() >= amountToSend) {
                userDetails
                        .getWalletDetails()
                        .setBalance(userDetails.getWalletDetails().getBalance() - amountToSend);

                receivingUserDetails
                        .getWalletDetails()
                        .setBalance(receivingUserDetails.getWalletDetails().getBalance() + amountToSend);

                responses.add(new DenariiResponse());
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getPrices(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            List<DenariiAsk> asksOfOtherUsers = getAsksOfOtherUsers(userIdentifier);

            for (DenariiAsk ask : asksOfOtherUsers) {
                DenariiResponse response = new DenariiResponse();

                response.askID = ask.getAskID();
                response.amount = ask.getAmount();
                response.askingPrice = ask.getAskingPrice();

                responses.add(response);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> buyDenarii(
            String userIdentifier,
            String amount,
            String bidPrice,
            String buyRegardlessOfPrice,
            String failIfFullAmountIsntMet) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            List<DenariiAsk> allAsks = getAsksOfOtherUsers(userIdentifier);

            allAsks.sort((askOne, askTwo) -> (int) (askOne.getAskingPrice() - askTwo.getAskingPrice()));

            BuyDenariiTry buyDenariiTry =
                    tryToBuyDenarii(
                            allAsks,
                            Double.parseDouble(amount),
                            Double.parseDouble(bidPrice),
                            Boolean.parseBoolean(buyRegardlessOfPrice),
                            Boolean.parseBoolean(failIfFullAmountIsntMet));

            for (DenariiAsk askMet : buyDenariiTry.asksMet) {
                askMet.setBuyer(userDetails);
            }

            if (buyDenariiTry.errorMessage.isEmpty()) {
                for (DenariiAsk askMet : buyDenariiTry.asksMet) {
                    DenariiResponse response = new DenariiResponse();
                    response.askID = askMet.getAskID();

                    responses.add(response);
                }
            } else if (buyDenariiTry.asksMet.isEmpty()) {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            } else {
                if (Boolean.parseBoolean(failIfFullAmountIsntMet)) {
                    for (DenariiAsk askMet : buyDenariiTry.asksMet) {
                        askMet.setInEscrow(false);
                        askMet.setBuyer(null);
                        askMet.setAmountBought(0.0);
                    }

                    // An empty responses list indicates failure
                    return new StubbedCall(responses);
                } else {
                    for (DenariiAsk askMet : buyDenariiTry.asksMet) {
                        DenariiResponse response = new DenariiResponse();
                        response.askID = askMet.getAskID();

                        responses.add(response);
                    }
                }
            }

        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> transferDenarii(String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            Optional<DenariiAsk> ask = getAskWithId(askIdentifier);

            if (ask.isPresent()) {
                Optional<UserDetails> sendingUser = getUserWithAsk(askIdentifier);

                if (sendingUser.isPresent()) {

                    UserDetails receivingUserDetails = user.get();
                    UserDetails sendingUserDetails = sendingUser.get();
                    DenariiAsk denariiAsk = ask.get();

                    if (sendingUserDetails.getWalletDetails().getBalance() >= denariiAsk.getAmountBought()) {
                        sendingUserDetails
                                .getWalletDetails()
                                .setBalance(
                                        sendingUserDetails.getWalletDetails().getBalance()
                                                - denariiAsk.getAmountBought());
                        receivingUserDetails
                                .getWalletDetails()
                                .setBalance(
                                        receivingUserDetails.getWalletDetails().getBalance()
                                                + denariiAsk.getAmountBought());

                        DenariiResponse response = new DenariiResponse();

                        response.askID = denariiAsk.getAskID();
                        response.amountBought = denariiAsk.getAmountBought();

                        denariiAsk.setInEscrow(false);
                        denariiAsk.setBuyer(null);
                        denariiAsk.setAmount(denariiAsk.getAmount() - denariiAsk.getAmountBought());
                        denariiAsk.setAmountBought(0.0);
                        denariiAsk.setIsSettled(true);

                        responses.add(response);
                    } else {
                        // An empty responses list indicates failure
                        return new StubbedCall(responses);
                    }
                } else {
                    // An empty responses list indicates failure
                    return new StubbedCall(responses);
                }
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> makeDenariiAsk(
            String userIdentifier, String amount, String askingPrice) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            DenariiAsk newAsk = new DenariiAsk();
            newAsk.setInEscrow(false);
            newAsk.setAmount(Double.parseDouble(amount));
            newAsk.setBuyer(null);
            newAsk.setAskingPrice(Double.parseDouble(askingPrice));
            newAsk.setAskID(String.valueOf(lastAskId));
            newAsk.setIsSettled(false);
            newAsk.setHasBeenSeenBySeller(false);

            UserDetails userDetails = user.get();
            userDetails.addDenariiAsk(newAsk);

            lastAskId += 1;

            DenariiResponse response = new DenariiResponse();
            response.askID = newAsk.getAskID();
            response.askingPrice = newAsk.getAskingPrice();
            response.amount = newAsk.getAmount();

            responses.add(response);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> pollForCompletedTransaction(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            List<DenariiAsk> filteredAsks = getAsksOfUser(userIdentifier, true, false);

            for (DenariiAsk ask : filteredAsks) {
                ask.setHasBeenSeenBySeller(true);

                DenariiResponse response = new DenariiResponse();
                response.askID = ask.getAskID();
                response.amount = ask.getAmount();
                response.askingPrice = ask.getAskingPrice();
                response.amountBought = ask.getAmountBought();

                responses.add(response);
            }

        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> cancelAsk(String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = new UserDetails();
            List<DenariiAsk> remainingAsks = new ArrayList<>();
            DenariiAsk deletedAsk = null;
            boolean deletedAnAsk = false;

            for (DenariiAsk ask : userDetails.getDenariiAskList()) {
                if (Objects.equals(ask.getAskID(), askIdentifier)) {
                    deletedAsk = ask;
                    deletedAnAsk = true;
                } else {
                    remainingAsks.add(ask);
                }
            }

            userDetails.clearDenariiAskList();
            ;
            userDetails.addAllDenariiAsk(remainingAsks);

            if (deletedAnAsk) {
                DenariiResponse response = new DenariiResponse();
                response.askID = deletedAsk.getAskID();

                responses.add(response);
            } else {
                // An empty response list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> hasCreditCardInfo(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            boolean hasCreditCardInfo = !Objects.equals(userDetails.getCreditCard(), null);

            DenariiResponse response = new DenariiResponse();
            response.hasCreditCardInfo = hasCreditCardInfo;

            responses.add(response);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> setCreditCardInfo(
            String userIdentifier,
            String cardNumber,
            String expirationDateMonth,
            String expirationDateYear,
            String securityCode) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            Random rand = new Random();

            CreditCard newCreditCard = new CreditCard();
            newCreditCard.setCustomerID(String.valueOf(rand.nextInt(100)));
            newCreditCard.setSourceTokenID(String.valueOf(rand.nextInt(100)));

            userDetails.setCreditCard(newCreditCard);

            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> clearCreditCardInfo(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = new UserDetails();

            userDetails.setCreditCard(null);

            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getMoneyFromBuyer(
            String userIdentifier, String amount, String currency) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> sendMoneyToSeller(
            String userIdentifier, String amount, String currency) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> isTransactionSettled(
            String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            Optional<DenariiAsk> ask = getAskWithId(askIdentifier);

            UserDetails userDetails = user.get();
            if (ask.isPresent()) {
                DenariiAsk denariiAsk = ask.get();

                DenariiResponse response = new DenariiResponse();
                if (!denariiAsk.getIsSettled() || !denariiAsk.getSeenBySeller()) {
                    response.transactionWasSettled = false;

                    responses.add(response);
                } else {
                    response.transactionWasSettled = true;

                    if (denariiAsk.getAmount() == 0) {

                        // Here we delete the ask if this was the last bit in it.
                        List<DenariiAsk> remainingAsks = new ArrayList<>();

                        for (DenariiAsk otherAsk : userDetails.getDenariiAskList()) {
                            if (!Objects.equals(otherAsk.getAskID(), askIdentifier)) {
                                remainingAsks.add(otherAsk);
                            }
                        }

                        userDetails.clearDenariiAskList();
                        ;
                        userDetails.addAllDenariiAsk(remainingAsks);
                    } else {
                        denariiAsk.setIsSettled(false);
                        denariiAsk.setHasBeenSeenBySeller(false);
                    }
                }
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> deleteUser(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            if (hasRemainingAsks(userIdentifier) || hasRemainingBuys(userIdentifier)) {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            } else {
                loggedInUser = null;

                users.remove(userIdentifier);

                responses.add(new DenariiResponse());
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getAskWithIdentifier(
            String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            Optional<DenariiAsk> ask = getAskWithId(askIdentifier);

            if (ask.isPresent()) {
                DenariiResponse response = new DenariiResponse();

                DenariiAsk denariiAsk = ask.get();

                response.askID = askIdentifier;
                response.amount = denariiAsk.getAmount();
                response.amountBought = denariiAsk.getAmountBought();
                response.askingPrice = denariiAsk.getAskingPrice();

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> transferDenariiBackToSeller(
            String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            Optional<DenariiAsk> ask = getAskWithId(askIdentifier);

            if (ask.isPresent()) {
                Optional<UserDetails> receivingUser = getUserWithAsk(askIdentifier);

                if (receivingUser.isPresent()) {

                    UserDetails sendingUserDetails = user.get();
                    UserDetails receivingUserDetails = receivingUser.get();
                    DenariiAsk denariiAsk = ask.get();

                    if (sendingUserDetails.getWalletDetails().getBalance() >= denariiAsk.getAmountBought()) {
                        sendingUserDetails
                                .getWalletDetails()
                                .setBalance(
                                        sendingUserDetails.getWalletDetails().getBalance()
                                                - denariiAsk.getAmountBought());
                        receivingUserDetails
                                .getWalletDetails()
                                .setBalance(
                                        receivingUserDetails.getWalletDetails().getBalance()
                                                + denariiAsk.getAmountBought());

                        DenariiResponse response = new DenariiResponse();

                        response.askID = denariiAsk.getAskID();
                        response.amountBought = denariiAsk.getAmountBought();

                        denariiAsk.setInEscrow(false);
                        denariiAsk.setBuyer(null);
                        denariiAsk.setAmount(denariiAsk.getAmount() + denariiAsk.getAmountBought());
                        denariiAsk.setAmountBought(0.0);
                        denariiAsk.setIsSettled(false);

                        responses.add(response);
                    } else {
                        // An empty responses list indicates failure
                        return new StubbedCall(responses);
                    }
                } else {
                    // An empty responses list indicates failure
                    return new StubbedCall(responses);
                }
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> sendMoneyBackToBuyer(
            String userIdentifier, String amount, String currency) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> cancelBuyOfAsk(String userIdentifier, String askIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            Optional<DenariiAsk> ask = getAskWithId(askIdentifier);

            if (ask.isPresent()) {
                DenariiAsk denariiAsk = ask.get();

                if (denariiAsk.getInEscrow() && !denariiAsk.getIsSettled()) {
                    denariiAsk.setInEscrow(false);
                    denariiAsk.setBuyer(null);
                    denariiAsk.setAmountBought(0.0);
                } else {
                    // An empty responses list indicates failure
                    return new StubbedCall(responses);
                }
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> verifyIdentity(
            String userIdentifier,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String dateOfBirth,
            String socialSecurityNumber,
            String zipcode,
            String phone,
            String workLocations) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            userDetails.getDenariiUser().setVerified(true);

            DenariiResponse response = new DenariiResponse();
            response.verificationStatus = "is_verified";

            responses.add(response);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> isAVerifiedPerson(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();

            DenariiResponse response = new DenariiResponse();

            if (userDetails.getDenariiUser().getIsVerified()) {
                response.verificationStatus = "is_verified";
            } else {
                response.verificationStatus = "failed_verification";
            }
            responses.add(response);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getAllAsks(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            for (DenariiAsk ask : user.get().getDenariiAskList()) {
                if (!ask.getIsSettled() && !ask.getInEscrow()) {
                    DenariiResponse response = new DenariiResponse();
                    response.askID = ask.getAskID();
                    response.amount = ask.getAmount();
                    response.amountBought = ask.getAmountBought();
                    response.askingPrice = ask.getAskingPrice();

                    responses.add(response);
                } else {
                    // An empty responses list indicates failure
                    return new StubbedCall(responses);
                }
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getAllBuys(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            List<DenariiAsk> asksOfOtherUsers = getAsksOfOtherUsers(userIdentifier);

            for (DenariiAsk ask : asksOfOtherUsers) {
                if (Objects.equals(ask.getBuyer().getUserID(), userIdentifier)) {
                    DenariiResponse response = new DenariiResponse();
                    response.askID = ask.getAskID();
                    response.amount = ask.getAmount();
                    response.askingPrice = ask.getAskingPrice();
                    response.amountBought = ask.getAmountBought();

                    responses.add(response);
                }
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> createSupportTicket(
            String userIdentifier, String title, String description) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            UserDetails userDetails = user.get();

            Random rand = new Random();

            SupportTicket supportTicket = new SupportTicket();
            supportTicket.setSupportID(String.valueOf(rand.nextInt(100)));
            supportTicket.setDescription(description);
            supportTicket.setTitle(title);

            userDetails.addSupportTicket(supportTicket);

            DenariiResponse response = new DenariiResponse();

            response.supportTicketID = supportTicket.getSupportID();

            responses.add(response);

        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> updateSupportTicket(
            String userIdentifier, String supportTicketIdentifier, String comment) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            Optional<SupportTicket> supportTicket = getSupportTicketWithId(supportTicketIdentifier);

            if (supportTicket.isPresent()) {
                SupportTicket denariiSupportTicket = supportTicket.get();

                SupportTicketComment newComment = new SupportTicketComment();
                newComment.setAuthor(user.get().getUserName());
                newComment.setContent(comment);

                denariiSupportTicket.addComment(newComment);

                Random rand = new Random();

                DenariiResponse response = new DenariiResponse();
                response.supportTicketID = supportTicketIdentifier;
                response.commentId = String.valueOf(rand.nextInt(100));

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> deleteSupportTicket(
            String userIdentifier, String supportTicketIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            List<SupportTicket> remainingTickets = new ArrayList<>();
            UserDetails userDetails = user.get();

            for (SupportTicket supportTicket : userDetails.getSupportTicketList()) {
                if (!Objects.equals(supportTicket.getSupportID(), supportTicketIdentifier)) {
                    remainingTickets.add(supportTicket);
                }
            }

            userDetails.clearSupportTicketList();
            userDetails.addAllSupportTicket(remainingTickets);

            DenariiResponse response = new DenariiResponse();
            response.supportTicketID = supportTicketIdentifier;

            responses.add(response);
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getSupportTickets(
            String userIdentifier, String canBeResolved) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();
            boolean canBeResolvedBool = Boolean.parseBoolean(canBeResolved);
            for (SupportTicket supportTicket : userDetails.getSupportTicketList()) {
                if (canBeResolvedBool || !supportTicket.getResolved()) {
                    DenariiResponse response = new DenariiResponse();
                    response.supportTicketID = supportTicket.getSupportID();
                    response.author = userDetails.getUserName();
                    response.title = supportTicket.getTitle();
                    response.description = supportTicket.getDescription();
                    response.updatedTimeBody = "";
                    response.creationTimeBody = "";
                    response.isResolved = supportTicket.getResolved();

                    responses.add(response);
                }
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getSupportTicket(
            String userIdentifier, String supportTicketIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            Optional<SupportTicket> supportTicketOptional =
                    getSupportTicketWithId(supportTicketIdentifier);

            if (supportTicketOptional.isPresent()) {
                SupportTicket supportTicket = supportTicketOptional.get();

                DenariiResponse response = new DenariiResponse();
                response.supportTicketID = supportTicketIdentifier;
                response.author = user.get().getUserName();
                response.title = supportTicket.getTitle();
                response.description = supportTicket.getDescription();
                response.updatedTimeBody = "";
                response.creationTimeBody = "";

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> getCommentsOnTicket(
            String userIdentifier, String supportTicketIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {

            Optional<SupportTicket> supportTicketOptional =
                    getSupportTicketWithId(supportTicketIdentifier);

            if (supportTicketOptional.isPresent()) {
                SupportTicket supportTicket = supportTicketOptional.get();

                for (SupportTicketComment comment : supportTicket.getSupportTicketCommentList()) {
                    DenariiResponse response = new DenariiResponse();
                    response.author = comment.getAuthor();
                    response.content = comment.getContent();
                    response.updatedTimeBody = "";
                    response.creationTimeBody = "";

                    responses.add(response);
                }
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> resolveSupportTicket(
            String userIdentifier, String supportTicketIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            Optional<SupportTicket> supportTicketOptional =
                    getSupportTicketWithId(supportTicketIdentifier);

            if (supportTicketOptional.isPresent()) {
                SupportTicket supportTicket = supportTicketOptional.get();

                supportTicket.setResolved(true);

                DenariiResponse response = new DenariiResponse();
                response.supportTicketID = supportTicketIdentifier;
                response.updatedTimeBody = "";

                responses.add(response);
            } else {
                // An empty responses list indicates failure
                return new StubbedCall(responses);
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> pollForEscrowedTransaction(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            UserDetails userDetails = user.get();
            for (DenariiAsk ask : userDetails.getDenariiAskList()) {
                if (ask.getInEscrow() && !ask.getIsSettled()) {
                    DenariiResponse response = new DenariiResponse();

                    response.askID = ask.getAskID();
                    response.amount = ask.getAmount();
                    response.askingPrice = ask.getAskingPrice();
                    response.amountBought = ask.getAmountBought();

                    responses.add(response);
                }
            }
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }

    @Override
    public Call<List<DenariiResponse>> logoutUser(String userIdentifier) {
        List<DenariiResponse> responses = new ArrayList<>();

        requireLogin(userIdentifier);

        Optional<UserDetails> user = getUserWithId(userIdentifier);

        if (user.isPresent()) {
            loggedInUser = null;

            responses.add(new DenariiResponse());
        } else {
            // An empty responses list indicates failure
            return new StubbedCall(responses);
        }

        return new StubbedCall(responses);
    }
}
