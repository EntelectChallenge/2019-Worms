namespace StarterBot.Entities.Commands
{
    public class ShootCommand: ICommand
    {
        public string Direction { get; set; } 

        public string RenderCommand()
        {
            return $"shoot {Direction}";
        }
    }
}
