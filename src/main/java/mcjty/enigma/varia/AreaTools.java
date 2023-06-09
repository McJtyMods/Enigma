package mcjty.enigma.varia;

import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.progress.Progress;
import net.minecraft.world.WorldServer;

public class AreaTools {

    public static IAreaIterator getAreaIterator(Progress progress, Object pos) throws ExecutionException {
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            Area namedArea = progress.getNamedArea(pos);
            if (namedArea == null) {
                Sphere namedSphere = progress.getNamedSphere(pos);
                if (namedSphere == null) {
                    throw new ExecutionException("Cannot find named position, area or sphere '" + pos + "'!");
                } else {
                    return namedSphere.getIterator();
                }
            } else {
                return namedArea.getIterator();
            }
        } else {
            return namedPosition.getIterator();
        }
    }

    public static IPositional getPositional(Progress progress, Object pos) throws ExecutionException {
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            Area namedArea = progress.getNamedArea(pos);
            if (namedArea == null) {
                Sphere namedSphere = progress.getNamedSphere(pos);
                if (namedSphere == null) {
                    throw new ExecutionException("Cannot find named position, area or sphere '" + pos + "'!");
                } else {
                    return namedSphere;
                }
            } else {
                return namedArea;
            }
        } else {
            return namedPosition;
        }
    }
}
