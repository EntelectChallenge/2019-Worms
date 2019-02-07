package za.co.entelect.challenge.botrunners;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.entities.BotArguments;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public abstract class BotRunner {

    protected BotMetaData botMetaData;
    protected int timeoutInMilliseconds;

    protected BotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        this.botMetaData = botMetaData;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    public String run() throws IOException, TimeoutException {
        return this.runBot();
    }

    protected abstract String runBot() throws IOException, TimeoutException;

    public abstract int getDockerPort();

    public String getBotDirectory() {
        return botMetaData.getBotDirectory();
    }

    public String getBotFileName() {
        return botMetaData.getBotFileName();
    }

    public BotArguments getArguments() { return botMetaData.getArguments(); }

    protected String RunSimpleCommandLineCommand(String line, int expectedExitValue) throws IOException, TimeoutException {
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        File bot = new File(this.getBotDirectory());
        executor.setWorkingDirectory(bot);
        executor.setExitValue(expectedExitValue);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(this.timeoutInMilliseconds);
        executor.setWatchdog(watchdog);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);

        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            if (watchdog.killedProcess()) {
                throw new TimeoutException("Bot process timed out after " + this.timeoutInMilliseconds + "ms of inactivity");
            } else {
                throw e;
            }
        }

        return outputStream.toString();
    }
}
