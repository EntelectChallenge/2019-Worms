using System.Collections.Generic;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class Player
    {
        public int Id { get; set; }
        public int Score { get; set; }
        public int Health { get; set; }
        public IEnumerable<Worm> Worms { get; set; }
    }
}