package alxkolb.yoi.run;
import alxkolb.yoi.lexer.Lexer;
import alxkolb.yoi.parser.Parser;
import alxkolb.yoi.rpn.RPN;
import alxkolb.yoi.stackMachine.StackMachine;
import alxkolb.yoi.utils.Token;
import java.util.LinkedList;
public class Run {
    public Run(String inputData) throws Exception{
        Lexer lexer = new Lexer(
                "WHILE_KW", "while",
                "STRUCT_NAME", "list|set",
                "OPERATION_KW", "add|remove|has",
                "PRINT_KW", "print",
                "FOR_KW", "for",
                "IF_KW", "if",
                "IS_KW", "is",
                "LET_KW", "let",
                "GET", "get",
                "OPEN_BRACKET", "\\(",
                "CLOSE_BRACKET", "\\)",
                "ASSIGN_OP", "=",
                "ARITHMETIC_OP", "\\+|-|\\*|/",
                "EXPR_END", ";",
                "OPEN_BRACE", "\\{",
                "CLOSE_BRACE", "}",
                "LOGIC_OP", "<|>|==|<=|>=|<>",
                "SYS", "\\@[a-zA-Z]*", // print table
                "VAR", "[a-zA-Z]+[0-9]*",
                "NUM", "[0-9]+"
        );

        final LinkedList<Token> lexerTokens = lexer.getTokens(inputData);

        System.out.println("Tokens from Lexer:");
        for (Token token:lexerTokens)
            System.out.println(token);

        nextApp("lexer");

        Parser parser = new Parser(lexerTokens);
        boolean correct = parser.parse();
        System.out.println("Parser :: " + correct);
        if (!correct)
            System.exit(1);

        nextApp("parser");

        final LinkedList<Token> rpnTokens = new RPN().getOutRPN(lexerTokens);

        System.out.println("Tokens from RPN:");
        for (Token token:rpnTokens)
            System.out.println(token);

        nextApp("rpn");

        StackMachine stackMachine = new StackMachine(rpnTokens);
        System.out.println("StackMachine:");
        stackMachine.run();
    }
    private void nextApp(String programPart) {
        System.out.println("\n" + programPart + " -- ok\n//////////\n");
    }
}
