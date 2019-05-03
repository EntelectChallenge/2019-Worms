package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class DotNetCoreBotRunner extends BotRunner {

    public DotNetCoreBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line = "dotnet " + this.getBotFileName();
        runSimpleCommandLineCommand(line, 0);
    }

}
