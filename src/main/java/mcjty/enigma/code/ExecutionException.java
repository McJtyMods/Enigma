package mcjty.enigma.code;

public class ExecutionException extends Exception {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable parent) {
        super(message, parent);
    }
}
