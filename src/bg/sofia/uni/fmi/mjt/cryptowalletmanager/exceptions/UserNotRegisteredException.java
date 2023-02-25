package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

public class UserNotRegisteredException extends Exception {
    public UserNotRegisteredException(String msg) {
        super(msg);
    }

    public UserNotRegisteredException(String msg, Throwable e) {
        super(msg, e);
    }
}
