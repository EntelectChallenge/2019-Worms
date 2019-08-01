package za.co.entelect.challenge.botrunners.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Scanner;

public class BotErrorHandler implements Runnable {

    private static final Logger log = LogManager.getLogger(BotErrorHandler.class);

    private InputStream botErrorStream;
    private String lastErrorMessage;
    private int round;

    public BotErrorHandler(InputStream botErrorStream) {
        this.botErrorStream = botErrorStream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(botErrorStream);

        while (scanner.hasNextLine()) {
            String error = scanner.nextLine();

            lastErrorMessage = error;
            log.error(lastErrorMessage);
        }
    }

    public String getLastError() {
        return lastErrorMessage;
    }

    public void setCurrentRound(int round) {
        this.round = round;
        lastErrorMessage = null;
    }
}
