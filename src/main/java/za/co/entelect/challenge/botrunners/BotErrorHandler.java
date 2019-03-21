package za.co.entelect.challenge.botrunners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Scanner;

public class BotErrorHandler implements Runnable {

    private static final Logger log = LogManager.getLogger(BotErrorHandler.class);

    private InputStream botErrorStream;

    public BotErrorHandler(InputStream botErrorStream) {
        this.botErrorStream = botErrorStream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(botErrorStream);

        while (scanner.hasNextLine()) {
            log.error(scanner.nextLine());
        }
    }
}
