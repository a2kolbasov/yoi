package alxkolb.yoi.stackMachine;

import alxkolb.yoi.lexer.Token;
import alxkolb.yoi.lists.YOIHashSet;
import alxkolb.yoi.lists.YOILinkedList;
import alxkolb.yoi.lists.YOILists;

import java.util.LinkedList;
import java.util.Stack;

public class StackMachine {
    VariableTable table;
    LinkedList<Token> tokens;
    Stack<Token> stack = new Stack<>();

    public StackMachine(LinkedList<Token> tokens){
        this.tokens = tokens;
        this.table = new VariableTable();
    }

    private void typeError(String operarion) throws Exception {
        throw new Exception("Ошибка: несовместимость типов ("+operarion+")");
    }

    private Token calcMathOp(String operation) throws Exception {
        Token tokenOp2 = stack.pop();
        Token tokenOp1 = stack.pop();

        if (tokenOp1.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp1.getValue()).equals("list") || table.getVariableType(tokenOp1.getValue()).equals("set"))
                typeError(operation);
        }

        if (tokenOp2.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp2.getValue()).equals("list") || table.getVariableType(tokenOp2.getValue()).equals("set"))
                typeError(operation);
        }

        int numOp1 = tokenOp1.getType().equals("VAR") ? (int) table.getVariableValue(tokenOp1.getValue()) :
                Integer.valueOf(tokenOp1.getValue());
        int numOp2 = tokenOp2.getType().equals("VAR") ? (int) table.getVariableValue(tokenOp2.getValue()) :
                Integer.valueOf(tokenOp2.getValue());

        int result = 0;
        switch (operation) {
            case "+":
                result = numOp1 + numOp2;
                break;
            case "-":
                result = numOp1 - numOp2;
                break;
            case "*":
                result = numOp1 * numOp2;
                break;
            case "/":
                result = numOp1 / numOp2;
                break;
        }
        return new Token(String.valueOf((int)result), "NUM");
    }

    private Token calcCompOp(String operation) throws Exception {
        Token tokenOp2 = stack.pop();
        Token tokenOp1 = stack.pop();

        if (tokenOp1.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp1.getValue()).equals("list") || table.getVariableType(tokenOp1.getValue()).equals("set"))
                typeError(operation);
        }

        if (tokenOp2.getType().equals("VAR")) {
            if (table.getVariableType(tokenOp2.getValue()).equals("list") || table.getVariableType(tokenOp2.getValue()).equals("set"))
                typeError(operation);
        }

        int numOp1 = tokenOp1.getType().equals("VAR") ? (int) table.getVariableValue(tokenOp1.getValue()) :
                Integer.valueOf(tokenOp1.getValue());
        int numOp2 = tokenOp2.getType().equals("VAR") ? (int) table.getVariableValue(tokenOp2.getValue()) :
                Integer.valueOf(tokenOp2.getValue());

        String result = "";
        switch (operation) {
            case ">":
                result = String.valueOf(numOp1 > numOp2);
                break;
            case "<":
                result = String.valueOf(numOp1 < numOp2);
                break;
            case "==":
                result = String.valueOf(numOp1 == numOp2);
                break;
            case ">=":
                result = String.valueOf(numOp1 >= numOp2);
                break;
            case "<=":
                result = String.valueOf(numOp1 <= numOp2);
                break;
            case "!=":
                result = String.valueOf(numOp1 != numOp2);
                break;
        }

        return new Token(result, "");
    }

    private void assign() throws Exception {
        Integer value = Integer.valueOf(stack.pop().getValue());
        String name = stack.pop().getValue();
        table.addVariable(name, "number", value);
    }

    public void run() throws Exception {
        Token currentToken;
        for (int i = 0; i < tokens.size(); i++) {
            currentToken = tokens.get(i);
            switch (currentToken.getType()) {
                case "VAR":
                case "NUM":
                //case "CONST_FLOAT":
                case "LABEL_START":
                case "LABEL_END":
                case "STRUCT_NAME":
                    stack.push(currentToken);
                    break;
                case "ARITHMETIC_OP":
                    stack.push(calcMathOp(currentToken.getValue()));
                    break;
                case "LOGIC_OP":
                    stack.push(calcCompOp(currentToken.getValue()));
                    break;
                case "ASSIGN_OP":
                    assign();
                    break;
                case "GOTO":
                    Token it = stack.pop();
                    i = it.getType().equals("LABEL_START") || it.getType().equals("LABEL_END") ?
                            Integer.valueOf(it.getValue()) - 1 : -1;
                    break;
                case "GOTO_BY_FALSE":
                    it = stack.pop();
                    if (stack.pop().getValue().equals("false")) {
                        i = Integer.valueOf(it.getValue()) - 1;
                    }
                    break;
                case "PRINT_KW":
                    Token token = stack.pop();
                    String arg = token.getType().equals("VAR") ?
                            table.getVariableValue(token.getValue()).toString() : token.getValue();
                    System.out.println(arg);
                    break;
                case "IS_KW":
                    String type = stack.pop().getValue();
                    String name = stack.pop().getValue();
                    switch (type) {
                        case "list":
                            table.addVariable(name, type, new YOILinkedList());
                            break;
                        case "set":
                            table.addVariable(name, type, new YOIHashSet());
                            break;
                    }
                    break;
                case "OPERATION_KW":
                    Token tokenOperand = stack.pop();
                    Object operand;
                    switch (tokenOperand.getType()) {
                        case "NUM":
                            operand = Integer.valueOf(tokenOperand.getValue());
                            break;
                        default:
                            operand = table.getVariableValue(tokenOperand.getValue());
                            break;
                    }

                    String structureName = stack.pop().getValue();
                    YOILists structure = (YOILists) table.getVariableValue(structureName);
                    switch (currentToken.getValue()) {
                        case "add":
                            structure.add(operand);
                            break;
                        case "has":
                            if (structure.contains(operand)) {
                                System.out.println(structureName + " has " + tokenOperand.getValue());
                            } else {
                                System.out.println(structureName + " has no " + tokenOperand.getValue());
                            }
                            break;
                        case "remove":
                            try {
                                structure.remove(Integer.valueOf(operand.toString()));
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("Элемента с индексом " + operand + " не существует в списке "
                                        + structureName);
                                return;
                            }
                            break;
                    }
                    break;
                case "GET":
                    Integer index = Integer.valueOf(stack.pop().getValue());
                    YOILinkedList list = (YOILinkedList) table.getVariableValue(stack.pop().getValue());
                    try {
                        stack.push(new Token(String.valueOf(list.get(index)), "NUM"));
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Индекс " + index + " отсутствует в списке");
                        return;
                    }
                    break;
            }
        }
    }
}
