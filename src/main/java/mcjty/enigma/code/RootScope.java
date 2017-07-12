package mcjty.enigma.code;

import mcjty.enigma.Enigma;
import mcjty.enigma.parser.ParserException;
import mcjty.enigma.parser.ProgramParser;
import mcjty.enigma.parser.RuleParser;
import mcjty.enigma.parser.TokenizedLine;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.List;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class RootScope {

    private static Scope root = null;
    private static ScopeInstance rootInstance = null;

    private static Scope getRoot() {
        return root;
    }

    public static ScopeInstance getRootInstance(World world) {
        if (rootInstance == null) {
            if (root == null) {
                root = new Scope(new ScopeID(STRINGS.get("root")));
            }
            rootInstance = new ScopeInstance(getRoot());
            EnigmaFunctionContext context = new EnigmaFunctionContext(world, null);
            context.setScopeInstance(rootInstance);
            root.onInit(context);

            Progress progress = ProgressHolder.getProgress(world);
            if (!progress.isRootActivated()) {
                // We set scope activity to false because the root has not been activated yet
                RootScope.getRootInstance(world).setActive(false);
                root.onSetup(context);
                progress.setRootActivated(true);
                ProgressHolder.save(world);
            }
        }
        return rootInstance;
    }

    public static void reset(World world) {
        Progress progress = ProgressHolder.getProgress(world);
        progress.reset();
        rootInstance = null;
        ProgressHolder.save(world);
    }

    private static File currentFile = null;

    public static boolean reload(World world, EntityPlayer player) throws IOException {
        if (currentFile == null) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "No rules loaded!");
            if (player instanceof EntityPlayer) {
                ((EntityPlayer) player).sendStatusMessage(component, false);
            } else {
                player.sendMessage(component);
            }
            return false;
        }
        root = readRules(world, currentFile);
        rootInstance = null;
        return true;
    }

    public static void setRoot(Scope r) {
        root = r;
    }

    public static Scope readRules(World world, File file) throws IOException {
        try {
            currentFile = file;
            InputStream inputstream = new FileInputStream(file);
            Progress progress = ProgressHolder.getProgress(world);
            List<TokenizedLine> lines = RuleParser.parse(new BufferedReader(new InputStreamReader(inputstream)), new EnigmaExpressionContext(progress));
            return ProgramParser.parse(lines);
        } catch (ParserException e) {
            Enigma.logger.log(Level.ERROR, "ERROR: " + e.getMessage() + " at line " + (e.getLinenumber()+1) + " (" + file.getName() + ")", e);
            throw new RuntimeException(e);
        }
    }
}
