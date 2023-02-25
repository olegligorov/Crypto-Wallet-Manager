package bg.sofia.uni.fmi.mjt.cryptowalletmanager.user;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.Utility;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Crypto;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Transaction;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.TransactionType;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidAmountException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidListingException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private final String username;
    private final String password;

    private Wallet wallet;

    private List<Transaction> transactionHistory;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
        this.transactionHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void deposit(double amount) throws InvalidAmountException {
        wallet.deposit(amount);
    }

    public void buy(Crypto crypto, double amount) throws InsufficientBalanceException, InvalidAmountException, InvalidListingException {
        wallet.buy(crypto, amount);
        Transaction transaction = new Transaction(crypto.getPrice_usd(), LocalDateTime.now(),
                crypto, TransactionType.BUY, amount);
        transactionHistory.add(transaction);
    }

    public void sell(Crypto crypto, double amount) throws InvalidAmountException, InvalidListingException {
        wallet.sell(crypto, amount);
        transactionHistory.add(new Transaction(crypto.getPrice_usd(), LocalDateTime.now(),
                crypto, TransactionType.SELL, amount));
    }

    public Wallet getWallet() {
        return wallet;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public String getHistory() {
        StringBuilder sb = new StringBuilder("");
        for (var transaction : transactionHistory) {
            if (transaction.getType() == TransactionType.BUY) {
                sb.append(transaction.getDate()).append(": Bought ");
            } else {
                sb.append(transaction.getDate()).append(": Sold ");
            }

            sb.append(transaction.getAmount())
                    .append(" ")
                    .append(transaction.getCrypto().getName())
                    .append(" with price ")
                    .append(transaction.getPrice())
                    .append(Utility.NEWLINE_SYMBOL);
        }
        return sb.toString();
    }
}
