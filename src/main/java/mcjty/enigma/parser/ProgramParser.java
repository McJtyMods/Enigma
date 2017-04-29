package mcjty.enigma.parser;

import mcjty.enigma.code.*;

import javax.annotation.Nonnull;
import java.util.List;

public class ProgramParser {

    public static Scope parse(@Nonnull List<TokenizedLine> lines) throws ParserException {
        Scope root = new Scope(new ScopeID("root"));
        ParsingContext context = new ParsingContext(lines);
        parseScope(context, root);
        return root;
    }

    private static void parseScope(ParsingContext context, Scope scope) throws ParserException {
        while (context.hasLines()) {
            TokenizedLine line = context.getLine();
            int linenumber = line.getLineNumber();

            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Bad indentation in scope", linenumber);
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                // Scope ends
                return;
            }
            context.nextLine();

            if (line.getMainToken() == MainToken.ON) {
                parseOn(context, line, scope);
            } else if (line.getMainToken() == MainToken.SCOPE) {
                scope.addScope(parseScope(context, line));
            } else if (line.getMainToken() == MainToken.PSCOPE) {
                scope.addPlayerScope(parseScope(context, line));
            } else {
                throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for a scope", linenumber);
            }
        }
    }

    private static Scope parseScope(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'SCOPE' statement", line.getLineNumber());
        }

        ScopeID id = new ScopeID((String) line.getParameters().get(0).eval(EnigmaFunctionContext.EMPTY));
        Scope newscope = new Scope(id);
        newscope.setCondition(line.getParameters().get(1));

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in a scope block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        parseScope(context, newscope);

        context.setCurrentIndent(origIndent);
        return newscope;
    }

    private static void parseOn(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line, Scope scope) throws ParserException {
        Token secondaryToken = line.getSecondaryToken();
        assert secondaryToken != null;

        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'ON' statement", line.getLineNumber());
        }
        ActionBlock actionBlock = new ActionBlock();
        parseActionBlock(context, actionBlock);

        switch (secondaryToken) {
            case OBTAINITEM:
                break;
            case OBTAINTAG:
                break;
            case BREAKBLOCK:
                break;
            case RIGHTCLICKBLOCK:
                scope.addOnRightClickBlock(actionBlock, line.getParameters().get(0));
                break;
            case RIGHTCLICKITEM:
                scope.addOnRightClickItem(actionBlock, line.getParameters().get(0));
                break;
            case LEFTCLICKBLOCK:
                scope.addOnLeftClickBlock(actionBlock, line.getParameters().get(0));
                break;
            case BLOCKAT:
                break;
            case DELAY:
                scope.addOnDelay(actionBlock, line.getParameters().get(0));
                break;
            case LOGIN:
                scope.addOnLogin(actionBlock);
                break;
            case OPEN:
                break;
            case ACTIVATE:
                scope.addOnActivate(actionBlock);
                break;
            case DEACTIVATE:
                scope.addOnDeactivate(actionBlock);
                break;
            case INIT:
                scope.addOnInit(actionBlock);
                break;
            case SETUP:
                scope.addOnSetup(actionBlock);
                break;
            default:
                throw new ParserException("Unexpected token '" + secondaryToken.name() + "' for 'ON' command!", line.getLineNumber());
        }
    }

    private static IfAction parseIf(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'IF' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> condition = line.getParameters().get(0);

        ActionBlock posBlock = new ActionBlock();
        ActionBlock negBlock = null;
        parseActionBlock(context, posBlock);

        if (context.hasLines() && context.getLine().getMainToken() == MainToken.ELSE && context.getCurrentIndent() == context.getLine().getIndentation()) {
            context.nextLine();
            negBlock = new ActionBlock();
            // @todo check ":" after else
            parseActionBlock(context, negBlock);
        }
        return new IfAction(condition, posBlock, negBlock);
    }

    private static CreateItemStackAction parseItemStack(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'ITEMSTACK' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> name = line.getParameters().get(0);

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in an itemstack block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        Expression<EnigmaFunctionContext> item = null;
        Expression<EnigmaFunctionContext> meta = c -> 0;
        Expression<EnigmaFunctionContext> amount = c -> 1;
        Expression<EnigmaFunctionContext> description = null;

        while (context.hasLines()) {
            line = context.getLine();
            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Unexpected indentation in itemstack block", line.getLineNumber());
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                break;
            }
            int linenumber = line.getLineNumber();
            context.nextLine();

            switch (line.getMainToken()) {
                case ITEM:
                    item = line.getParameters().get(0);
                    break;
                case AMOUNT:
                    amount = line.getParameters().get(0);
                    break;
                case META:
                    meta = line.getParameters().get(0);
                    break;
                case DESCRIPTION:
                    description = line.getParameters().get(0);
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for itemstack block", linenumber);
            }
        }

        if (item == null) {
            throw new ParserException("Missing 'item' for itemstack!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateItemStackAction(name, item, amount, meta, description);
    }

    private static CreateBlockStateAction parseBlockState(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'BLOCKSTATE' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> name = line.getParameters().get(0);

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in an blockstate block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        Expression<EnigmaFunctionContext> block = null;
        Expression<EnigmaFunctionContext> meta = c -> 0;

        while (context.hasLines()) {
            line = context.getLine();
            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Unexpected indentation in blockstate block", line.getLineNumber());
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                break;
            }
            int linenumber = line.getLineNumber();
            context.nextLine();

            switch (line.getMainToken()) {
                case BLOCK:
                    block = line.getParameters().get(0);
                    break;
                case META:
                    meta = line.getParameters().get(0);
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for blockstate block", linenumber);
            }
        }

        if (block == null) {
            throw new ParserException("Missing 'item' for blockstate!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateBlockStateAction(name, block, meta);
    }

    private static void parseActionBlock(ParsingContext<EnigmaFunctionContext> context, ActionBlock actionBlock) throws ParserException {
        TokenizedLine<EnigmaFunctionContext> line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Actions in an action block must be indented to the right", line.getLineNumber());
        }
        int oldindent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        while (context.hasLines()) {
            line = context.getLine();
            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Unexpected indentation in action block", line.getLineNumber());
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                break;
            }
            int linenumber = line.getLineNumber();
            context.nextLine();

            switch (line.getMainToken()) {
                case STATE:
                    actionBlock.addAction(new SetStateAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case PSTATE:
                    actionBlock.addAction(new SetPlayerStateAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case VAR:
                    break;
                case IF:
                    actionBlock.addAction(parseIf(context, line));
                    break;
                case ITEMSTACK:
                    actionBlock.addAction(parseItemStack(context, line));
                    break;
                case SETBLOCK:
                    actionBlock.addAction(new SetBlockAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case BLOCKSTATE:
                    actionBlock.addAction(parseBlockState(context, line));
                    break;
                case POSITION:
                    actionBlock.addAction(new PositionAction(
                            line.getParameters().get(0),
                            line.getParameters().get(1),
                            line.getParameters().get(2),
                            line.getParameters().get(3),
                            line.getParameters().get(4)));
                    break;
                case MESSAGE:
                    actionBlock.addAction(new MessageAction(line.getParameters().get(0)));
                    break;
                case LOG:
                    actionBlock.addAction(new LogAction(line.getParameters().get(0)));
                    break;
                case GIVE:
                    actionBlock.addAction(new GiveAction(line.getParameters().get(0)));
                    break;
                case TAKE:
                    actionBlock.addAction(new TakeAction(line.getParameters().get(0)));
                    break;
                case TAKEALL:
                    actionBlock.addAction(new TakeAllAction(line.getParameters().get(0)));
                    break;
                case CANCEL:
                    actionBlock.addAction(new CancelAction());
                    break;
                case TELEPORT:
                    actionBlock.addAction(new TeleportAction(line.getParameters().get(0)));
                    break;
                case TAG:
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for action block!", linenumber);
            }
        }

        context.setCurrentIndent(oldindent);
    }
}
