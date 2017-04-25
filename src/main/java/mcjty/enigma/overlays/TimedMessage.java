package mcjty.enigma.overlays;

public class TimedMessage {
    private final String message;
    private int timeleft;

    public TimedMessage(String message, int timeleft) {
        this.message = message;
        this.timeleft = timeleft;
    }

    public String getMessage() {
        return message;
    }

    public int getTimeleft() {
        return timeleft;
    }

    public void setTimeleft(int timeleft) {
        this.timeleft = timeleft;
    }
}
