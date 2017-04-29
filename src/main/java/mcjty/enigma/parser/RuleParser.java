package mcjty.enigma.parser;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.Scope;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RuleParser<T> {

    public static <T> List<TokenizedLine> parse(File file, @Nonnull ExpressionContext<T> expressionContext) throws ParserException {
        if (!file.exists()) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return parse(reader, expressionContext);
        } catch (FileNotFoundException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        } catch (IOException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
    }

    public static <T> List<TokenizedLine> parse(BufferedReader reader, ExpressionContext<T> expressionContext) throws IOException, ParserException {
        List<TokenizedLine> lines = new ArrayList<>();
        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            TokenizedLine tokenizedLine = getTokenizedLine(line, i, expressionContext);
            if (tokenizedLine != null) {
                tokenizedLine.dump();
                lines.add(tokenizedLine);
            }
            line = reader.readLine();
            i++;
        }
        return lines;
    }

    private static <T> TokenizedLine getTokenizedLine(String line, int linenumber, ExpressionContext<T> expressionContext) throws ParserException {
        int indentation = 0;
        int i = 0;
        while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
            if (line.charAt(i) == '\t') {
                indentation = (indentation + 8) % 8;
            } else {
                indentation++;
            }
            i++;
        }
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }

        StringPointer str = new StringPointer(line);
        String token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).getExpression().eval(null));

        int parameters = 0;

        MainToken mainToken = MainToken.getTokenByName(token);
        if (mainToken == null) {
            throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
        }
        parameters = mainToken.getParameters();

        Token secondaryToken = null;
        if (mainToken.isHasSecondaryToken()) {
            token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).getExpression().eval(null));
            secondaryToken = Token.getTokenByName(token);
            if (secondaryToken == null) {
                throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
            }
            parameters = secondaryToken.getParameters();
        }

        List<Expression<T>> params = new ArrayList<>(parameters);
        for (int t = 0 ; t < parameters ; t++) {
            ParsedExpression<T> expression = ExpressionParser.eval(str, expressionContext);
            params.add(expression.getExpression());
        }
        boolean endsWithColon = str.hasMore() && str.current() == ':';

        return new TokenizedLine<T>(indentation, linenumber, mainToken, secondaryToken, params, endsWithColon);
    }

    public static void main(String[] args) {
        ExpressionContext<Void> context = new ExpressionContext<Void>() {
            @Nullable
            @Override
            public Expression<Void> getVariable(String var) {
                return "var".equals(var) ? w -> 666 : null;
            }

            @Override
            public boolean isVariable(String var) {
                return "var".equals(var);
            }

            @Nullable
            @Override
            public ExpressionFunction<Void> getFunction(String name) {
                if ("double".equals(name)) {
                    return (w,o) -> ObjectTools.asIntSafe(o[0]) * 2;
                } else if ("state".equals(name)) {
                    return (w,o) -> o[0];
                } else if ("pstate".equals(name)) {
                    return (w,o) -> o[0];
                } else if ("hasitem".equals(name)) {
                    return (w, o) -> false;
                } else if ("max".equals(name)) {
                    return (w, o) -> Math.max(ObjectTools.asIntSafe(o[0]), ObjectTools.asIntSafe(o[1]));
                } else {
                    return null;
                }
            }

            @Override
            public boolean isFunction(String name) {
                return "double".equals(name) || "state".equals(name) || "hasitem".equals(name)
                        || "pstate".equals(name) || "max".equals(name);
            }
        };

        String dir = System.getProperty("user.dir");
        System.out.println("dir = " + dir);
        File f = new File("out/production/enigma/assets/enigma/rules/ruleexample");

        try {
            List<TokenizedLine> lines = parse(f, context);
            Scope root = ProgramParser.parse(lines);
            root.dump(0);
        } catch (ParserException e) {
            System.out.println("e.getMessage() = " + e.getMessage() + " at line " + e.getLinenumber());
        }

        StringPointer str = new StringPointer("double(1)*var   8/2!=2+2 sqrt 16 \"Dit is \\\"een\\\" test\"+' (echt)' 'nog eentje' max(8 16)");
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
    }
}
