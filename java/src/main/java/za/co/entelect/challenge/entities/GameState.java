package za.co.entelect.challenge.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameState {
    protected Player[] players;
    protected CellStateContainer[][] gameMap;
    protected GameDetails gameDetails;
    protected Cell[] teslaHitList;

    public List<Player> getPlayers() {
        return new ArrayList<>(Arrays.asList(players));
    }

    public List<CellStateContainer> getGameMap() {
        ArrayList<CellStateContainer> list = new ArrayList<>();

        for (CellStateContainer[] aGameMap : gameMap) {
            list.addAll(Arrays.asList(aGameMap));
        }

        return list;
    }

    public GameDetails getGameDetails() {
        return gameDetails;
    }
}
