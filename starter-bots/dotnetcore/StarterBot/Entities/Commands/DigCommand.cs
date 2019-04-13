namespace StarterBot.Entities.Commands
{
    public class DigCommand: ICommand
    {
        public MapPosition MapPosition { get; set; }

        public string RenderCommand()
        {
            return $"dig {MapPosition.X} {MapPosition.Y}";
        }
    }
}
