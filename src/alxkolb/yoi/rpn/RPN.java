package alxkolb.yoi.rpn;

import alxkolb.yoi.lexer.Token;
import java.util.LinkedList;
import java.util.Stack;

public class RPN {
    // Обратная польская запись в виде токенов
    private LinkedList<Token> outRPN = new LinkedList<>();

    private boolean higherPriority(String op1, String op2) {
        return (op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"));
    }

    private LinkedList<Token> getExprInBrackets(LinkedList<Token> tokens, String type) {
        LinkedList<Token> body = new LinkedList<>();
        int open = 1;
        int close = 0;
        while (open != close) {
            Token token = tokens.poll();
            if (token.getType().equals("OPEN_" + type))
                open++;
            else if (token.getType().equals("CLOSE_" + type))
                close++;
            body.add(token);
        }
        body.removeLast();
        return body;
    }

    private void whileToRPN(LinkedList<Token> tokens) {
        boolean stop = false;
        int start = outRPN.size();
        int end = 0;
        Token labelStart;
        Token labelEnd = null;

        while (!stop) {
            Token token = tokens.poll();
            String type = token.getType();
            switch (type) {
                case "OPEN_BRACKET":
                    LinkedList<Token> condition = getExprInBrackets(tokens, "BRACKET");
                    getRPN(condition);
                    tokens.addFirst(new Token(")", "CLOSE_BRACKET"));
                    break;
                case "CLOSE_BRACKET":
                    labelEnd = new Token("", "LABEL_END");
                    outRPN.add(labelEnd);
                    outRPN.add(new Token("!F", "GOTO_BY_FALSE"));
                    break;
                case "OPEN_BRACE":
                    LinkedList<Token> body = getExprInBrackets(tokens, "BRACE");
                    getRPN(body);
                    tokens.addFirst(new Token("}", "CLOSE_BRACE"));
                    break;
                case "CLOSE_BRACE":
                    labelStart = new Token(String.valueOf(start), "LABEL_START");
                    outRPN.add(labelStart);
                    outRPN.add(new Token("!", "GOTO"));
                    end = outRPN.size();
                    stop = true;
                    break;
            }
        }

        labelEnd.setValue(String.valueOf(end));
    }

    private void forToRPN(LinkedList<Token> tokens) {
        boolean stop = false;
        int start = 0;
        int end = 0;

        Token t, labelStart, labelEnd = null;

        LinkedList<Token> initialization = new LinkedList<>();
        LinkedList<Token> stopCondition = new LinkedList<>();
        LinkedList<Token> inc = new LinkedList<>();

        while (!stop) {
            Token token = tokens.poll();
            String type = token.getType();
            switch (type) {
                case "OPEN_BRACKET":
                    LinkedList<Token> allCondition = getExprInBrackets(tokens, "BRACKET");

                    while (!(t = allCondition.poll()).getType().equals("EXPR_END"))
                        initialization.addLast(t);

                    while (!(t = allCondition.poll()).getType().equals("EXPR_END"))
                        stopCondition.addLast(t);

                    inc = allCondition;

                    getRPN(initialization);
                    start = outRPN.size();
                    getRPN(stopCondition);

                    tokens.addFirst(new Token(")", "CLOSE_BRACKET"));
                    break;

                case "CLOSE_BRACKET":
                    labelEnd = new Token("", "LABEL_END");
                    outRPN.add(labelEnd);
                    outRPN.add(new Token("!F", "GOTO_BY_FALSE"));
                    break;

                case "OPEN_BRACE":
                    LinkedList<Token> body = getExprInBrackets(tokens, "BRACE");
                    getRPN(body);
                    getRPN(inc);
                    tokens.addFirst(new Token("}", "CLOSE_BRACE"));
                    break;

                case "CLOSE_BRACE":
                    labelStart = new Token(String.valueOf(start), "LABEL_START");
                    outRPN.add(labelStart);
                    outRPN.add(new Token("!", "GOTO"));
                    end = outRPN.size();
                    stop = true;
                    break;
            }
        }
        labelEnd.setValue(String.valueOf(end));
    }

    private void ifToRPN(LinkedList<Token> tokens) {
        boolean stop = false;
        int end = 0;
        Token labelEnd = null;
        do {
            Token token = tokens.poll();
            String type = token.getType();
            switch (type) {
                case "OPEN_BRACKET":
                    LinkedList<Token> condition = getExprInBrackets(tokens, "BRACKET");
                    getRPN(condition);
                    tokens.addFirst(new Token(")", "CLOSE_BRACKET"));
                    break;
                case "CLOSE_BRACKET":
                    labelEnd = new Token("", "LABEL_END");
                    outRPN.add(labelEnd);
                    outRPN.add(new Token("!F", "GOTO_BY_FALSE"));
                    break;
                case "OPEN_BRACE":
                    LinkedList<Token> body = getExprInBrackets(tokens, "BRACE");
                    getRPN(body);
                    tokens.addFirst(new Token("}", "CLOSE_BRACE"));
                    break;
                case "CLOSE_BRACE":
                    end = outRPN.size();
                    stop = true;
                    break;
            }
        } while (!stop);
        labelEnd.setValue(String.valueOf(end));
    }

    private LinkedList<Token> getRPN(LinkedList<Token> tokens) {
        Stack<Token> stack = new Stack<>();
        Token upperInStack;

        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            String type = token.getType();
            switch (type) {
                // Если следующий токен - операнд, то сразу добавить в полиз
                case "VAR":
                case "SYS": // print table
                case "NUM":
                case "STRUCT_NAME":
                    outRPN.add(token);
                    break;
                case "ARITHMETIC_OP":
                    // Вытолкнуть из стека все операции с более высоким приоритетом
                    while (!stack.isEmpty() && (upperInStack = stack.peek()).getType().equals("ARITHMETIC_OP") &&
                            higherPriority(upperInStack.getValue(), token.getValue())) {
                        outRPN.add(stack.pop());
                    }
                    stack.push(token);
                    break;
                case "ASSIGN_OP":
                case "OPEN_BRACKET":
                case "OPERATION_KW":
                case "PRINT_KW":
                case "IS_KW":
                case "LOGIC_OP":
                case "GET":
                    stack.push(token);
                    break;
                case "CLOSE_BRACKET":
                    // Выталкивать из стека все операции пока не встретится открывающаяся скобка
                    while (!stack.isEmpty() && !(upperInStack = stack.pop()).getType().equals("OPEN_BRACKET")) {
                        outRPN.add(upperInStack);
                    }
                    break;
                case "EXPR_END":
                    while (!stack.isEmpty()) {
                        outRPN.add(stack.pop());
                    }
                    break;
                case "WHILE_KW":
                    whileToRPN(tokens);
                    break;
                case "FOR_KW":
                    forToRPN(tokens);
                    break;
                case "IF_KW":
                    ifToRPN(tokens);
                    break;
            }
        }

        // Выталкнуть оставшиеся токены из стека в полиз
        while (!stack.isEmpty())
            outRPN.add(stack.pop());
        return outRPN;
    }

    public LinkedList<Token> getOutRPN(LinkedList<Token> tokensFromLexer) {
        return getRPN(
                new LinkedList<Token>(tokensFromLexer));
    }
}
