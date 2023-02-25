package bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.User;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private CommandCreator() {}

    public static Command newCommand(User user, String input) {
        List<String> tokens = Arrays.stream(input.split(" ")).toList();
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);
        return new Command(user, tokens.get(0), args);
    }
}
