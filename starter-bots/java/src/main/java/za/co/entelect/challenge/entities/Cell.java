package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.CellType;

public class Cell {
    @SerializedName("x")
    public int x;

    @SerializedName("y")
    public int y;

    @SerializedName("type")
    public CellType type;

    @SerializedName("powerup")
    public PowerUp powerUp;
}
