package mcjty.enigma.snapshot;

import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FakeWorld extends World {

    private final World copy;

    public FakeWorld(World copy) {
        super(new FakeSaveHandler(), copy.getWorldInfo(), new WorldProvider() {
            @Override
            public DimensionType getDimensionType() {
                return copy.provider.getDimensionType();
            }
        }, copy.profiler, false);
        this.copy = copy;
        chunkProvider = createChunkProvider();
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        Method method = ReflectionHelper.findMethod(World.class, "createChunkProvider", "func_72970_h");
        try {
            return (IChunkProvider) method.invoke(copy);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }
}
