package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class CPlusPlusBotRunner extends BotRunner {

    public CPlusPlusBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = "cmd /c \"" + this.getBotFileName() + "\"";
        } else {
            line = "\"./" + this.getBotFileName() + "\"";
        }

        runSimpleCommandLineCommand(line, 0);
    }

}
