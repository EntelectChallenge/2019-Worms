package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class GolangBotRunner extends BotRunner {

    public GolangBotRunner(BotMetaData botMetaData, int timoutInMilis) {
        super(botMetaData, timoutInMilis);
    }

    @Override
    protected void runBot() throws IOException {
        String line = "go run \"" + this.getBotDirectory() + "/" + this.getBotFileName() + "\"";
        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9011;
    }
}
