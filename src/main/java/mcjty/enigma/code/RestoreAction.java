package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.snapshot.SnapshotTools;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class RestoreAction extends Action {
    private final Expression<EnigmaFunctionContext> file;

    public RestoreAction(Expression<EnigmaFunctionContext> file) {
        this.file = file;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Restore: " + file);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        String fn = ObjectTools.asStringSafe(file.eval(context));
        try {
            File dataDir = new File(((WorldServer) context.getWorld()).getChunkSaveLocation(), "enigma");
            dataDir.mkdirs();
            File file = new File(dataDir, fn);
            SnapshotTools.restoreChunkSnapshot(context.getWorld(), file);
        } catch (IOException e) {
            throw new ExecutionException("Could not restore snapshot from '" + fn + "'!");
        }
    }

}
