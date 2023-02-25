package bg.sofia.uni.fmi.mjt.cryptowalletmanager.user;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Crypto;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidAmountException;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.InvalidListingException;

public interface WalletInterface {
    public void deposit(double amount) throws InvalidAmountException;

    public void buy(Crypto crypto, double amount) throws InsufficientBalanceException, InvalidListingException, InvalidAmountException;

    public void sell(Crypto crypto, double amount) throws InvalidListingException, InvalidAmountException;
}
