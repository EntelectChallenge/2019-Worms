package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class Python2BotRunner extends BotRunner {

    public Python2BotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = "py -2 \"" + this.getBotFileName() + "\"";
        } else {
            line = "python \"" + this.getBotFileName() + "\"";
        }

        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9003;
    }
}