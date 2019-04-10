using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class PowerUp
    {
        [JsonConverter(typeof(StringEnumConverter))]
        public PowerUpType Type { get; set; }
        public int Value { get; set; }
    }
}
