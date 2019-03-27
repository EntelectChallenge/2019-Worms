package za.co.entelect.challenge.entities;

import za.co.entelect.challenge.enums.PlayerType;

import java.util.ArrayList;
import java.util.List;

public class CellStateContainer {
    public int x;
    public int y;
    public PlayerType cellOwner;
    protected List<Building> buildings;
    protected List<Missile> missiles;

    public CellStateContainer(int x, int y, PlayerType cellOwner) {
        this.x = x;
        this.y = y;
        this.cellOwner = cellOwner;
        this.buildings = new ArrayList<>();
        this.missiles = new ArrayList<>();
    }

    public List<Building> getBuildings() {
        return this.buildings;
    }

    public List<Missile> getMissiles() {
        return this.missiles;
    }

}
