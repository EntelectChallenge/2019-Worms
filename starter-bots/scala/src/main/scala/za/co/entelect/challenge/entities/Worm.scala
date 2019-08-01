package za.co.entelect.challenge.entities

trait Worm {
  val id: Int
  val health: Int
  val position: Position
  val diggingRange: Int
  val movementRange: Int
}
