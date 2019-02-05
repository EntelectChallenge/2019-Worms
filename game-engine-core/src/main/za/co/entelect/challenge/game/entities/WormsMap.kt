package za.co.entelect.challenge.game.entities;

public class WormsMap(val players: List<WormsPlayer>)  {


    val livingPlayers: List<WormsPlayer>
        get() = players.filter { !it.dead }
}
