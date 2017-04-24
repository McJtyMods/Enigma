package mcjty.xnet;


import com.google.common.base.Function;
import com.google.common.base.Optional;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatCreativeTabs;
import mcjty.xnet.api.IXNet;
import mcjty.xnet.apiimpl.XNetApi;
import mcjty.xnet.commands.CommandCheck;
import mcjty.xnet.commands.CommandDump;
import mcjty.xnet.commands.CommandGen;
import mcjty.xnet.commands.CommandRebuild;
import mcjty.xnet.items.manual.GuiXNetManual;
import mcjty.xnet.multiblock.XNetBlobData;
import mcjty.xnet.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = XNet.MODID, name = XNet.MODNAME,
        dependencies =
                        "required-after:mcjtylib_ng@[" + XNet.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:compatlayer@[" + XNet.COMPATLAYER_VER + ",);" +
                        "after:rftools@[" + XNet.MIN_RFTOOLS_VER + ",);" +
                        "after:Forge@[" + XNet.MIN_FORGE10_VER + ",);" +
                        "after:forge@[" + XNet.MIN_FORGE11_VER + ",)",
        version = XNet.MODVERSION,
        acceptedMinecraftVersions = "[1.10,1.12)")
public class XNet implements ModBase {

    public static final String MODID = "xnet";
    public static final String MODNAME = "XNet";
    public static final String MODVERSION = "1.3.1";

    public static final String MIN_FORGE10_VER = "12.18.2.2116";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "2.3.11";
    public static final String MIN_RFTOOLS_VER = "6.00";
    public static final String COMPATLAYER_VER = "0.2.5";

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    @SidedProxy(clientSide = "mcjty.xnet.proxy.ClientProxy", serverSide = "mcjty.xnet.proxy.ServerProxy")
    public static CommonProxy proxy;

    public ClientInfo clientInfo = new ClientInfo();

    @Mod.Instance(MODID)
    public static XNet instance;
    public static Logger logger;

    public static boolean rftools = false;
    public static XNetApi xNetApi = new XNetApi();

    public static CreativeTabs tabXNet = new CompatCreativeTabs("XNet") {
        @Override
        protected Item getItem() {
            return Item.getItemFromBlock(Blocks.ANVIL);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        rftools = Loader.isModLoaded("rftools");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        XNetBlobData.clearInstance();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDump());
        event.registerServerCommand(new CommandGen());
        event.registerServerCommand(new CommandRebuild());
        event.registerServerCommand(new CommandCheck());
    }

    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiXNetManual.locatePage = page;
        player.openGui(XNet.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("getXNet")) {
                Optional<Function<IXNet, Void>> value = message.getFunctionValue(IXNet.class, Void.class);
                if (value.isPresent()) {
                    value.get().apply(xNetApi);
                } else {
                    logger.warn("Some mod didn't return a valid result with getXNet!");
                }
            }
        }
    }

}
