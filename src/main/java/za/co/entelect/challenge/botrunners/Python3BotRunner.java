package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;

import java.io.IOException;

public class Python3BotRunner extends BotRunner {

    public Python3BotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected String runBot() throws IOException, TimeoutException {
        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = "py -3 \"" + this.getBotFileName() + "\"";
        } else {
            line = "python \"" + this.getBotFileName() + "\"";
        }

        return RunSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9004;
    }
}