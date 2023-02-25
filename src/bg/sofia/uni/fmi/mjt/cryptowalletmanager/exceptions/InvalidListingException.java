package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

public class InvalidListingException extends Exception {
    public InvalidListingException(String msg) {
        super(msg);
    }

    public InvalidListingException(String msg, Throwable t) {
        super(msg, t);
    }
}
