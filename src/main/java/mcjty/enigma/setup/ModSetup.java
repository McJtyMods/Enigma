package mcjty.enigma.setup;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.enigma.ForgeEventHandlers;
import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.items.ModItems;
import mcjty.enigma.network.EnigmaMessages;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.Callable;

public class ModSetup {

    public static File modConfigDir;
    public static boolean lostcities = false;
    private Logger logger;

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        lostcities = Loader.isModLoaded("lostcities");

        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath(), "enigma.cfg"));

        EnigmaMessages.registerMessages("enigma");

        readMainConfig();
        ModItems.init();
        ModBlocks.init();
        FxAnimationHandler.init();
    }

    public Logger getLogger() {
        return logger;
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
//            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

//            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
        mainConfig = null;
    }

    public World getClientWorld() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public EntityPlayer getClientPlayer() {
        throw new IllegalStateException("This should only be called from client side");
    }

    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        throw new IllegalStateException("This should only be called from client side");
    }

    public boolean isClient() {
        return false;
    }
}
