using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class Player
    {
        public PlayerType PlayerType { get; set; }
        public int Energy { get; set; }
        public int Health { get; set; }
        public int HitsTaken { get; set; }
        public int Score { get; set; }
    }
}