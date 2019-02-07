package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class BotArguments {
    @SerializedName("coreCount")
    private int coreCount;

    public BotArguments(int coreCount) {
        this.coreCount = coreCount;
    }

    public int getCoreCount() {
        return this.coreCount;
    }
}
