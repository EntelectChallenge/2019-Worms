package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.config.BotMetaData;

import java.io.IOException;

public class HaskellBotRunner extends BotRunner {

    public HaskellBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) {
        super(botMetaData, timeoutInMilliseconds);
    }

    @Override
    protected void runBot() throws IOException {
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

        runSimpleCommandLineCommand(line, 0);
    }

    @Override
    public int getDockerPort() {
        return 9005;
    }
}
