package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

interface WormBuilder {

    fun build(id: Int, config: GameConfig, position: Point): Worm
    fun build(id: Int, config: GameConfig): Worm
}
