package za.co.entelect.challenge.game.contracts.command;

import za.co.entelect.challenge.game.contracts.game.GamePlayer;
import za.co.entelect.challenge.game.contracts.map.GameMap;

public class RawCommand implements Command {

    private String command;

    public RawCommand() {
        this("");
    }

    public RawCommand(String command) {
        this.command = command;
    }

    @Override
    public void performCommand(GameMap gameMap, GamePlayer player) {

    }

    @Override
    public boolean isValid() {
        return true;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
