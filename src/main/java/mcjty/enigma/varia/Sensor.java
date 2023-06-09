package mcjty.enigma.varia;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Sensor {
    public enum SensorOp {
        ADDED, REMOVED, NOTHING
    }

    private final IPositional positional;
    private final Set<UUID> players = new HashSet<>();

    public Sensor(IPositional positional) {
        this.positional = positional;
    }

    public IPositional getPositional() {
        return positional;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void clear() {
        players.clear();
    }

    public SensorOp testPlayer(EntityPlayerMP player) {
        if (positional.getWorld() != player.getServerWorld()) {
            return SensorOp.NOTHING;
        }
        if (positional.isInside(player.getPosition())) {
            boolean added = players.add(player.getUniqueID());
            return added ? SensorOp.ADDED : SensorOp.NOTHING;
        } else {
            boolean removed = players.remove(player.getUniqueID());
            return removed ? SensorOp.REMOVED : SensorOp.NOTHING;
        }
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
    }
}
