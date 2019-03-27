using StarterBot.Enums;

namespace StarterBot.Entities
{
    public class Building : Cell
    {
        public int Health { get; set; }
        public int ConstructionTimeLeft { get; set; }
        public int Price { get; set; }
        public int WeaponDamage { get; set; }
        public int WeaponSpeed { get; set; }
        public int WeaponCooldownTimeLeft { get; set; }
        public int WeaponCooldownPeriod { get; set; }
        public int DestroyScore { get; set; }
        public int EnergyGeneratedPerTurn { get; set; }
        public BuildingType BuildingType { get; set; }
    }
}