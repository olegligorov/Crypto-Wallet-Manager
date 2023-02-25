package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String msg) {
        super(msg);
    }

    public InsufficientBalanceException(String msg, Throwable t) {
        super(msg, t);
    }
}
