package bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands;

public class HelpMessage {
    private HelpMessage() {}

    public static String getHelpMessage() {
        return "The commands are:" + Utility.NEWLINE_SYMBOL +
                "login <username> <password> to login into your account" + Utility.NEWLINE_SYMBOL +
                "register <username> <password>  to register" + Utility.NEWLINE_SYMBOL +
                "deposit-money <sum> to deposit money into your account" + Utility.NEWLINE_SYMBOL +
                "list-offerings to list all offerings" + Utility.NEWLINE_SYMBOL +
                "buy --offering=<offering_code> --money=<amount> to buy crypto for the amount of money" + Utility.NEWLINE_SYMBOL +
                "sell --offering=<offering_code> to sell crypto" + Utility.NEWLINE_SYMBOL +
                "get-wallet-summary to display all the data for your wallet" + Utility.NEWLINE_SYMBOL +
                "get-wallet-overall-summary to display detailed data for your wallet" + Utility.NEWLINE_SYMBOL +
                "get-transaction-history to view your transaction history" + Utility.NEWLINE_SYMBOL +
                "logout" + Utility.NEWLINE_SYMBOL +
                "disconnect to disconnect from the server" + Utility.NEWLINE_SYMBOL ;
    }
}
