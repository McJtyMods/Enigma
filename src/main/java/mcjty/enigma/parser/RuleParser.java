package mcjty.enigma.parser;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.Scope;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RuleParser {

    public static List<TokenizedLine> parse(File file) throws ParserException {
        if (!file.exists()) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return parse(reader);
        } catch (FileNotFoundException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        } catch (IOException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
    }

    private static List<TokenizedLine> parse(BufferedReader reader) throws IOException, ParserException {
        List<TokenizedLine> lines = new ArrayList<>();
        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            TokenizedLine tokenizedLine = getTokenizedLine(line, i);
            if (tokenizedLine != null) {
                tokenizedLine.dump();
                lines.add(tokenizedLine);
            }
            line = reader.readLine();
            i++;
        }
        return lines;
    }

    private static TokenizedLine getTokenizedLine(String line, int linenumber) throws ParserException {
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
        boolean endsWithColon = false;
        if (line.endsWith(":")) {
            endsWithColon = true;
            line = line.substring(0, line.length() - 1);
        }

        StrTokenizer tokenizer = new StrTokenizer(line, ' ', '"');
        if (!tokenizer.hasNext()) {
            throw new ParserException("Truncated line!", linenumber);
        }

        int parameters = 0;

        String token = tokenizer.next();
        MainToken mainToken = MainToken.getTokenByName(token);
        if (mainToken == null) {
            throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
        }
        parameters = mainToken.getParameters();

        Token secondaryToken = null;
        if (mainToken.isHasSecondaryToken()) {
            if (!tokenizer.hasNext()) {
                throw new ParserException("Truncated line!", linenumber);
            }
            token = tokenizer.next();
            secondaryToken = Token.getTokenByName(token);
            if (secondaryToken == null) {
                throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
            }
            parameters = secondaryToken.getParameters();
        }

        List<String> params = new ArrayList<>(parameters);
        for (int t = 0 ; t < parameters ; t++) {
            if (!tokenizer.hasNext()) {
                throw new ParserException("Truncated line!", linenumber);
            }
            token = tokenizer.next();
            params.add(token);
        }

        return new TokenizedLine(indentation, linenumber, mainToken, secondaryToken, params, endsWithColon);
    }

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        System.out.println("dir = " + dir);
        File f = new File("out/production/enigma/assets/enigma/rules/ruleexample");

        try {
            List<TokenizedLine> lines = parse(f);
            Scope root = ProgramParser.parse(lines);
            root.dump(0);
        } catch (ParserException e) {
            System.out.println("e.getMessage() = " + e.getMessage() + " at line " + e.getLinenumber());
        }
    }
}
