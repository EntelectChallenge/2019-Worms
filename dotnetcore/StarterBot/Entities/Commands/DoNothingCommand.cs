namespace StarterBot.Entities.Commands
{
    public class DoNothingCommand: ICommand
    {
        public string RenderCommand()
        {
            return "nothing";
        }
    }
}
