package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class Python3BotRunner extends BotRunner {

    public Python3BotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = "py -3 \"" + this.getBotFileName() + "\"";
        } else {
            line = "python \"" + this.getBotFileName() + "\"";
        }

        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9004;
    }
}