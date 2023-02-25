package bg.sofia.uni.fmi.mjt.cryptowalletmanager;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.Utility;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.User;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.UserDatabase;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CryptoWalletServer {
    private static final int SERVER_PORT = 5555;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 204800;
    private final ByteBuffer messageBuffer;
    private final CommandExecutor commandExecutor;
    private UserDatabase users;
    private Map<SocketAddress, User> loggedUsers = new HashMap<>();

    private static final String databaseLocation = "." + File.separator + "resources" + File.separator + "users.txt";

    private boolean isStarted = true;
    private Selector selector;

    public static void main(String[] args) {
        new CryptoWalletServer().start();
    }

    public CryptoWalletServer() {
        ExceptionLogger.setUpLogger();
        users = new UserDatabase(databaseLocation);
        this.messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.commandExecutor = new CommandExecutor(users);
        System.out.println("The server is running");
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (isStarted) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        messageBuffer.clear();
                        int r = socketChannel.read(messageBuffer);
                        if (r <= 0) {
                            System.out.println("Nothing to read, closing channel");
                            users.writeAccountsInDatabase();
                            socketChannel.close();
                            continue;
                        }

                        handleKeyIsReadable(key, messageBuffer);
                    } else if (key.isAcceptable()) {
                        handleKeyIsAcceptable(selector, key);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            ExceptionLogger.writeException(e);
            System.err.println("There is a problem with the server socket: " + e.getMessage());
        }

        users.writeAccountsInDatabase();
        System.out.println("Server stopped");
    }

    public void stop() {
        isStarted = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
        users.writeAccountsInDatabase();
    }

    private void handleKeyIsAcceptable(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void handleKeyIsReadable(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();
        StringBuilder response = commandExecutor.executeCommand(message, clientChannel.getRemoteAddress(), loggedUsers);

        if (response.length() != 0) {
            String serverOutput = response.toString().replace(Utility.NEWLINE_SYMBOL, System.lineSeparator());
            System.out.println("Sending response to client: " + serverOutput);
            response.append(System.lineSeparator());
            buffer.clear();
            buffer.put(response.toString().getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            clientChannel.write(buffer);
        }
    }
}
