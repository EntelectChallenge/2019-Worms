package za.co.entelect.challenge.game.engine.entities

//TODO: Replace with factory class read from file.
//But after modules plit out into web/jvm/shared
//Plus inject it everywhere as far as possible. We don't neccesarily need a DI framework, but do something like it for testability.
public class GameConfig {

    val maxRounds = 0
    val maxDoNothings = 10
}