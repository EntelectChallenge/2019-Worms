package za.co.entelect.challenge.game.engine.entities

import za.co.entelect.challenge.game.engine.player.Weapon

//TODO: Read from file for JVM module.
//Plus inject it everywhere as far as possible. We don't necessarily need a DI framework, but do something like it for testability.
class GameConfig {

    val maxRounds = 0
    val maxDoNothings = 10
    val commandoWorms = PlayerWormDefinition(3, 100, Weapon(1, 3))
    val pushbackDamage = 5

    val mapColumns = 32
    val mapRows = 32

    class PlayerWormDefinition(val count: Int,
                               val initialHp: Int,
                               val weapon: Weapon,
                               val movementRage: Int = 1,
                               val diggingRange: Int = 1)

    val healthPackHp = 5
}
