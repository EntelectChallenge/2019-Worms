package za.co.entelect.challenge.game.engine.entities;

import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.random.Random

public class WormsMap(val players: List<WormsPlayer>,
                      val rows: Int,
                      val columns: Int,
                      val cells: MutableList<MapCell>,
                      val config: GameConfig = GameConfig()) {

    val livingPlayers: List<WormsPlayer>
        get() = players.filter { !it.dead }

    val winningPlayer: WormsPlayer?
        get() = null

    var currentRound: Int = 0


    operator fun get(target: Point): MapCell {
        return this[target.x, target.y]
    }

    operator fun get(x: Int, y: Int): MapCell {
        return cells[y * columns + x]
    }
}
