using Newtonsoft.Json;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class Cell
    {
        public int X { get; set; }
        public int Y { get; set; }
        public PlayerType PlayerType { get; set; }

        public Cell()
        {
        }

        public Cell(int x, int y, PlayerType playerType)
        {
            this.X = x;
            this.Y = y;
            this.PlayerType = playerType;
        }

        public int getX()
        {
            return X;
        }

        public int getY()
        {
            return Y;
        }

        public PlayerType getPlayerType()
        {
            return PlayerType;
        }

        public bool isPlayers(PlayerType id)
        {
            return id == PlayerType;
        }
    }
}