package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.PowerUpType;

public class PowerUp {
    @SerializedName("type")
    public PowerUpType type;

    @SerializedName("value")
    public int value;
}
