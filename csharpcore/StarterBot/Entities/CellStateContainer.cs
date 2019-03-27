using System.Collections.Generic;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class CellStateContainer
    {
        public int X { get; set; } 
        public int Y { get; set; } 
        public PlayerType CellOwner { get; set; } 
        public List<Building> Buildings { get; set; } 
        public List<Missile> Missiles { get; set; } 
    }
}