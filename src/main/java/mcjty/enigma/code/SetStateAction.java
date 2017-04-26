package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SetStateAction extends Action {
    private final Expression name;
    private final Expression value;

    public SetStateAction(Expression name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Set State: " + name + "=" + value);
    }

    @Override
    public void execute(World world, EntityPlayer player) {
        Progress progress = ProgressHolder.getProgress(world);
        System.out.println("Setting state " + name + " to " + value);

//        int nameI = STRINGS.get(name);
//        int valueI = STRINGS.get(value);

        //@todo
//        List<Scope> toactivate = new ArrayList<>();
//        Enigma.root.forActiveScopes(world, scope -> {
//            for (Scope child : scope.getNestedScopes()) {
//                if (!child.isActive(world)) {
//                    if (child.getStateName().equals(nameI)) {
//                        if (child.getStateValue().equals(valueI)) {
//                            toactivate.add(child);
//                        }
//                    }
//                }
//            }
//        });

        progress.setState(ObjectTools.asStringSafe(name.eval(world)), ObjectTools.asStringSafe(value.eval(world)));
//        for (Scope scope : toactivate) {
//            scope.start(world);
//        }

        ProgressHolder.save(world);
    }
}
