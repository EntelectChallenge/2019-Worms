package za.co.entelect.challenge.botrunners.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotInputHandler implements Runnable {

    private static final Logger log = LogManager.getLogger(BotInputHandler.class);

    private InputStream botInputStream;
    private ReentrantLock reentrantLock;
    private Condition commandSignalCondition;
    private String lastReceivedCommand;
    private int round;

    public BotInputHandler(InputStream botInputStream, ReentrantLock reentrantLock, Condition commandSignalCondition) {
        this.botInputStream = botInputStream;
        this.reentrantLock = reentrantLock;
        this.commandSignalCondition = commandSignalCondition;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(botInputStream);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            log.info(command);

            String commandFormat = String.format("C;%d;", round);
            if (command.startsWith(commandFormat)) {
                reentrantLock.lock();
                commandSignalCondition.signal();
                lastReceivedCommand = command.replaceFirst(commandFormat, "");
                reentrantLock.unlock();
            }
        }
    }

    public String getLastReceivedCommand() {
        return lastReceivedCommand;
    }

    public void setCurrentRound(int round) {
        this.round = round;
        lastReceivedCommand = null;
    }
}
