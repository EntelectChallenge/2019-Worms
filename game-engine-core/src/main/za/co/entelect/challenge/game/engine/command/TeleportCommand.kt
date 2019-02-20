package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm

class TeleportCommand(val target: Point): WormsCommand {
    constructor(x : Int, y: Int):  this(Point(x, y))

    override fun isValid(gameMap: WormsMap, player: Worm): Boolean {


        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun execute(gameMap: WormsMap, player: Worm) {
        val targetCell = gameMap[target]

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}