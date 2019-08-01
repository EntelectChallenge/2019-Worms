package za.co.entelect.challenge.entities

case class GameState(currentRound: Int,
                     maxRounds: Int,
                     mapSize: Int,
                     currentWormId: Int,
                     consecutiveDoNothingCount: Int,
                     myPlayer: MyPlayer,
                     opponents: List[Opponent],
                     map: List[List[Cell]])