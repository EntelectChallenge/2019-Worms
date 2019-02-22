package za.co.entelect.challenge.game.engine.entities

import kotlin.random.Random


//TODO: Read from file for JVM module.
//Plus inject it everywhere as far as possible. We don't neccesarily need a DI framework, but do something like it for testability.
public class GameConfig {

    val maxRounds = 0
    val maxDoNothings = 10
    val commandoWorms = PlayerWormDefinition(2, 100, 10)
    val pushbackDamage = 5

    var seed: Long = 0
        set(value) {
            field = value
            random = Random(seed)
        }

    @Transient
    var random: Random = Random

    class PlayerWormDefinition(val count: Int,
                               val initialHp: Int,
                               val attackDamage: Int) {

    }
}
