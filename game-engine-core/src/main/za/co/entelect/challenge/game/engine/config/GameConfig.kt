package za.co.entelect.challenge.game.engine.config

import za.co.entelect.challenge.game.engine.player.Weapon

class GameConfig private constructor(val maxRounds: Int,
                                     val maxDoNothings: Int,
                                     val pushbackDamage: Int,
                                     val commandoWorms: PlayerWormDefinition,
                                     val mapSize: Int,
                                     val healthPackHp: Int)

class PlayerWormDefinition(val count: Int,
                           val initialHp: Int,
                           val movementRage: Int,
                           val diggingRange: Int,
                           val weapon: Weapon)