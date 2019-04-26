package za.co.entelect.challenge.botrunners;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class PythonBotRunner extends BotRunner {

    private static final Logger log = LogManager.getLogger(PythonBotRunner.class);

    private final String pythonCommand;

    private String[] pythonCommands = new String[]{
            "python3", "py -3", "python", "py",
    };

    public PythonBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) throws Exception {
        super(botMetaData, timeoutInMilliseconds);
        pythonCommand = resolvePythonCommand();
    }

    @Override
    protected void runBot() throws IOException {
        String line = String.format("%s \"%s\"", pythonCommand, this.getBotFileName());
        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9004;
    }

    private String resolvePythonCommand() throws Exception {

        //We don't need to worry about the output from the following commands. We can safely dispose the output.
        PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(null);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);

        for (String command : pythonCommands) {
            try {
                CommandLine cmdLine = CommandLine.parse(String.format("%s --version", command));
                executor.execute(cmdLine);

                log.info(String.format("Successfully to resolved command: %s", command));
                return command;

            } catch (IOException e) {
                log.warn(String.format("Failed to resolve command: %s", command));
            }
        }

        throw new Exception("Failed to resolve python command. Please ensure you have Python 3 installed");
    }
}