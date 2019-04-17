using System;
using System.Collections.Generic;
using System.Text;

namespace StarterBot.Entities
{
    public class Worm
    {
        public int Id { get; set; }
        public int PlayerId { get; set; }
        public int Health { get; set; }
        public MapPosition Position { get; set; }
        public WeaponDetails Weapon { get; set; }
        public int DiggingRange { get; set; }
        public int MovementRange { get; set; }
    }
}
