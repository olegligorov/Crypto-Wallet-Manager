package bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private double price;
    private LocalDateTime date;
    private Crypto crypto;
    private TransactionType type;
    private double amount;

    public Transaction(double price, LocalDateTime date, Crypto crypto, TransactionType type, double amount) {
        this.price = price;
        this.date = date;
        this.crypto = crypto;
        this.type = type;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
