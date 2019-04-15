package alxkolb.yoi.parser;

import alxkolb.yoi.lexer.Token;

public class ParseException extends Exception{
    //private String message;

    public ParseException(String message) {
        super(message);
        //this.message = message;
    }
    public ParseException(String lexemeType, Token token, int pos){
        super("Ожидали " + lexemeType + ", получили " + token.getType() + " в токене " + pos + " (" + token.getValue() + ")");
    }
    public ParseException(String lexemeType, Token token){
        super("Ожидали " + lexemeType + ", получили " + token.getType() + " (" + token.getValue() + ")");
    }

    @Override
    public String toString(){
        return super.getMessage();
    }
}
