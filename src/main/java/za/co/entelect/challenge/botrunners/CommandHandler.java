package za.co.entelect.challenge.botrunners;

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CommandHandler implements ExecuteStreamHandler {

    private static final Logger log = LogManager.getLogger(CommandHandler.class);

    private int timeoutInMilliseconds;

    private OutputStream botInputStream;
    private InputStream botErrorStream;
    private InputStream botOutputStream;

    private BotInputHandler botInputHandler;
    private BotErrorHandler botErrorHandler;

    private Thread botInputThread;
    private Thread botErrorThread;

    private ReentrantLock reentrantLock = new ReentrantLock(true);
    private Condition botReadyCondition = reentrantLock.newCondition();
    private Condition commandSignalCondition = reentrantLock.newCondition();

    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);

    public CommandHandler(int timeoutInMilliseconds) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    @Override
    public void setProcessInputStream(OutputStream os) throws IOException {
        this.botInputStream = os;
    }

    @Override
    public void setProcessErrorStream(InputStream is) throws IOException {
        this.botErrorStream = is;
    }

    @Override
    public void setProcessOutputStream(InputStream is) throws IOException {
        botOutputStream = is;
    }

    @Override
    public void start() throws IOException {

        reentrantLock.lock();
        started.set(true);

        botInputHandler = new BotInputHandler(botOutputStream, reentrantLock, commandSignalCondition);
        botErrorHandler = new BotErrorHandler(botErrorStream);

        botInputThread = new Thread(botInputHandler);
        botErrorThread = new Thread(botErrorHandler);

        botInputThread.start();
        botErrorThread.start();

        //Wait for bot streams to start listening
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            //We can ignore this
        }

        botReadyCondition.signal();
        reentrantLock.unlock();
    }

    @Override
    public void stop() throws IOException {
        botInputThread.interrupt();
        botErrorThread.interrupt();

        stopped.set(true);
        reentrantLock.lock();
        commandSignalCondition.signal();
        reentrantLock.unlock();
    }

    public String getBotCommand() {
        if (!stopped.get()) {
            reentrantLock.lock();
            try {
                commandSignalCondition.await(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
                if (!stopped.get()) {
                    return botInputHandler.getLastReceivedCommand();
                }
            } catch (InterruptedException e) {
                log.error("Bot exceeded time limit", e);
            } finally {
                reentrantLock.unlock();
            }
        }

        return "";
    }

    public void signalNewRound(int round) {

        reentrantLock.lock();

        if (!started.get()) {
            botReadyCondition.awaitUninterruptibly();
        }

        try {
            botInputHandler.setCurrentRound(round);

            if (!stopped.get()) {
                botInputStream.write(Integer.toString(round).getBytes());
                botInputStream.write(System.lineSeparator().getBytes());
                botInputStream.flush();
            }
        } catch (IOException e) {
            log.error("Failed to notify bot of new round", e);
        }

        reentrantLock.unlock();
    }
}
