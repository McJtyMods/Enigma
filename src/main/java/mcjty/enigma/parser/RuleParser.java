package mcjty.enigma.parser;

import mcjty.enigma.Enigma;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.*;

public class RuleParser {

    public static void parse(File file) {
        if (!file.exists()) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            parse(reader);
        } catch (FileNotFoundException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        } catch (IOException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
    }

    private static void parse(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            TokenizedLine tokenizedLine = getTokenizedLine(line);
            if (tokenizedLine != null) {
                tokenizedLine.dump();
            }
            line = reader.readLine();
        }
    }

    private static TokenizedLine getTokenizedLine(String line) {
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
        String[] tokens = StringUtils.split(line);

        MainToken mainToken = MainToken.getTokenByName(tokens[0]);
        if (mainToken == null) {
            System.out.println("ERROR: Unknown token '" + tokens[0] + "'!");
            return null;
        }
        Token secondaryToken = null;
        if (mainToken.isHasSecondaryToken()) {
            secondaryToken = Token.getTokenByName(tokens[1]);
            if (secondaryToken == null) {
                System.out.println("ERROR: Unknown token '" + tokens[1] + "'!");
                return null;
            }
        }

        return new TokenizedLine(indentation, mainToken, secondaryToken, tokens, endsWithColon);
    }

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        System.out.println("dir = " + dir);
        File f = new File("out/production/enigma/assets/enigma/rules/ruleexample");

        parse(f);

    }
}
