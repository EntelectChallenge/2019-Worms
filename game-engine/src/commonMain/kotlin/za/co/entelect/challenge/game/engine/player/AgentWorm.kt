package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

object AgentWorm : WormBuilder {

    override fun build(id: Int, config: GameConfig, position: Point): Worm {
        return Worm(id = id,
                health = config.agentWorms.initialHp,
                position = position,
                weapon = Weapon.fromWeapon(config.agentWorms.weapon),
                bananas = Bananas.fromBananas(config.agentWorms.bananas),
                diggingRange = config.agentWorms.diggingRange,
                movementRange = config.agentWorms.movementRage,
                profession = config.agentWorms.professionName)
    }

    override fun build(id: Int, config: GameConfig): Worm {
        return Worm(id = id,
                health = config.agentWorms.initialHp,
                weapon = Weapon.fromWeapon(config.agentWorms.weapon),
                bananas = Bananas.fromBananas(config.agentWorms.bananas),
                diggingRange = config.agentWorms.diggingRange,
                movementRange = config.agentWorms.movementRage,
                profession = config.agentWorms.professionName)
    }
}



