package alxkolb.yoi.parser;

import alxkolb.yoi.lexer.Token;
import java.util.LinkedList;

public class Parser {
    private LinkedList<Token> tokens;
    private LinkedList<Token> expr = new LinkedList<>();
    private Token currentToken;

    public Parser(LinkedList<Token> tokensFromLexer){
        // Чтобы не изменять входной LinkedList
        this.tokens = new LinkedList<>(tokensFromLexer);
    }

    public boolean parse() {
        try {
            while (!tokens.isEmpty())
                lang();
        } catch (ParseException p) {
            System.out.println(p.getMessage());
            //p.printStackTrace();
            return false;
        } catch (Exception e){
            //e.printStackTrace();
            //return true;
        } finally {//     TODO : СПРОСИТЬ ОБ ЭТОМ !!!
            //return true;
        }

        return true;
    }

    private void checkToken(String appropriateType) throws ParseException {
        match();
        String currentTokenType = currentToken.getType();
        if (!currentTokenType.equals(appropriateType)) {
            error(currentToken, appropriateType);
        }
    }

    private void error(Token token, String appropriateType) throws ParseException{
        throw new ParseException(appropriateType, token);
    }

    private void match() {
        currentToken = tokens.poll();
        expr.add(currentToken);
    }

    private void lang() throws ParseException {
        expr();
    }

    private void expr() throws ParseException {
        LinkedList<Token> copy = new LinkedList<>(tokens);
        Token t = copy.poll();
        switch (t.getType()) {
            case "VAR":
                switch (copy.poll().getType()) {
                    case "ASSIGN_OP":
                        assignExpr();
                        break;
                    case "OPERATION_KW":
                        structureOperation();
                        break;
                    default:
                        error(t, "VAR | OPERATION_KW");
                        break;
                }
                break;
            case "IF_KW":
                ifStatement();
                break;
            case "WHILE_KW":
            case "FOR_KW":
                anyLoop();
                break;
            case "LET_KW":
                structureDeclaration();
                break;
            case "PRINT_KW":
                printSt();
                break;
            default:
                error(t, "VAR | loop");
        }
    }

    private void anyLoop() throws ParseException {
        Token t = tokens.peek();
        String type = t.getType();
        switch (type) {
            case "WHILE_KW":
                whileLoop();
                break;
            case "FOR_KW":
                forLoop();
                break;
            default:
                error(t, "WHILE_KW | FOR_KW");
        }
    }

    private void whileLoop() throws ParseException {
        whileKw();
        conditionInBr();
        body();
    }

    private void conditionInBr() throws ParseException {
        openBracket();
        condition();
        closeBracket();
    }

    private void condition() throws ParseException {
        operand();
        compOp();
        operand();
    }

    private void body() throws ParseException {
        Token t;
        boolean stop = false;

        openBrace();
        while (!stop) {
            LinkedList<Token> copy = new LinkedList<>(tokens);
            t = copy.poll();
            switch (t.getType()) {
                case "VAR":
                    switch (copy.poll().getType()) {
                        case "ASSIGN_OP":
                            assignExpr();
                            break;
                        case "OPERATION_KW":
                            structureOperation();
                    }
                    break;
                case "WHILE_KW":
                case "FOR_KW":
                    anyLoop();
                    break;
                case "IF_KW":
                    ifStatement();
                    break;
                case "LET_KW":
                    structureDeclaration();
                    break;
                case "CLOSE_BRACE":
                    closeBrace();
                    stop = true;
                    break;
                case "PRINT_KW":
                    printSt();
                    break;
                default:
                    error(t, "VAR | WHILE_KW | FOR_KW | IF_KW | LET_KW");
                    break;
            }
        }
    }

    private void forLoop() throws ParseException {
        forKw();
        forStatements();
        body();
    }

    private void forStatements() throws ParseException {
        openBracket();
        assignExpr();
        condition();
        exprEnd();
        indexChange();
        closeBracket();
    }

    private void indexChange() throws ParseException {
        var();
        assignOp();
        assignValue();
    }

    private void ifStatement() throws ParseException {
        ifKw();
        conditionInBr();
        body();
    }

    private void printSt() throws ParseException {
        printKw();
        operand();
        exprEnd();
    }

    private void structureDeclaration() throws ParseException {
        letKw();
        var();
        isKw();
        structName();
        exprEnd();
    }

    private void getOperation() throws ParseException {
        var();
        get();
        operand();
    }

    private void structureOperation() throws ParseException {
        var();
        operationKw();
        operand();
        exprEnd();
    }

    private void assignExpr() throws ParseException {
        var();
        assignOp();
        assignValue();
        exprEnd();
    }

    private void assignValue() throws ParseException {
        arithmExpr();
    }

    private void inBr() throws ParseException {
        openBracket();
        arithmExpr();
        closeBracket();
    }

    private void arithmExpr() throws ParseException {
        operand();
        while (tokens.peek() != null && tokens.peek().getType().equals("ARITHMETIC_OP")) {
            arOp();
            operand();
        }
    }

    private void operand() throws ParseException {
        LinkedList<Token> t = new LinkedList<>(tokens);
        if (t.poll().getType().equals("OPEN_BRACKET")) {
            inBr();
        } else {
            if (t.poll().getType().equals("GET")) {
                getOperation();
            } else {
                singleOperand();
            }
        }
    }

    private void singleOperand() throws ParseException {
        Token token = tokens.peek();
        switch (token.getType()) {
            case "VAR":
                var();
                break;
            case "NUM":
                constInt();
                break;
            case "CONST_FLOAT":
                constFloat();
                break;
            default:
                error(token, "VAR | NUM | CONST_FLOAT");
                tokens.remove();
        }
    }

    private void var() throws ParseException {
        checkToken("VAR");
    }

    private void assignOp() throws ParseException {
        checkToken("ASSIGN_OP");
    }

    private void openBracket() throws ParseException {
        checkToken("OPEN_BRACKET");
    }

    private void closeBracket() throws ParseException {
        checkToken("CLOSE_BRACKET");
    }

    private void arOp() throws ParseException {
        checkToken("ARITHMETIC_OP");
    }

    private void constInt() throws ParseException {
        checkToken("NUM");
    }

    private void constFloat() throws ParseException {
        checkToken("CONST_FLOAT");
    }

    private void compOp() throws ParseException {
        checkToken("LOGIC_OP");
    }

    private void openBrace() throws ParseException {
        checkToken("OPEN_BRACE");
    }

    private void closeBrace() throws ParseException {
        checkToken("CLOSE_BRACE");
    }

    private void whileKw() throws ParseException {
        checkToken("WHILE_KW");
    }

    private void forKw() throws ParseException {
        checkToken("FOR_KW");
    }

    private void ifKw() throws ParseException {
        checkToken("IF_KW");
    }

    private void letKw() throws ParseException {
        checkToken("LET_KW");
    }

    private void isKw() throws ParseException {
        checkToken("IS_KW");
    }

    private void printKw() throws ParseException {
        checkToken("PRINT_KW");
    }

    private void structName() throws ParseException {
        checkToken("STRUCT_NAME");
    }

    private void operationKw() throws ParseException {
        checkToken("OPERATION_KW");
    }

    private void get() throws ParseException {
        checkToken("GET");
    }

    private void exprEnd() throws ParseException {
        checkToken("EXPR_END");
    }
}
