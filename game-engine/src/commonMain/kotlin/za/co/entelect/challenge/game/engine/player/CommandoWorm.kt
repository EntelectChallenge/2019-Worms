package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
import kotlin.js.JsName

object CommandoWorm {

    @JsName("buildWithPositions")
    fun build(id: Int, config: GameConfig, position: Point): Worm {
        return Worm(id = id,
                health = config.commandoWorms.initialHp,
                position = position,
                weapon = config.commandoWorms.weapon,
                diggingRange = config.commandoWorms.diggingRange,
                movementRange = config.commandoWorms.movementRage,
                profession = config.commandoWorms.professionName)
    }

    @JsName("build")
    fun build(id: Int, config: GameConfig): Worm {
        return Worm(id = id,
                health = config.commandoWorms.initialHp,
                weapon = config.commandoWorms.weapon,
                diggingRange = config.commandoWorms.diggingRange,
                movementRange = config.commandoWorms.movementRage,
                profession = config.commandoWorms.professionName)
    }
}



