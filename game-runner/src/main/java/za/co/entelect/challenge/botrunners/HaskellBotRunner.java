package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class HaskellBotRunner extends BotRunner {

    public HaskellBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
        String line;

        if(System.getProperty("os.name").contains("Windows")) {
            line = this.getBotDirectory() + "\\" + this.getBotFileName() + ".exe";
        } else {
            line = this.getBotDirectory() + "/" + this.getBotFileName();
        }

        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9004;
    }
}