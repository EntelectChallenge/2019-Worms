package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError

class CommandExecutor(val player: WormsPlayer,
                      val map: WormsMap,
                      val command: WormsCommand) {

    val worm = player.currentWorm
    // Moves are validated on command executor construction
    private val moveValidation = command.validate(map, worm)

    fun execute() {
        if (moveValidation.isValid) {
            command.execute(map, worm)
            player.doNothingsCount = 0
        } else {
            player.doNothingsCount++
            map.errorList.add(GameError(moveValidation.reason, player, worm, map.currentRound))
        }
    }

}