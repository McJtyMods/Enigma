package mcjty.enigma.parser;

import org.apache.commons.lang3.text.StrBuilder;

public class ExpressionParser {

    private final StringPointer str;
    private final ExpressionContext context;
    private int ch;

    public ExpressionParser(StringPointer str, ExpressionContext context) {
        this.str = str;
        this.context = context;
    }

    public static Expression eval(final StringPointer str, ExpressionContext context) {
        return new ExpressionParser(str, context).parse();
    }

    private void nextChar() {
        str.inc();
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
                str.dec();
                return false;
            }
        }
        return false;
    }

    public Expression parse() {
        nextChar();
        Expression x = parseExpression();
        if (str.hasMore() && str.current() != ',' && str.current() != ':') {
            str.dec();
        }
        return x;
    }

    // Grammar:
    // expression = termequals | expression `==` termequals | expression `!=` termequals
    // termequals = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor
    // factor = `+` factor | `-` factor | `(` expression `)`
    //        | number | functionName factor | factor `^` factor

    private Expression parseExpression() {
        Expression x = parseTermEquals();
        while (true) {
            if (eat2('=', '=')) {
                Expression a = x;
                Expression b = parseTermEquals();
                x = w -> ObjectTools.equals(a.eval(w), b.eval(w));
            } else if (eat2('!', '=')) {
                Expression a = x;
                Expression b = parseTermEquals();
                x = w -> !ObjectTools.equals(a.eval(w), b.eval(w));
            } else {
                return x;
            }
        }
    }

    private Expression parseTermEquals() {
        Expression x = parseTerm();
        while (true) {
            if (eat('+')) {
                Expression a = x;
                Expression b = parseTerm();
                x = w -> ObjectTools.add(a.eval(w), b.eval(w));
            } else if (eat('-')) {
                Expression a = x;
                Expression b = parseTerm();
                x = w -> ObjectTools.sub(a.eval(w), b.eval(w));
            } else {
                return x;
            }
        }
    }

    private Expression parseTerm() {
        Expression x = parseFactor();
        while (true) {
            if (eat('*')) {
                Expression a = x;
                Expression b = parseFactor();
                x = w -> ObjectTools.mul(a.eval(w), b.eval(w));
            } else if (eat('/')) {
                Expression a = x;
                Expression b = parseFactor();
                x = w -> ObjectTools.div(a.eval(w), b.eval(w));
            } else {
                return x;
            }
        }
    }

    private Expression parseFactor() {
        if (eat('+')) {
            return parseFactor(); // unary plus
        }
        if (eat('-')) {
            Expression a = parseFactor();
            return w -> ObjectTools.sub(0, a.eval(w));
        }

        Expression x;
        int startPos = str.index();
        if (eat('(')) { // parentheses
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                nextChar();
            }
            int d = Integer.parseInt(str.substring(startPos, str.index()));
            x = w -> d;
        } else if (ch == '"' || ch == '\'') {
            int toquote = ch;
            StrBuilder builder = new StrBuilder();
            nextChar();
            while (ch != toquote) {
                if (ch == '\\') {
                    nextChar();
                }
                builder.append((char)ch);
                nextChar();
            }
            nextChar();
            String s = builder.toString();
            x = w -> s;
        } else if (ch >= 'a' && ch <= 'z') { // functions
            while (ch >= 'a' && ch <= 'z') {
                nextChar();
            }
            String func = str.substring(startPos, str.index());
            if ("sqrt".equals(func)) {
                x = parseFactor();
                Expression finalX = x;
                x = w -> Math.sqrt(ObjectTools.asIntSafe(finalX.eval(w)));
            } else if (context.isVariable(func)) {
                x = context.getVariable(func);
            } else if (context.isFunction(func)) {
                x = parseFactor();
                Expression finalX = x;
                ExpressionFunction function = context.getFunction(func);
                x = w -> function.eval(w, finalX.eval(w));
            } else {
                x = w -> func;
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^')) {
            Expression a = x;
            Expression b = parseFactor();
            x = w -> Math.pow(ObjectTools.asIntSafe(a.eval(w)), ObjectTools.asIntSafe(b.eval(w))); // exponentiation
        }

        return x;
    }
}
