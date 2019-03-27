package za.co.entelect.challenge.enums;

public enum BuildingType {
    DEFENSE(0),
    ATTACK(1),
    ENERGY(2);

    private int type;

    BuildingType(int type) {

        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String buildCommand(int x, int y) {
        return String.format("%d,%d,%d", x, y, getType());
    }
}
