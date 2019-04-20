package alxkolb.yoi;

import alxkolb.yoi.lexer.Lexer;
import alxkolb.yoi.lexer.Token;
import alxkolb.yoi.parser.Parser;
import alxkolb.yoi.rpn.RPN;
import alxkolb.yoi.stackMachine.StackMachine;

import java.io.IOException;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath, inputData;
            FileReader fileReader = new FileReader();
            if (args.length == 1){
                filePath = args[0];
                inputData = fileReader.read(filePath);
                System.out.println(
                        filePath+
                                ":\n////\n" +
                                inputData +
                                "\n////\n"
                );
            } else {
                inputData = null;
                System.out.println("Не задан входной файл. Запущен тестовый пример.\n");
            }
            run(inputData);
        } catch (IOException e){
            System.err.println("Не найден файл " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void run(String inputData) throws Exception{
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

        if (inputData == null)
            inputData = getTestInputString();

        final LinkedList<Token> lexerTokens = lexer.getTokens(inputData);

        System.out.println("Tokens from Lexer:");
        for (Token t:lexerTokens)
            System.out.println(t);

        nextLab("lexer");

        Parser parser = new Parser(lexerTokens);
        boolean correct = parser.parse();
        System.out.println("Parser :: " + correct);
        if (!correct)
            System.exit(1);

        nextLab("parser");

        LinkedList<Token> rpnTokens = new RPN().getOutRPN(lexerTokens);

        nextLab("rpn");

        StackMachine stackMachine = new StackMachine(rpnTokens);
        System.out.println("StackMachine:");
        stackMachine.run();
    }

    private static void nextLab(String programPart) {
        System.out.println("\n" + programPart + " -- ok\n//////////\n");
    }

    private static String getTestInputString(){
        //////////
        int stringNumber = 7;
        //////////
        String[] strings = new String[]{
                //// 0
                "a = 0;" + "for (i = 0; i < 10; i = i + 2) {" + "a = a + 1;" + "}" + "print a;",
                //// 1
                "a = 6;" +
                        "if (a > (2 + 3)) {" +
                        "a = a + 2; print (2 + 3);" +
                        "}",
                //// 2
                "let a is list; let b is set; print a; print b; print (1+2);",
                //// 3
                "let L is list; L add 5; L add 6; L add 7; print (L get 2);",
                ////
                "let a is set; a add 1; a add 1; a add 50; print a; a add 2; print a; a has 1; a has 2; a has 3;" +
                        "a remove 1; print a; a has 2; a has 1;",
                ////
                "for (i = 0; i < 5; i = i + 1) {print i;}",
                ////
                "c = 1; b = 2; let a is list; a add 5; a add 6; print (b + c);",
                //// 7
                "a=5; b = 1+a; print @var;",
        };
        return strings[stringNumber];
    }

}
