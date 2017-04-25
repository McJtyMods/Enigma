package mcjty.enigma.overlays;

import java.util.ArrayList;
import java.util.List;

public class TimedMessages {
    private static List<TimedMessage> timedMessages = new ArrayList<>();

    public static void tick() {
        List<TimedMessage> newMessages = new ArrayList<>();
        for (TimedMessage m : timedMessages) {
            m.setTimeleft(m.getTimeleft()-1);
            if (m.getTimeleft() > 0) {
                newMessages.add(m);
            }
        }
        timedMessages = newMessages;
    }

    public static void addMessage(String message, int timeout) {
        timedMessages.add(new TimedMessage(message, timeout));
    }

    public static List<TimedMessage> getMessages() {
        return timedMessages;
    }
}
