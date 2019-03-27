package za.co.entelect.challenge.entities;

import za.co.entelect.challenge.enums.BuildingType;

import java.util.HashMap;

public class GameDetails {
    public int round;
    public int mapWidth;
    public int mapHeight;
    public int roundIncomeEnergy;
    public HashMap<BuildingType, BuildingStats> buildingsStats = new HashMap<>();

}
