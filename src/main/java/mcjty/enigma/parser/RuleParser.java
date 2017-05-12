package mcjty.enigma.parser;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.EnigmaExpressionContext;
import mcjty.enigma.code.ExecutionException;
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

    public static <T> TokenizedLine getTokenizedLine(String line, int linenumber, ExpressionContext<T> expressionContext) throws ParserException {
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
        String token = null;
        try {
            token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).getExpression().eval(null));
        } catch (ExpressionException e) {
            throw new ParserException("Error parsing token: " + e.getMessage(), linenumber);
        } catch (ExecutionException e) {
            throw new ParserException("Error parsing token: " + e.getMessage(), linenumber);
        }

        int minParameters = 0;
        int maxParameters = 0;

        MainToken mainToken = MainToken.getTokenByName(token);
        if (mainToken == null) {
            throw new ParserException("ERROR: Unknown token '" + token, linenumber);
        }
        minParameters = mainToken.getMinParameters();
        maxParameters = mainToken.getMaxParameters();

        Token secondaryToken = null;
        if (mainToken.isHasSecondaryToken()) {
            try {
                token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).getExpression().eval(null));
            } catch (ExpressionException e) {
                throw new ParserException("Error parsing token: " + e.getMessage(), linenumber);
            } catch (ExecutionException e) {
                throw new ParserException("Error parsing token: " + e.getMessage(), linenumber);
            }
            secondaryToken = Token.getTokenByName(token);
            if (secondaryToken == null) {
                throw new ParserException("ERROR: Unknown token '" + token, linenumber);
            }
            minParameters = secondaryToken.getParameters();
            maxParameters = secondaryToken.getParameters();
        }

        List<Expression<T>> params = new ArrayList<>(minParameters);
        for (int t = 0 ; t < maxParameters ; t++) {
            if (t >= minParameters && !str.hasMore()) {
                break;
            }
            ParsedExpression<T> expression = null;
            try {
                expression = ExpressionParser.eval(str, expressionContext);
            } catch (ExpressionException e) {
                throw new ParserException("Error parsing parameter " + (t+1) + ": " + e.getMessage(), linenumber);
            }
            params.add(expression.getExpression());
        }
        boolean endsWithColon = false;
        if (str.hasMore()) {
            if (str.current() == ':') {
                endsWithColon = true;
                str.inc();
            }
        }
        if (str.hasMore()) {
            throw new ParserException("Unexpected extra paramaters", linenumber);
        }

        return new TokenizedLine<T>(indentation, linenumber, mainToken, secondaryToken, params, endsWithColon);
    }

    public static void main(String[] args) {
        ExpressionContext<Void> context = new ExpressionContext<Void>() {
            @Nullable
            @Override
            public Expression<Void> getVariable(String var) {
                return "var".equals(var) ? w -> 666 : null;
            }

            @Nullable
            @Override
            public ExpressionFunction<Void> getFunction(String name) {
                if ("double".equals(name)) {
                    return (w,o) -> ObjectTools.asIntSafe(o[0]) * 2;
                } else if ("state".equals(name)) {
                    return (w,o) -> o[0];
                } else if ("distance".equals(name)) {
                    return (w,o) -> o[0];
                } else if ("pstate".equals(name)) {
                    return (w,o) -> o[0];
                } else if ("hasitem".equals(name)) {
                    return (w, o) -> false;
                } else if ("max".equals(name)) {
                    return (w, o) -> Math.max(ObjectTools.asIntSafe(o[0]), ObjectTools.asIntSafe(o[1]));
                } else {
                    return (w, o) -> 0;
                }
            }

            @Override
            public boolean isFunction(String name) {
                return "double".equals(name) || EnigmaExpressionContext.FUNCTIONS.containsKey(name);
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
            System.out.println("e.getMessage() = " + e.getMessage() + " at line " + (e.getLinenumber()+1));
        }

        StringPointer str = new StringPointer("double(1)*$var   8/2!=2+2 sqrt 16 \"Dit is \\\"een\\\" test\"+' (echt)' 'nog eentje' max(8 16)");
        try {
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
            System.out.println("result = " + ExpressionParser.eval(str, context).getExpression().eval(null));
        } catch (ExpressionException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
