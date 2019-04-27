package za.co.entelect.challenge.entities

case class EnemyWorm(id: Int,
                     health: Int,
                     position: Position,
                     diggingRange: Int,
                     movementRange: Int) extends Worm