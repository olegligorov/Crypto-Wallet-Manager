package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

public class InvalidAmountException extends Exception {
    public InvalidAmountException(String msg) {
        super(msg);
    }

    public InvalidAmountException(String msg, Throwable t) {
        super(msg, t);
    }
}
