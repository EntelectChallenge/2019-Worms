package za.co.entelect.challenge.commands

import za.co.entelect.challenge.enums.Direction.Direction

case class ShootCommand(direction: Direction) extends Command {
  def render(): String = s"shoot ${direction.entryName}"
}