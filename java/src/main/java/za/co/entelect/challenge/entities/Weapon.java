package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Weapon {
    @SerializedName("damage")
    public int damage;

    @SerializedName("range")
    public int range;
}
