package za.co.entelect.challenge.game.engine.config

import za.co.entelect.challenge.game.engine.player.Bananas
import za.co.entelect.challenge.game.engine.player.Snowballs
import za.co.entelect.challenge.game.engine.player.Weapon

class GameConfig private constructor(val maxRounds: Int,
                                     val maxDoNothings: Int,
                                     val pushbackDamage: Int,
                                     val lavaDamage: Int,
                                     val commandoWorms: PlayerWormDefinition,
                                     val agentWorms: PlayerWormDefinition,
                                     val technologistWorms: PlayerWormDefinition,
                                     val mapSize: Int,
                                     val healthPackHp: Int,
                                     val totalHealthPacks: Int,
                                     val scores: Scores,
                                     val csvSeparator: String,
                                     val wormSelectTokens: SelectTokenConfig,
                                     val battleRoyaleStart: Double,
                                     val battleRoyaleEnd: Double)

class PlayerWormDefinition(val count: Int,
                           val initialHp: Int,
                           val movementRage: Int,
                           val diggingRange: Int,
                           val weapon: Weapon,
                           val bananas: Bananas?,
                           val snowballs: Snowballs?,
                           val professionName: String)

class Scores(val attack: Int,
             val killShot: Int,
             val missedAttack: Int,
             val powerup: Int,
             val freeze: Int,
             val dig: Int,
             val move: Int,
             val invalidCommand: Int,
             val doNothing: Int)

class SelectTokenConfig(val count: Int)
