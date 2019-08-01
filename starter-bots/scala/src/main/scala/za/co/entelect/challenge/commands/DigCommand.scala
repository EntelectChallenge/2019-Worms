package za.co.entelect.challenge.commands

case class DigCommand(x: Int, y: Int) extends Command {
  def render(): String = s"dig $x $y"
}
