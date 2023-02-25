package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

public class CryptoListingsException extends Exception {
    public CryptoListingsException(String msg) {
        super(msg);
    }

    public CryptoListingsException(String msg, Throwable t) {
        super(msg, t);
    }
}
