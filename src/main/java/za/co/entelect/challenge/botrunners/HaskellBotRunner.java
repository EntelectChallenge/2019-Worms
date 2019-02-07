package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.entities.BotArguments;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;

import java.io.IOException;

public class HaskellBotRunner extends BotRunner {

    public HaskellBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected String runBot() throws IOException, TimeoutException {
        String runTimeArguments;
        if (this.getArguments() != null) {
            int coreCount = this.getArguments().getCoreCount();
            runTimeArguments = " +RTS -N" + Integer.toString(coreCount) + " -RTS";
        } else {
            runTimeArguments = "";
        }

        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = "cmd /c \"" + this.getBotFileName() + runTimeArguments + "\"";
        } else {
            line = "./" + this.getBotFileName() + runTimeArguments;
        }

        return RunSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9005;
    }
}
