using Newtonsoft.Json;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class Cell
    {
        private int X { get; set; }
        private int Y { get; set; }
        private PlayerIdentifier PlayerIdentifier { get; set; }

        public Cell()
        {
        }

        public Cell(int x, int y, PlayerIdentifier playerIdentifier)
        {
            this.X = x;
            this.Y = y;
            this.PlayerIdentifier = playerIdentifier;
        }

        public int getX()
        {
            return X;
        }

        public int getY()
        {
            return Y;
        }

        public PlayerIdentifier getPlayerType()
        {
            return PlayerIdentifier;
        }

        public bool isPlayers(PlayerIdentifier id)
        {
            return id == PlayerIdentifier;
        }
    }
}