package bg.sofia.uni.fmi.mjt.cryptowalletmanager;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.Utility;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.ExceptionLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CryptoWalletClient {
    private static final int SERVER_PORT = 5555;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) {
        new CryptoWalletClient().start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server, type help to see the full list of commands");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();
                writer.println(message);
                String reply = reader.readLine();

                //in the returned replies NEWLINE_SYMBOL is used as a symbol for a new line
                reply = reply.replace(Utility.NEWLINE_SYMBOL, "\n");
                System.out.println(reply);

                if ("disconnect".equals(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            ExceptionLogger.writeException(e);
            System.err.println("Try again later or contact administrator by providing the logs in /resources/logs.txt");
        }
    }
}
