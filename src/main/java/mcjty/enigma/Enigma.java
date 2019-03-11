package mcjty.enigma;


import mcjty.enigma.api.IEnigmaScript;
import mcjty.enigma.apiimp.EnigmaScript;
import mcjty.enigma.commands.*;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.setup.IProxy;
import mcjty.enigma.setup.ModSetup;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import java.util.Optional;
import java.util.function.Function;

@Mod(modid = Enigma.MODID, name = Enigma.MODNAME,
        dependencies =
                        "after:forge@[" + Enigma.MIN_FORGE11_VER + ",)",
        version = Enigma.MODVERSION)
public class Enigma {

    public static final String MODID = "enigma";
    public static final String MODNAME = "Enigma";
    public static final String MODVERSION = "0.0.1";

    public static final String MIN_FORGE11_VER = "13.19.0.2176";

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    @SidedProxy(clientSide = "mcjty.enigma.setup.ClientProxy", serverSide = "mcjty.enigma.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance(MODID)
    public static Enigma instance;
    public static EnigmaScript enigmaScript = new EnigmaScript();

    public static CreativeTabs tabEnigma = new CreativeTabs("Enigma") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.ENCHANTED_BOOK);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        setup.preInit(event);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        ProgressHolder.clearInstance();
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("getEnigmaScript")) {
                Optional<Function<IEnigmaScript, Void>> value = message.getFunctionValue(IEnigmaScript.class, Void.class);
                if (value.isPresent()) {
                    value.get().apply(enigmaScript);
                } else {
                    setup.getLogger().warn("Some mod didn't return a valid result with getEnigmaScript!");
                }
            }
        }
    }


    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CmdStates());
        event.registerServerCommand(new CmdReset());
        event.registerServerCommand(new CmdReload());
        event.registerServerCommand(new CmdLoad());
        event.registerServerCommand(new CmdSnapshot());
        event.registerServerCommand(new CmdRestore());
        event.registerServerCommand(new CmdEval());
        event.registerServerCommand(new CmdAction());
    }

    public String getModId() {
        return MODID;
    }
}
