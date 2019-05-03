package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class JavaBotRunner extends BotRunner {
    public JavaBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line = "java -jar \"" + this.getBotFileName() + "\"";
        runSimpleCommandLineCommand(line, 0);
    }

}
