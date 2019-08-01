package za.co.entelect.challenge.botrunners;

import org.apache.commons.exec.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.handlers.CommandHandler;
import za.co.entelect.challenge.config.BotArguments;
import za.co.entelect.challenge.config.BotMetaData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BotRunner implements ProcessDestroyer {

    private static final Logger log = LogManager.getLogger(BotRunner.class);

    protected BotMetaData botMetaData;
    protected int timeoutInMilliseconds;

    private CommandHandler commandHandler;
    private List<Process> processes;
    private ReentrantLock lock;
    private boolean stopped = false;

    protected BotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        this.botMetaData = botMetaData;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
        this.commandHandler = new CommandHandler(timeoutInMilliseconds);
        this.processes = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void run() throws IOException {
        this.runBot();
    }

    protected abstract void runBot() throws IOException;

    public String getBotDirectory() {
        return botMetaData.getBotDirectory();
    }

    public String getBotFileName() {
        return botMetaData.getBotFileName();
    }

    public BotArguments getArguments() {
        return botMetaData.getArguments();
    }

    protected void runSimpleCommandLineCommand(String line, int expectedExitValue) throws IOException {
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        File bot = new File(this.getBotDirectory());

        executor.setWorkingDirectory(bot);
        executor.setExitValue(expectedExitValue);
        executor.setStreamHandler(commandHandler);
        executor.setProcessDestroyer(this);
        executor.execute(cmdLine, new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                if (!stopped) {
                    log.info("Bot process completed successfully");
                }
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                if (!stopped) {
                    log.error("Bot process failed", e);
                }
            }
        });
    }

    @Override
    public boolean add(Process process) {
        lock.lock();
        try {
            return processes.add(process);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Process process) {
        lock.lock();
        try {
            return processes.remove(process);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return processes.size();
    }

    public void newRound(int round) {
        commandHandler.signalNewRound(round);
    }

    public String getLastCommand() {
        return commandHandler.getBotCommand();
    }

    public String getLastError() {
        return commandHandler.getBotError();
    }

    public void shutdown() {
        lock.lock();
        try {
            stopped = true;
            try {
                commandHandler.stop();
            } catch (IOException e) {
                log.error("Failed to stop command handler", e);
            }

            for (Process process : processes) {
                process.destroyForcibly();
            }
        } finally {
            lock.unlock();
        }
    }
}
