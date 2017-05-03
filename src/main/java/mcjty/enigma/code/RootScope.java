package mcjty.enigma.code;

import mcjty.enigma.Enigma;
import mcjty.enigma.parser.ParserException;
import mcjty.enigma.parser.ProgramParser;
import mcjty.enigma.parser.RuleParser;
import mcjty.enigma.parser.TokenizedLine;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class RootScope {

    private static Scope root = null;
    private static ScopeInstance rootInstance = null;

    private static Scope getRoot(World world) {
        if (root == null) {
            root = RootScope.readRules(world);
        }
        return root;
    }

    public static ScopeInstance getRootInstance(World world) {
        if (rootInstance == null) {
            rootInstance = new ScopeInstance(getRoot(world));
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

    public static void reset(World world) {
        Progress progress = ProgressHolder.getProgress(world);
        progress.reset();
        rootInstance = null;
        ProgressHolder.save(world);
    }

    public static void reload(World world) {
        root = readRules(world);
        rootInstance = null;
    }

    public static Scope readRules(World world) {
        InputStream inputstream = Enigma.class.getResourceAsStream("/assets/enigma/rules/ruleexample");
        try {
            Progress progress = ProgressHolder.getProgress(world);
            List<TokenizedLine> lines = RuleParser.parse(new BufferedReader(new InputStreamReader(inputstream)), new EnigmaExpressionContext(progress));
            return ProgramParser.parse(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserException e) {
            Enigma.logger.log(Level.ERROR, "ERROR: " + e.getMessage() + " at line " + (e.getLinenumber()+1), e);
            throw new RuntimeException(e);
        }
    }
}
