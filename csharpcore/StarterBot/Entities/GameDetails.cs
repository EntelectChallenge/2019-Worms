using System;
using System.Collections.Generic;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class GameDetails
    {
        public int Round { get; set; }
        public int MapWidth { get; set; }
        public int MapHeight { get; set; }
        public Dictionary<BuildingType, BuildingStats> BuildingsStats { get; set; }
    }
}