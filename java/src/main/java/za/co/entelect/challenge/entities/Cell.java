package za.co.entelect.challenge.entities;

import za.co.entelect.challenge.enums.PlayerType;

public class Cell {

    protected int x;
    protected int y;
    protected PlayerType playerType;

    public Cell() {

    }

    public Cell(int x, int y, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.playerType = playerType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public boolean isPlayers(PlayerType id) {
        return id == playerType;
    }
}
