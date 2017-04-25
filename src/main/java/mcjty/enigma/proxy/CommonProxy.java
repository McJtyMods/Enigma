package mcjty.enigma.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.enigma.Enigma;
import mcjty.enigma.ForgeEventHandlers;
import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.parser.ParserException;
import mcjty.enigma.parser.ProgramParser;
import mcjty.enigma.parser.RuleParser;
import mcjty.enigma.parser.TokenizedLine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class CommonProxy {

    public static File modConfigDir;
    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        modConfigDir = e.getModConfigurationDirectory();
        mainConfig = new Configuration(new File(modConfigDir.getPath(), "enigma.cfg"));

        readMainConfig();
        readRules();

        EnigmaMessages.registerMessages("enigma");

//        ModItems.init();
//        ModBlocks.init();
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

    private void readRules() {
        InputStream inputstream = Enigma.class.getResourceAsStream("/assets/enigma/rules/ruleexample");
        try {
            List<TokenizedLine> lines = RuleParser.parse(new BufferedReader(new InputStreamReader(inputstream)));
            Enigma.root = ProgramParser.parse(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserException e) {
            Enigma.logger.log(Level.ERROR, "ERROR: " + e.getMessage() + " at line " + e.getLinenumber(), e);
            throw new RuntimeException(e);
        }
    }

    public void init(FMLInitializationEvent e) {
//        NetworkRegistry.INSTANCE.registerGuiHandler(XNet.instance, new GuiProxy());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
//        ModRecipes.init();
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

}
