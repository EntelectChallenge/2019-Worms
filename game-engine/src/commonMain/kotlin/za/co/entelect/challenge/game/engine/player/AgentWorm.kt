package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
import kotlin.js.JsName

object AgentWorm {

    @JsName("buildWithPositions")
    fun build(id: Int, config: GameConfig, position: Point): Worm {
        return Worm(id = id,
                health = config.agentWorms.initialHp,
                position = position,
                weapon = config.agentWorms.weapon.copy(),
                bananas = config.agentWorms.bananas?.copy(),
                diggingRange = config.agentWorms.diggingRange,
                movementRange = config.agentWorms.movementRage,
                profession = config.agentWorms.professionName)
    }

    @JsName("build")
    fun build(id: Int, config: GameConfig): Worm {
        return Worm(id = id,
                health = config.agentWorms.initialHp,
                weapon = config.agentWorms.weapon.copy(),
                bananas = config.agentWorms.bananas?.copy(),
                diggingRange = config.agentWorms.diggingRange,
                movementRange = config.agentWorms.movementRage,
                profession = config.agentWorms.professionName)
    }
}



