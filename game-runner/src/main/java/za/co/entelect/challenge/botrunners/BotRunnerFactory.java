package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.engine.exceptions.InvalidRunnerState;
import za.co.entelect.challenge.config.BotMetaData;
import za.co.entelect.challenge.enums.BotLanguage;

public class BotRunnerFactory {
    public static BotRunner createBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) throws Exception {
        BotLanguage botLanguage = botMetaData.getBotLanguage();

        if (botLanguage == null) {
            throw new InvalidRunnerState("Invalid bot language");
        }

        switch (botLanguage){
            case JAVA:
                return new JavaBotRunner(botMetaData, timeoutInMilliseconds);
            case DOTNETCORE:
                return new DotNetCoreBotRunner(botMetaData, timeoutInMilliseconds);
            case JAVASCRIPT:
                return new JavaScriptBotRunner(botMetaData, timeoutInMilliseconds);
            case PYTHON3:
                return new PythonBotRunner(botMetaData, timeoutInMilliseconds);
            case CPLUSPLUS:
                return new CPlusPlusBotRunner(botMetaData, timeoutInMilliseconds);
            case HASKELL:
                return new HaskellBotRunner(botMetaData, timeoutInMilliseconds);
            case SCALA:
                return new ScalaBotRunner(botMetaData, timeoutInMilliseconds);
            case RUST:
                return new RustBotRunner(botMetaData, timeoutInMilliseconds);
            default:
                throw new InvalidRunnerState("No runner found for bot language " + botLanguage);
        }
    }
}
