using System;
using System.Collections.Generic;
using System.Text;

namespace StarterBot.Entities
{
    public class BuildingStats
    {
        public int Health;
        public int ConstructionTime;
        public int Price;

        //Weapon details, applicable only to attack buildings
        public int WeaponDamage;
        public int WeaponSpeed;
        public int WeaponCooldownPeriod;

        // Energy generation details, only applicable to energy buildings
        public int EnergyGeneratedPerTurn;

        // Score details
        public int DestroyMultiplier;
        public int ConstructionScore;
    }
}
