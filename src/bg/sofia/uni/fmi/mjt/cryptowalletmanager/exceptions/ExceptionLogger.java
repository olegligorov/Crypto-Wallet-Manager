package bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionLogger {
    private static final String PATH = "." + File.separator + "resources" + File.separator;
    private static final String LOGFILE_NAME = "logs.txt";
    private static final String LOGDIR = PATH + LOGFILE_NAME;

    public static void setUpLogger() {
        File resources = new File(PATH);
        if (!resources.exists()) {
        resources.mkdirs();
        }
    }

    public static void writeException(Exception exception) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOGDIR, true));
             PrintWriter printWriter = new PrintWriter(bufferedWriter, true)){
            exception.printStackTrace(printWriter);
        } catch (IOException e) {
            throw new RuntimeException("Could not write exception to file", e);
        }
    }
}
