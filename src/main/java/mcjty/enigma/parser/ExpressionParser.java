package mcjty.enigma.parser;

import mcjty.enigma.code.ExecutionException;
import org.apache.commons.lang3.text.StrBuilder;

public class ExpressionParser<T> {

    private final StringPointer str;
    private final ExpressionContext<T> context;
    private int ch;

    public ExpressionParser(StringPointer str, ExpressionContext<T> context) {
        this.str = str;
        this.context = context;
    }

    public static <T> ParsedExpression<T> eval(final StringPointer str, ExpressionContext<T> context) throws ExpressionException {
        return new ExpressionParser<>(str, context).parse();
    }

    private void nextChar() {
        str.inc();
        ch = str.hasMore() ? str.current() : -1;
    }

    private void prevChar() {
        str.dec();
        ch = str.hasMore() ? str.current() : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') {
            nextChar();
        }
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private boolean eat2(int charToEat, int char2ToEat) {
        while (ch == ' ') {
            nextChar();
        }
        if (ch == charToEat) {
            nextChar();
            if (ch == char2ToEat) {
                nextChar();
                return true;
            } else {
                prevChar();
                return false;
            }
        }
        return false;
    }

    public ParsedExpression<T> parse() throws ExpressionException {
        nextChar();
        ParsedExpression<T> x = parseExpression();
        if (str.hasMore() && str.current() != ',' && str.current() != ':') {
            prevChar();
        }
        return x;
    }

    // Grammar:
    // expression = termequals | expression `==` termequals | expression `!=` termequals | expression '<' termequals
    // termequals = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor
    // factor = `+` factor | `-` factor | `(` expression `)`
    //        | number | functionName factor | factor `^` factor

    private ParsedExpression<T> parseExpression() throws ExpressionException {
        ParsedExpression<T> x = parseTermEquals();
        while (true) {
            if (eat2('=', '=')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.equals(a.eval(w), b.eval(w)), x, bexp, "==");
            } else if (eat2('!', '=')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> !ObjectTools.equals(a.eval(w), b.eval(w)), x, bexp, "!=");
            } else if (eat2('<', '=')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.lessOrEqual(a.eval(w), b.eval(w)), x, bexp, "<=");
            } else if (eat2('>', '=')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.lessOrEqual(b.eval(w), a.eval(w)), x, bexp, ">=");
            } else if (eat('<')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.less(a.eval(w), b.eval(w)), x, bexp, "<");
            } else if (eat('>')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTermEquals();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.less(b.eval(w), a.eval(w)), x, bexp, ">");
            } else {
                return x;
            }
        }
    }

    private ParsedExpression<T> parseTermEquals() throws ExpressionException {
        ParsedExpression<T> x = parseTerm();
        while (true) {
            if (eat('+')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTerm();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.add(a.eval(w), b.eval(w)), x, bexp, "+");
            } else if (eat('-')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseTerm();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.sub(a.eval(w), b.eval(w)), x, bexp, "-");
            } else {
                return x;
            }
        }
    }

    private ParsedExpression<T> parseTerm() throws ExpressionException {
        ParsedExpression<T> x = parseFactor();
        while (true) {
            if (eat('*')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseFactor();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.mul(a.eval(w), b.eval(w)), x, bexp, "*");
            } else if (eat('/')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseFactor();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.div(a.eval(w), b.eval(w)), x, bexp, "/");
            } else if (eat('%')) {
                Expression<T> a = x.getExpression();
                ParsedExpression<T> bexp = parseFactor();
                Expression<T> b = bexp.getExpression();
                x = optimizeBinaryOperator(w -> ObjectTools.mod(a.eval(w), b.eval(w)), x, bexp, "%");
            } else {
                return x;
            }
        }
    }

    private ParsedExpression<T> parseFactor() throws ExpressionException {
        if (eat('+')) {
            return parseFactor(); // unary plus
        }
        if (eat('-')) {
            ParsedExpression<T> aexp = parseFactor();
            Expression<T> a = aexp.getExpression();
            if (aexp.isConstant()) {
                try {
                    Object eval = ObjectTools.sub(0, a.eval(null));
                    return new ParsedExpression<>(w -> eval, true, eval.toString());
                } catch (ExecutionException e) {
                    throw new ExpressionException(e.getMessage());
                }
            } else {
                return new ParsedExpression<>(w -> ObjectTools.sub(0, a.eval(w)), false, "-" + aexp.getDebug());
            }
        }
        if (eat('!')) {
            ParsedExpression<T> aexp = parseFactor();
            Expression<T> a = aexp.getExpression();
            if (aexp.isConstant()) {
                try {
                    Object eval = ObjectTools.not(a.eval(null));
                    return new ParsedExpression<>(w -> eval, true, eval.toString());
                } catch (ExecutionException e) {
                    throw new ExpressionException(e.getMessage());
                }
            } else {
                return new ParsedExpression<>(w -> ObjectTools.not(a.eval(w)), false, "!" + aexp.getDebug());
            }
        }

        ParsedExpression<T> x;
        int startPos = str.index();
        if (eat('(')) { // parentheses
            ParsedExpression<T> exp = parseExpression();
            x = new ParsedExpression<>(exp.getExpression(), exp.isConstant(), "(" + exp.getDebug() + ")");
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                nextChar();
            }
            String substring = str.substring(startPos, str.index());
            if (substring.contains(".")) {
                double d = Double.parseDouble(substring);
                x = new ParsedExpression<>(w -> d, true, Double.toString(d));
            } else {
                int d = Integer.parseInt(substring);
                x = new ParsedExpression<>(w -> d, true, Integer.toString(d));
            }
        } else if (ch == '"' || ch == '\'') {
            int toquote = ch;
            StrBuilder builder = new StrBuilder();
            nextChar();
            while (ch != toquote) {
                if (ch == '\\') {
                    nextChar();
                }
                builder.append((char) ch);
                nextChar();
            }
            nextChar();
            String s = builder.toString();
            x = new ParsedExpression<>(w -> s, true, "\"" + s + "\"");
        } else if (ch == '$') {
            nextChar();
            if (isIdentifierCharFirst(ch)) {
                nextChar();
                while (isIdentifierChar(ch)) {
                    nextChar();
                }
            }
            String func = str.substring(startPos+1, str.index());
            x = new ParsedExpression<>(context.getVariable(func), false, func);
        } else if (isIdentifierCharFirst(ch)) { // functions
            while (isIdentifierChar(ch)) {
                nextChar();
            }
            String func = str.substring(startPos, str.index());
            if ("sqrt".equals(func)) {
                x = parseExpression();
                Expression<T> finalX = x.getExpression();
                if (x.isConstant()) {
                    try {
                        double result = Math.sqrt(ObjectTools.asDoubleSafe(finalX.eval(null)));
                        x = new ParsedExpression<>(w -> result, true, Double.toString(result));
                    } catch (ExecutionException e) {
                        throw new ExpressionException(e.getMessage());
                    }
                } else {
                    x = new ParsedExpression<>(w -> Math.sqrt(ObjectTools.asDoubleSafe(finalX.eval(w))), false,
                            "sqrt(" + x.getDebug() + ")");
                }
            } else if ("true".equals(func)) {
                x = new ParsedExpression<>(w -> true, true, "true");
            } else if ("false".equals(func)) {
                x = new ParsedExpression<>(w -> false, true, "false");
            } else if (context.isFunction(func)) {
                if (!eat('(')) {
                    throw new ExpressionException("Excected '(' after a function");
                }
                ExpressionFunction<T> function = context.getFunction(func);
                if (eat(')')) {
                    x = new ParsedExpression<>(w -> function.eval(w), false, func + "()");
                } else {
                    ParsedExpression<T> x1 = parseExpression();
                    Expression<T> finalX1 = x1.getExpression();
                    if (eat(')')) {
                        x = new ParsedExpression<>(w -> function.eval(w, finalX1.eval(w)), false, func + " " + x1.getDebug());
                    } else {
                        eat(',');
                        ParsedExpression<T> x2 = parseExpression();
                        Expression<T> finalX2 = x2.getExpression();
                        if (eat(')')) {
                            x = new ParsedExpression<>(w -> function.eval(w, finalX1.eval(w), finalX2.eval(w)), false, func + " " + x1.getDebug() + "," + x2.getDebug());
                        } else {
                            eat(',');
                            ParsedExpression<T> x3 = parseExpression();
                            Expression<T> finalX3 = x3.getExpression();
                            if (eat(')')) {
                                x = new ParsedExpression<>(w -> function.eval(w, finalX1.eval(w), finalX2.eval(w), finalX3.eval(w)), false, func + " " + x1.getDebug() + "," + x2.getDebug() + "," + x3.getDebug());
                            } else {
                                eat(',');
                                ParsedExpression<T> x4 = parseExpression();
                                Expression<T> finalX4 = x4.getExpression();
                                x = new ParsedExpression<>(w -> function.eval(w, finalX1.eval(w), finalX2.eval(w), finalX3.eval(w), finalX4.eval(w)), false,
                                        func + " " + x1.getDebug() + "," + x2.getDebug() + "," + x3.getDebug() + "," + x4.getDebug());
                                if (!eat(')')) {
                                    throw new ExpressionException("Excected ')' after the function parameters");
                                }
                            }
                        }
                    }
                }
            } else {
                x = new ParsedExpression<>(w -> func, true, func);
            }
        } else {
            throw new ExpressionException("Unexpected: " + (char) ch);
        }

        if (eat('^')) {
            Expression<T> a = x.getExpression();
            ParsedExpression<T> bexp = parseFactor();
            Expression<T> b = bexp.getExpression();
            return optimizeBinaryOperator(w -> Math.pow(ObjectTools.asIntSafe(a.eval(w)), ObjectTools.asIntSafe(b.eval(w))), x, bexp, "^");
        }

        return x;
    }

    private static boolean isIdentifierCharFirst(int ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isIdentifierChar(int ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    private ParsedExpression<T> optimizeBinaryOperator(Expression<T> operation, ParsedExpression<T> p1, ParsedExpression<T> p2, String op) throws ExpressionException {
        if (p1.isConstant() && p2.isConstant()) {
            try {
                Object rc = operation.eval(null);
                return new ParsedExpression<>(w -> rc, true, rc.toString());
            } catch (ExecutionException e) {
                throw new ExpressionException(e.getMessage());
            }
        } else {
            return new ParsedExpression<>(operation, false, p1.getDebug() + op + p2.getDebug());
        }
    }


}
