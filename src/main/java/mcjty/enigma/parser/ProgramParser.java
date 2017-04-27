package mcjty.enigma.parser;

import mcjty.enigma.code.*;

import javax.annotation.Nonnull;
import java.util.List;

public class ProgramParser {

    public static Scope parse(@Nonnull List<TokenizedLine> lines) throws ParserException {
        Scope root = new Scope();
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
            } else if (line.getMainToken() == MainToken.WHILE) {
                parseWhile(context, line, scope);
            } else {
                throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for a scope", linenumber);
            }
        }
    }


    private static void parseWhile(ParsingContext context, TokenizedLine line, Scope scope) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'WHILE' statement", line.getLineNumber());
        }

        Scope newscope = new Scope();
        newscope.setCondition(line.getParameters().get(0));

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in a scope block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        parseScope(context, newscope);
        scope.addScope(newscope);

        context.setCurrentIndent(origIndent);
    }

    private static void parseOn(ParsingContext context, TokenizedLine line, Scope scope) throws ParserException {
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
            case LEFTCLICKBLOCK:
                scope.addOnLeftClickBlock(actionBlock, line.getParameters().get(0));
                break;
            case BLOCKAT:
                break;
            case DELAY:
                scope.addOnDelay(actionBlock, line.getParameters().get(0));
                break;
            case OPEN:
                break;
            case START:
                scope.addOnStart(actionBlock);
                break;
            case INIT:
                scope.addOnInit(actionBlock);
                break;
            default:
                throw new ParserException("Unexpected token '" + secondaryToken.name() + "' for 'ON' command!", line.getLineNumber());
        }
    }

    private static void parseActionBlock(ParsingContext context, ActionBlock actionBlock) throws ParserException {
        TokenizedLine line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Actions in an action block must be indented to the right!", line.getLineNumber());
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
                case VAR:
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
                case GIVE:
                    actionBlock.addAction(new GiveAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case TAG:
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for an action block!", linenumber);
            }
        }

        context.setCurrentIndent(oldindent);
    }
}
