package bg.sofia.uni.fmi.mjt.cryptowalletmanager.user;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.UserNotRegisteredException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase implements Serializable {
    private Map<String, User> users;
    private final String database;

    public UserDatabase(String database) {
        this.users = new HashMap<>();
        this.database = database;
        readFromDatabase();
    }

    public void readFromDatabase() {
        users = restoreProfilesFromDatabase();
    }

    private Map<String, User> restoreProfilesFromDatabase() {
        File userDatabaseFile = new File(database);

        if (userDatabaseFile.exists() && userDatabaseFile.length() != 0) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(database))) {
                return (Map<String, User>) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                ExceptionLogger.writeException(e);
                System.err.println("Problem restoring profiles from database\n");
            }
        }
        return new HashMap<>();
    }

    public void setUpDatabase() {
        File resources = new File("resources");
        if (!resources.exists()) {
            resources.mkdirs();
        }
    }

    public void writeAccountsInDatabase() {
        setUpDatabase();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(database, false))) {
            outputStream.writeObject(users);
        } catch (IOException e) {
            ExceptionLogger.writeException(e);
            System.err.println("Problem writing in accounts database!\n" + e.getMessage());
        }
    }

    public User registerUser(String username, String password) {
        User newUser = new User(username, password);
        users.put(username, newUser);
        return newUser;
    }

    public User getUser(String username) throws UserNotRegisteredException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new UserNotRegisteredException("User is not registered");
    }

    public boolean containsUser(String username) {
        return users.containsKey(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
