package za.co.entelect.challenge.commands

case class MoveCommand(x: Int, y: Int) extends Command {
  def render(): String = s"move $x $y"
}