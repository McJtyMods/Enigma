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

    private static void parseScope(ParsingContext<EnigmaFunctionContext> context, Scope scope) throws ParserException {
        while (context.hasLines()) {
            TokenizedLine<EnigmaFunctionContext> line = context.getLine();
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
            } else if (line.getMainToken() == MainToken.BLOCK) {
                if (!line.isEndsWithColon()) {
                    throw new ParserException("Expected ':' after 'SCOPE' statement", line.getLineNumber());
                }
                String name = ObjectTools.asStringSafe(line.getParameters().get(0).eval(EnigmaFunctionContext.EMPTY));
                ActionBlock block = new ActionBlock();
                parseActionBlock(context, block);
                scope.addNamedActionBlock(name, block);
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
            case RIGHTCLICKPOSITION:
                scope.addOnRightClickPosition(actionBlock, line.getParameters().get(0));
                break;
            case RIGHTCLICKITEM:
                scope.addOnRightClickItem(actionBlock, line.getParameters().get(0));
                break;
            case LEFTCLICKPOSITION:
                scope.addOnLeftClickPosition(actionBlock, line.getParameters().get(0));
                break;
            case LEFTCLICKBLOCK:
                scope.addOnLeftClickBlock(actionBlock, line.getParameters().get(0));
                break;
            case DEATH:
                scope.addOnDeath(actionBlock);
                break;
            case BLOCKAT:
                break;
            case DELAY:
                scope.addOnDelay(actionBlock, line.getParameters().get(0), false);
                break;
            case REPEAT:
                scope.addOnDelay(actionBlock, line.getParameters().get(0), true);
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

    private static Action parseFxAnim(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        Token secondaryToken = line.getSecondaryToken();
        assert secondaryToken != null;

        switch (secondaryToken) {
            case MOVE:
                return new FxAnimMoveAction(line.getParameters().get(0), line.getParameters().get(1), line.getParameters().get(2));
            case ROTATE:
                return new FxAnimRotateAction(line.getParameters().get(0),
                        line.getParameters().get(1), line.getParameters().get(2),
                        line.getParameters().get(3), line.getParameters().get(4));
            case COLOR:
                return new FxAnimColorAction(line.getParameters().get(0),
                        line.getParameters().get(1), line.getParameters().get(2),
                        line.getParameters().get(3), line.getParameters().get(4),
                        line.getParameters().get(5), line.getParameters().get(6),
                        line.getParameters().get(7), line.getParameters().get(8), false);
            case COLORANDBACK:
                return new FxAnimColorAction(line.getParameters().get(0),
                        line.getParameters().get(1), line.getParameters().get(2),
                        line.getParameters().get(3), line.getParameters().get(4),
                        line.getParameters().get(5), line.getParameters().get(6),
                        line.getParameters().get(7), line.getParameters().get(8), true);
            default:
                throw new ParserException("Unexpected token '" + secondaryToken.name() + "' for 'fxanim' command!", line.getLineNumber());
        }
    }

    private static Action parseIf(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
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

    private static DelayAction parseDelay(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'delay' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> ticks = line.getParameters().get(0);

        ActionBlock block = new ActionBlock();
        parseActionBlock(context, block);

        return new DelayAction(ticks, block);
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
                case NAME:
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
            throw new ParserException("Missing 'name' for itemstack!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateItemStackAction(name, item, amount, meta, description);
    }

    private static CreateMobAction parseMob(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'mob' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> name = line.getParameters().get(0);

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in a mob block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        Expression<EnigmaFunctionContext> mob = null;
        Expression<EnigmaFunctionContext> tag = null;
        Expression<EnigmaFunctionContext> hp = null;
        Expression<EnigmaFunctionContext> damage = null;
        Expression<EnigmaFunctionContext> item = null;
        Expression<EnigmaFunctionContext> helmet = null;
        Expression<EnigmaFunctionContext> chestplate = null;
        Expression<EnigmaFunctionContext> leggings = null;
        Expression<EnigmaFunctionContext> boots = null;
        Expression<EnigmaFunctionContext> aggressive = null;

        while (context.hasLines()) {
            line = context.getLine();
            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Unexpected indentation in mob block", line.getLineNumber());
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                break;
            }
            int linenumber = line.getLineNumber();
            context.nextLine();

            switch (line.getMainToken()) {
                case NAME:
                    mob = line.getParameters().get(0);
                    break;
                case TAG:
                    tag = line.getParameters().get(0);
                    break;
                case HP:
                    hp = line.getParameters().get(0);
                    break;
                case DAMAGE:
                    damage = line.getParameters().get(0);
                    break;
                case ITEM:
                    item = line.getParameters().get(0);
                    break;
                case HELMET:
                    helmet = line.getParameters().get(0);
                    break;
                case CHESTPLATE:
                    chestplate = line.getParameters().get(0);
                    break;
                case LEGGINGS:
                    leggings = line.getParameters().get(0);
                    break;
                case BOOTS:
                    boots = line.getParameters().get(0);
                    break;
                case AGGRESSIVE:
                    aggressive = line.getParameters().get(0);
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for mob block", linenumber);
            }
        }

        if (mob == null) {
            throw new ParserException("Missing 'name' for mob!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateMobAction(name, mob, tag, hp, damage, item, helmet, chestplate, leggings, boots, aggressive);
    }

    private static CreateParticleAction parseParticleConfig(ParsingContext<EnigmaFunctionContext> context, TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        if (!line.isEndsWithColon()) {
            throw new ParserException("Expected ':' after 'CREATEPARTICLES' statement", line.getLineNumber());
        }

        Expression<EnigmaFunctionContext> name = line.getParameters().get(0);

        line = context.getLine();
        if (line.getIndentation() <= context.getCurrentIndent()) {
            throw new ParserException("Commands in an createparticles block must be indented to the right!", line.getLineNumber());
        }

        int origIndent = context.getCurrentIndent();
        context.setCurrentIndent(line.getIndentation());

        Expression<EnigmaFunctionContext> partsys = null;
        Expression<EnigmaFunctionContext> amount = c -> 1;
        Expression<EnigmaFunctionContext> offsetX = c -> 0.0;
        Expression<EnigmaFunctionContext> offsetY = c -> 0.0;
        Expression<EnigmaFunctionContext> offsetZ = c -> 0.0;
        Expression<EnigmaFunctionContext> speed = c -> 0.15;

        while (context.hasLines()) {
            line = context.getLine();
            if (line.getIndentation() > context.getCurrentIndent()) {
                throw new ParserException("Unexpected indentation in createparticles block", line.getLineNumber());
            }
            if (line.getIndentation() < context.getCurrentIndent()) {
                break;
            }
            int linenumber = line.getLineNumber();
            context.nextLine();

            switch (line.getMainToken()) {
                case NAME:
                    partsys = line.getParameters().get(0);
                    break;
                case AMOUNT:
                    amount = line.getParameters().get(0);
                    break;
                case OFFSET:
                    offsetX = line.getParameters().get(0);
                    offsetY = line.getParameters().get(1);
                    offsetZ = line.getParameters().get(2);
                    break;
                case SPEED:
                    speed = line.getParameters().get(0);
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for createparticles block", linenumber);
            }
        }

        if (partsys == null) {
            throw new ParserException("Missing 'name' for createparticles!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateParticleAction(name, partsys, amount, offsetX, offsetY, offsetZ, speed);
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
                case NAME:
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
            throw new ParserException("Missing 'name' for blockstate!", line.getLineNumber());
        }

        context.setCurrentIndent(origIndent);
        return new CreateBlockStateAction(name, block, meta);
    }

    public static Action parseSettingAction(TokenizedLine<EnigmaFunctionContext> line) throws ParserException {
        Token secondaryToken = line.getSecondaryToken();
        assert secondaryToken != null;

        switch (secondaryToken) {
            case CHAT:
                return new SettingChatAction(line.getParameters().get(0));
            default:
                throw new ParserException("Unexpected token '" + secondaryToken.name() + "' for 'setting' command!", line.getLineNumber());
        }
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
                case RESTORE:
                    actionBlock.addAction(new RestoreAction(line.getParameters().get(0)));
                    break;
                case VAR:
                    actionBlock.addAction(new SetVariableAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case PVAR:
                    actionBlock.addAction(new SetPlayerVariableAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case LOCAL:
                    actionBlock.addAction(new SetLocalVariableAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case FXANIM:
                    actionBlock.addAction(parseFxAnim(context, line));
                    break;
                case CALL:
                    actionBlock.addAction(new CallAction(line.getParameters().get(0)));
                    break;
                case IF:
                    actionBlock.addAction(parseIf(context, line));
                    break;
                case DELAY:
                    actionBlock.addAction(parseDelay(context, line));
                    break;
                case CREATEPARTICLES:
                    actionBlock.addAction(parseParticleConfig(context, line));
                    break;
                case MOB:
                    actionBlock.addAction(parseMob(context, line));
                    break;
                case ITEMSTACK:
                    actionBlock.addAction(parseItemStack(context, line));
                    break;
                case SETBLOCK:
                    actionBlock.addAction(new SetBlockAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case SETMIMIC:
                    actionBlock.addAction(new SetMimicAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case SETTING:
                    actionBlock.addAction(parseSettingAction(line));
                    break;
                case BLOCKSTATE:
                    actionBlock.addAction(parseBlockState(context, line));
                    break;
                case COMMAND:
                    actionBlock.addAction(new CommandAction(line.getParameters().get(0)));
                    break;
                case LOOKAT:
                    actionBlock.addAction(new LookAtAction(line.getParameters().get(0)));
                    break;
                case POSITION:
                    actionBlock.addAction(new PositionAction(
                            line.getParameters().get(0),
                            line.getParameters().get(1),
                            line.getParameters().get(2),
                            line.getParameters().get(3),
                            line.getParameters().get(4)));
                    break;
                case HP:
                    actionBlock.addAction(new SetHpAction(line.getParameters().get(0)));
                    break;
                case MESSAGE:
                    actionBlock.addAction(new MessageAction(line.getParameters().get(0), line.getParameters()));
                    break;
                case LOG:
                    actionBlock.addAction(new LogAction(line.getParameters().get(0)));
                    break;
                case GIVE:
                    actionBlock.addAction(new GiveAction(line.getParameters().get(0)));
                    break;
                case DROP:
                    actionBlock.addAction(new DropAction(line.getParameters().get(0), line.getParameters().get(1)));
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
                case SOUND:
                    actionBlock.addAction(new SoundAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case SPAWN:
                    actionBlock.addAction(new SpawnAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                case KILL:
                    actionBlock.addAction(new KillAction(line.getParameters().get(0)));
                    break;
                case PARTICLE:
                    actionBlock.addAction(new ParticleAction(line.getParameters().get(0), line.getParameters().get(1)));
                    break;
                default:
                    throw new ParserException("Unexpected command '" + line.getMainToken().name() + "' for action block!", linenumber);
            }
        }

        context.setCurrentIndent(oldindent);
    }
}
