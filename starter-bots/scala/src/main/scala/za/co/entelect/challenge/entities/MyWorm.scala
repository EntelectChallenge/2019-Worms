package za.co.entelect.challenge.entities

case class MyWorm(weapon: Weapon,
                  id: Int,
                  health: Int,
                  position: Position,
                  diggingRange: Int,
                  movementRange: Int) extends Worm