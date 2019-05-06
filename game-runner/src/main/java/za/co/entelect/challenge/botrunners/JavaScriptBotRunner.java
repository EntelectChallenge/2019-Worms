package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class JavaScriptBotRunner extends BotRunner {
    public JavaScriptBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line = "node \"" + this.getBotFileName() + "\"";
        runSimpleCommandLineCommand(line, 0);
    }

}
