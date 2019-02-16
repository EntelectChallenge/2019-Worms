package za.co.entelect.challenge.game.engine.entities;

import za.co.entelect.challenge.game.engine.player.WormsPlayer

public class WormsMap(val players: List<WormsPlayer>) {


    val livingPlayers: List<WormsPlayer>
        get() = players.filter { !it.dead }

    val winningPlayer: WormsPlayer?
        get() = null

    var currentRound: Int = 0
}
