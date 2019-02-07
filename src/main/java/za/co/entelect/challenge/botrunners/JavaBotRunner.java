package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;

import java.io.IOException;

public class JavaBotRunner extends BotRunner {
    public JavaBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected String runBot() throws IOException, TimeoutException {
        String line = "java -jar \"" + this.getBotFileName() + "\"";
        return RunSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9002;
    }
}
