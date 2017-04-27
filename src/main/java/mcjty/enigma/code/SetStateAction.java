package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import org.apache.commons.lang3.StringUtils;

public class SetStateAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext>  value;

    public SetStateAction(Expression<EnigmaFunctionContext>  name, Expression<EnigmaFunctionContext>  value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Set State: " + name + "=" + value);
    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
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

        progress.setState(ObjectTools.asStringSafe(name.eval(context)), ObjectTools.asStringSafe(value.eval(context)));
//        for (Scope scope : toactivate) {
//            scope.start(world);
//        }

        ProgressHolder.save(context.getWorld());
    }
}
