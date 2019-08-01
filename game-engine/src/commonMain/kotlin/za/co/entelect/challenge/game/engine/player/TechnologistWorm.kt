package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

object TechnologistWorm : WormBuilder {

    override fun build(id: Int, config: GameConfig, position: Point): Worm {
        return Worm(id = id,
                health = config.technologistWorms.initialHp,
                position = position,
                weapon = Weapon.fromWeapon(config.technologistWorms.weapon),
                snowballs = Snowballs.fromSnowballs(config.technologistWorms.snowballs),
                diggingRange = config.technologistWorms.diggingRange,
                movementRange = config.technologistWorms.movementRage,
                profession = config.technologistWorms.professionName)
    }

    override fun build(id: Int, config: GameConfig): Worm {
        return Worm(id = id,
                health = config.technologistWorms.initialHp,
                weapon = Weapon.fromWeapon(config.technologistWorms.weapon),
                snowballs = Snowballs.fromSnowballs(config.technologistWorms.snowballs),
                diggingRange = config.technologistWorms.diggingRange,
                movementRange = config.technologistWorms.movementRage,
                profession = config.technologistWorms.professionName)
    }
}



