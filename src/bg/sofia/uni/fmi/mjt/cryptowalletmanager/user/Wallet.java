package bg.sofia.uni.fmi.mjt.cryptowalletmanager.user;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.Utility;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Crypto;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidAmountException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidListingException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Wallet implements WalletInterface, Serializable {
    private double balance;
    private Map<Crypto, Double> portfolio;

    public Wallet() {
        balance = 0;
        portfolio = new HashMap<>();
    }

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount can not be less than 0");
        }
        balance += amount;
    }

    public void buy(Crypto crypto, double amount) throws InsufficientBalanceException,
            InvalidListingException, InvalidAmountException {
        if (crypto == null) {
            throw new InvalidListingException("Invalid crypto");
        }
        if (amount < 0) {
            throw new InvalidAmountException("Buy amount can not be less than 0");
        }
        if (balance < crypto.getPrice_usd() * amount) {
            throw new InsufficientBalanceException("There is not enough money to finish the transaction");
        }
        balance -= crypto.getPrice_usd() * amount;
        double oldAmount = portfolio.getOrDefault(crypto, 0.00);
        portfolio.put(crypto, oldAmount + amount);
    }

    public void sell(Crypto crypto, double amount) throws InvalidListingException, InvalidAmountException {
        if (crypto == null) {
            throw new InvalidListingException("Invalid crypto");
        }
        if (amount < 0) {
            throw new InvalidAmountException("Sell amount can not be less than 0");
        }
        if (!portfolio.containsKey(crypto)) {
            throw new InvalidListingException("You can not sell an asset that you do not own");
        }
        if (amount > portfolio.get(crypto)) {
            throw new InvalidAmountException("You can not sell more than what you got");
        }
        balance += crypto.getPrice_usd() * amount;
        double oldAmount = portfolio.get(crypto);
        portfolio.put(crypto, oldAmount - amount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (var holding : portfolio.keySet()) {
            sb.append(holding.getName()).append(":").append(portfolio.get(holding)).
                    append("    price: ").append(holding.getPrice_usd()).append(Utility.NEWLINE_SYMBOL);
        }
        return "Wallet: " + Utility.NEWLINE_SYMBOL +
                "balance=" + balance + "$" + Utility.NEWLINE_SYMBOL +
                "portfolio:" + Utility.NEWLINE_SYMBOL +
                sb.toString();
    }
}
