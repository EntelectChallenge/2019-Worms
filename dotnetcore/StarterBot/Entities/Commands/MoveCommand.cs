namespace StarterBot.Entities.Commands
{
    public class MoveCommand: ICommand
    {
        public MapPosition MapPosition { get; set; }

        public string RenderCommand()
        {
            return $"move {MapPosition.X} {MapPosition.Y}";
        }
    }
}
