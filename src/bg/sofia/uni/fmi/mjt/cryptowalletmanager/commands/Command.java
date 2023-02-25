package bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.user.User;

public record Command(User user, String command, String[] arguments) {
}
