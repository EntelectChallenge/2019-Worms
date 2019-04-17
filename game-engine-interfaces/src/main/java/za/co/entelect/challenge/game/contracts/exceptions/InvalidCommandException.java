package za.co.entelect.challenge.game.contracts.exceptions;

public class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
    public InvalidCommandException(String message, Exception e) {
        super(message, e);
    }
}
