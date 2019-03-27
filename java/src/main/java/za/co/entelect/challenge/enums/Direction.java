package za.co.entelect.challenge.enums;

public enum Direction {
    LEFT(-1),
    RIGHT(1);

    private int multiplier;

    Direction(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
