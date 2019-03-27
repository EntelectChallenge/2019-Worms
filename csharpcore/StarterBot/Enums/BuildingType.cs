using System.Runtime.Serialization;
using Newtonsoft.Json;

namespace StarterBot.Enums
{
    public enum BuildingType
    {
        [JsonProperty("DEFENSE")]
        [EnumMember(Value = "DEFENSE")]
        Defense = 0,
        [JsonProperty("ATTACK")]
        [EnumMember(Value = "ATTACK")]
        Attack = 1,
        [JsonProperty("ENERGY")]
        [EnumMember(Value = "ENERGY")]
        Energy = 2,
        [JsonProperty("TESLA")]
        [EnumMember(Value = "TESLA")]
        Tesla = 3
    }
}