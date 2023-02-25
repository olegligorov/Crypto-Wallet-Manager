package bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Crypto;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.CryptoListings;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.TransactionType;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.CryptoListingsException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidAmountException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidListingException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.UserNotRegisteredException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.User;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.UserDatabase;

import java.net.SocketAddress;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandExecutor {
    private static final String DEPOSIT_MONEY = "deposit-money";
    private static final String LIST_OFFERINGS = "list-offerings";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
    private static final String GET_WALLET_SUMMARY = "get-wallet-summary";
    private static final String GET_WALLET_OVERALL_SUMMARY = "get-wallet-overall-summary";
    private static final String GET_TRANSACTION_HISTORY = "get-transaction-history";

    private final UserDatabase users;

    private final CryptoListings cryptoListings;
    private Map<String, Crypto> cryptoMap = new LinkedHashMap<>();

    private HttpClient client = HttpClient.newBuilder().build();

    public CommandExecutor(UserDatabase users) {
        this.users = users;
        this.cryptoListings = new CryptoListings(client);
        try {
            cryptoMap = cryptoListings.getListings();
        } catch (CryptoListingsException e) {
            ExceptionLogger.writeException(e);
        }
    }

    public StringBuilder executeCommand(String message, SocketAddress clientAddress, Map<SocketAddress, User> loggedUsers) {
        StringBuilder response = new StringBuilder();
        String[] commands = message.split(" ");
        if ("help".equals(commands[0])) {
            String helpMessage = HelpMessage.getHelpMessage();
            return new StringBuilder(helpMessage);
        }
        try {
            if ("login".equals(commands[0])) {
                String username = commands[1];

                String userPassword = commands[2];
                String password = Encryptor.encrypt(userPassword);

                return login(username, password, clientAddress, loggedUsers);
            } else if ("logout".equals(commands[0])) {
                return logout(clientAddress, loggedUsers);
            } else if ("register".equals(commands[0])) {
                String username = commands[1];

                String userPassword = commands[2];
                String password = Encryptor.encrypt(userPassword);

                return register(username, password, clientAddress, loggedUsers);
            } else if ("disconnect".equals(commands[0])) {
                return new StringBuilder("Disconnected from the server!");
            } else if (loggedUsers.containsKey(clientAddress)) {
                User currentUser = loggedUsers.get(clientAddress);
                response.append(execute(CommandCreator.newCommand(currentUser, message)));
            } else {
                response = new StringBuilder("You need to log in first!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ExceptionLogger.writeException(e);
            response.append("Invalid input, type help to see the list of commands and how to use them");
            return response;
        } catch (UserNotRegisteredException e) {
            ExceptionLogger.writeException(e);
            response.append("You have to register first!");
            return response;
        }
        return response;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case DEPOSIT_MONEY -> depositMoney(cmd.user(), cmd.arguments());
            case LIST_OFFERINGS -> listOfferings();
            case BUY -> buy(cmd.user(), cmd.arguments());
            case SELL -> sell(cmd.user(), cmd.arguments());
            case GET_WALLET_SUMMARY -> getWalletSummary(cmd.user());
            case GET_WALLET_OVERALL_SUMMARY -> getWalletOverallSummary(cmd.user());
            case GET_TRANSACTION_HISTORY -> getTransactionHistory(cmd.user());
            default -> "Unknown command";
        };
    }

    private String depositMoney(User user, String[] arguments) {
        double sum = Double.parseDouble(arguments[0]);
        try {
            user.deposit(sum);
        } catch (InvalidAmountException e) {
            ExceptionLogger.writeException(e);
            return e.getMessage();
        }
        return "Successfully deposited " + sum + "$";
    }

    private void updateListings() {
        LocalDateTime timeNow = LocalDateTime.now().minusMinutes(30);
        if (cryptoListings.getListingsTime().isBefore(timeNow)) {
            try {
                cryptoMap = cryptoListings.getListings();
            } catch (CryptoListingsException e) {
                ExceptionLogger.writeException(e);
            }
        }
    }

    private String listOfferings() {
        updateListings();
        return cryptoMap.values()
                .stream()
                //.sorted(Comparator.comparing(Crypto::getPrice_usd).reversed())
                .map(Crypto::toString)
                .collect(Collectors.joining(Utility.NEWLINE_SYMBOL));
    }

    private String buy(User user, String[] arguments) {
        updateListings();
        String asset_id = arguments[0];
        double amount = Double.parseDouble(arguments[1]);
        Crypto crypto = cryptoMap.getOrDefault(asset_id, null);

        try {
            user.buy(crypto, amount);
        } catch (InsufficientBalanceException | InvalidAmountException | InvalidListingException e) {
            ExceptionLogger.writeException(e);
            return e.getMessage();
        }
        return "successfully bought " + amount + " " + crypto.getName();
    }

    private String sell(User user, String[] arguments) {
        updateListings();

        String asset_id = arguments[0];
        double amount = Double.parseDouble(arguments[1]);
        Crypto crypto = cryptoMap.getOrDefault(asset_id, null);

        try {
            user.sell(crypto, amount);
        } catch (InvalidAmountException | InvalidListingException e) {
            ExceptionLogger.writeException(e);
            return e.getMessage();
        }

        return "Successfully sold " + amount + " " + crypto.getName();
    }

    private String getWalletSummary(User user) {
        return user.getWallet().toString();
    }

    private String getTransactionHistory(User user) {
        return user.getHistory();
    }

    private String getWalletOverallSummary(User user) {
        updateListings();
        Map<Crypto, Double> PNLs = new HashMap<>();
        double totalPNL = 0.00;

        var transactionHistory = user.getTransactionHistory();
        //#TODO fix for type.SELL and add amount*price!
        for (var transaction : transactionHistory) {
            if (transaction.getType() == TransactionType.BUY) {
                double oldPNL = PNLs.getOrDefault(transaction.getCrypto(), 0.00);
                double oldPrice = transaction.getPrice();
                double currentPrice = cryptoMap.get(transaction.getCrypto().getAsset_id()).getPrice_usd();
                double currPNL = (currentPrice - oldPrice) * transaction.getAmount();
                PNLs.put(transaction.getCrypto(), oldPNL + currPNL);
                totalPNL += currPNL;
            }
        }

        StringBuilder sb = new StringBuilder("");
        sb.append("Profit and Loss from your holdings:");
/*        for (var crypto : PNLs.keySet()) {

        }
*/
        //#TODO
        return null;
    }


    private StringBuilder login(String username, String password, SocketAddress clientAddress,
                                Map<SocketAddress, User> loggedUsers) throws UserNotRegisteredException {
        if (loggedUsers.containsKey(clientAddress)) {
            return new StringBuilder("You need to logout first!");
        }
        User currentUser = users.getUser(username);
        if (currentUser.getPassword().equals(password)) {
            loggedUsers.put(clientAddress, currentUser);
            return new StringBuilder("Welcome " + username);
        } else {
            return new StringBuilder("Invalid password");
        }
    }

    private StringBuilder logout(SocketAddress clientAddress, Map<SocketAddress, User> loggedUsers) {
        if (!loggedUsers.containsKey(clientAddress)) {
            return new StringBuilder("You are not logged in!");
        }
        loggedUsers.remove(clientAddress);
        return new StringBuilder("Logged out!");
    }

    private StringBuilder register(String username, String password, SocketAddress clientAddress,
                                   Map<SocketAddress, User> loggedUsers) {
        if (loggedUsers.containsKey(clientAddress)) {
            return new StringBuilder("You need to logout first!");
        }
        if (users.containsUser(username)) {
            return new StringBuilder("Username is already taken!");
        }
        if (password == null || password.equals("")) {
            return new StringBuilder("Password can not be empty!");
        }
        User currentUser = users.registerUser(username, password);
        loggedUsers.put(clientAddress, currentUser);
        return new StringBuilder("Successfully registered!");
    }
}
