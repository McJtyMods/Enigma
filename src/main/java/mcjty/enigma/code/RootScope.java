package mcjty.enigma.code;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.world.World;

public class RootScope {

    public static Scope root;
    private static ScopeInstance rootInstance = null;

    public static ScopeInstance getRootInstance(World world) {
        if (rootInstance == null) {
            rootInstance = new ScopeInstance(root);

            root.onInit(new EnigmaFunctionContext(world, null));

            Progress progress = ProgressHolder.getProgress(world);
            if (!progress.isRootActivated()) {
                // We set scope activity to false because the root has not been activated yet
                RootScope.getRootInstance(world).setActive(false);
                root.onSetup(new EnigmaFunctionContext(world, null));
                progress.setRootActivated(true);
                ProgressHolder.save(world);
            }
        }
        return rootInstance;
    }
}
