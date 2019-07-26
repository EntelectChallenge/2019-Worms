package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

object CommandoWorm : WormBuilder {

    override fun build(id: Int, config: GameConfig, position: Point): Worm {
        return Worm(id = id,
                health = config.commandoWorms.initialHp,
                position = position,
                weapon = Weapon.fromWeapon(config.commandoWorms.weapon),
                snowballs = null,
                diggingRange = config.commandoWorms.diggingRange,
                movementRange = config.commandoWorms.movementRage,
                profession = config.commandoWorms.professionName)
    }

    override fun build(id: Int, config: GameConfig): Worm {
        return Worm(id = id,
                health = config.commandoWorms.initialHp,
                weapon = Weapon.fromWeapon(config.commandoWorms.weapon),
                snowballs = null,
                diggingRange = config.commandoWorms.diggingRange,
                movementRange = config.commandoWorms.movementRage,
                profession = config.commandoWorms.professionName)
    }
}



