package za.co.entelect.challenge.game.engine.entities

//TODO: Read from file.
//But after modules plit out into web/jvm/shared
//Plus inject it everywhere as far as possible. We don't neccesarily need a DI framework, but do something like it for testability.
public class GameConfig {

    val maxRounds = 0
    val maxDoNothings = 10
    val commandoWorms = PlayerWormDefinition(2, 100, 10)
}

class PlayerWormDefinition(val count: Int, val initialHp: Int, val attackDamage: Int) {

}