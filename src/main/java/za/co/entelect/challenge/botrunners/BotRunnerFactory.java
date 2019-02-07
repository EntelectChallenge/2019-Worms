package za.co.entelect.challenge.botrunners;

import za.co.entelect.challenge.engine.exceptions.InvalidRunnerState;
import za.co.entelect.challenge.entities.BotMetaData;

public class BotRunnerFactory {
    public static BotRunner createBotRunner(BotMetaData botMetaData, int timeoutInMilliseconds) throws InvalidRunnerState {
        switch (botMetaData.getBotLanguage()){
            case JAVA:
                return new JavaBotRunner(botMetaData, timeoutInMilliseconds);
            case CSHARPCORE:
                return new CSharpCoreBotRunner(botMetaData, timeoutInMilliseconds);
            case JAVASCRIPT:
                return new JavaScriptBotRunner(botMetaData, timeoutInMilliseconds);
            case RUST:
                return new RustBotRunner(botMetaData, timeoutInMilliseconds);
            case CPLUSPLUS:
                return new CPlusPlusBotRunner(botMetaData, timeoutInMilliseconds);
            case PYTHON2:
                return new Python2BotRunner(botMetaData, timeoutInMilliseconds);
            case PYTHON3:
                return new Python3BotRunner(botMetaData, timeoutInMilliseconds);
            case KOTLIN:
                return new KotlinBotRunner(botMetaData, timeoutInMilliseconds);
            case GOLANG:
                return new GolangBotRunner(botMetaData, timeoutInMilliseconds);
            case HASKELL:
                return new HaskellBotRunner(botMetaData, timeoutInMilliseconds);
            case LISP:
                return new LispBotRunner(botMetaData, timeoutInMilliseconds);
            case PHP:
                return new PHPBotRunner(botMetaData, timeoutInMilliseconds);
            case SCALA:
                return new ScalaBotRunner(botMetaData, timeoutInMilliseconds);
            default:
                break;
        }
        throw new InvalidRunnerState("Invalid bot language");
    }
}
