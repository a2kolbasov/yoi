package alxkolb.yoi.lexer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    // Лучше массив []
    private LinkedList<Lexeme> lexemes = new LinkedList<>();

    public Lexer(String... inputLexemes) throws Exception{ // s[2n] = name , s[2n-1] = val ...
        if (inputLexemes.length % 2 != 0) {
            /*
            System.err.println("terms % 2 != 0");
            System.exit(1);
            */
            throw new Exception("Lexer :: terms % 2 != 0");
        }
        for (int i = 0; i<inputLexemes.length; i+=2)
            addLexeme(inputLexemes[i],inputLexemes[i+1]);
    }

    private void addLexeme(String type, String reg_value){
        lexemes.add(
                new Lexeme(type, Pattern.compile(reg_value))
        );
    }

    //      ++++++++++++++++++++++++++++++++++

// Scanner.next() = :spase:     .nextLine = \n

    public LinkedList<Token> getTokens(final String inputData) throws Exception{
        LinkedList<Token> tokens = new LinkedList<>();
        // accumulator
        String ac = "";
        Token lastFoundToken = null;
        boolean found;
        // position
        int pos = 0;

        while (pos < inputData.length()) {
            ac = ac + inputData.charAt(pos);
            found = false;

            for (Lexeme lexeme : lexemes) {
                Matcher m = lexeme.pattern.matcher(ac);
                //m.find();
                if (m.matches()) {
                    lastFoundToken = new Token(ac, lexeme.type);
                    found = true;
                    //System.out.println("        found: " + lastFoundToken);
                    break;
                }
            }

            if (found)
                pos++;

            if (!found || pos==inputData.length()) {
                if (lastFoundToken != null ){
                    //System.out.println("add token: " + lastFoundToken + "\n");
                    tokens.add(lastFoundToken);
                    lastFoundToken = null;
                } else {
                    pos++;
                    if (!(ac.equals(" ") || ac.equals("\n")) || ac.equals("\t"))
                        // Обнаружена синтаксическая ошибка
                        throw new Exception("Lexer :: nothing found: " + ac);
                }

                ac = "";
            }
        }
        return tokens;
    }
}
