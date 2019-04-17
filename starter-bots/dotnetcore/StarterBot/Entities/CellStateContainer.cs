using System;
using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class CellStateContainer
    {
        public int X { get; set; } 
        public int Y { get; set; }
        public Worm Occupier { get; set; }
        public PowerUp PowerUp { get; set; }
        [JsonConverter(typeof(StringEnumConverter))]
        public CellType Type { get; set; }
    }
}