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

    public static void reset(World world) {
        Progress progress = ProgressHolder.getProgress(world);
        progress.reset();
        rootInstance = null;
        ProgressHolder.save(world);
    }

    public static void reload(World world) {
        readRules();
    }

    public static void readRules() {
        InputStream inputstream = Enigma.class.getResourceAsStream("/assets/enigma/rules/ruleexample");
        try {
            List<TokenizedLine> lines = RuleParser.parse(new BufferedReader(new InputStreamReader(inputstream)), new EnigmaExpressionContext());
            root = ProgramParser.parse(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserException e) {
            Enigma.logger.log(Level.ERROR, "ERROR: " + e.getMessage() + " at line " + (e.getLinenumber()+1), e);
            throw new RuntimeException(e);
        }
    }
}
