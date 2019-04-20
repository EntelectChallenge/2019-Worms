package za.co.entelect.challenge.commands

case class DoNothingCommand() extends Command {
  def render(): String = "nothing"
}
