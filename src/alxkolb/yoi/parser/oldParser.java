package alxkolb.yoi.parser;

import alxkolb.yoi.utils.Token;
import java.util.LinkedList;
import java.lang.Exception;

public class oldParser {
    private final LinkedList<Token> tokens;
    private int pos = 0;

    public oldParser(LinkedList<Token> tokens){
        this.tokens = tokens;
    }

    public boolean parse(){
        try {
            lang();
        } catch (ParseException p) {
            System.out.println(p.getMessage());
            return false;
        } catch (Exception e){
            // Кончились токены
            return true;
        }
        return true;
    }

    private void compareAndInc(String tokenType) throws Exception {
        if (tokenType.equals(
                tokens.get(pos).getType()))
            pos++;
        else
            exception(tokenType);
    }

    private void exception(String tokenType) throws Exception {
        throw new ParseException(tokenType, tokens.get(pos),pos);
    }
    private void exception(int errorPos, Exception exception) throws Exception {
        // Если подошёл первый токен -- ошибка не в определении типа, а в последовательности токенов
        if (errorPos != pos)
            throw exception;
    }

    private void lang() throws Exception {
        while (true)
            expr();
    }

    private void expr() throws Exception {
        int startPos = pos;

        try {
            _print();
            return;
        } catch (Exception e) {
            exception(startPos,e);
        }

        try {
            _set_val();
            return;
        } catch (Exception e){
            exception(startPos,e);
        }
        
        try {
        	_while();
        	return;
        } catch (Exception e) {
        	exception(startPos,e);
        }
        
        try {
        	_if();
        	return;
        } catch (Exception e) {
        	exception(startPos,e);
        }

        exception("expr");
    }

    private void _while() throws Exception{
    	WHILE();
    	_cond();
    	_body();
    }
    
    private void _if() throws Exception{
    	IF();
    	_cond();
    	_body();
    }
    
    private void WHILE() throws Exception{
    	compareAndInc("WHILE");
    }
    private void IF() throws Exception{
    	compareAndInc("IF");
    }
    private void _cond() throws Exception{
    	_value();
    	LOGIC_OP();
    	_value();
    }
    
    private void _body() throws Exception{
    	expr();
    }
    
    private void LOGIC_OP() throws Exception{
    	compareAndInc("LOGIC_OP");
    }

    private void _print() throws Exception {
        PRINT();
        VAR();
    }

    private void PRINT() throws Exception{
        compareAndInc("PRINT");
    }
    private void VAR() throws Exception{
        compareAndInc("VAR");
    }

    private void _set_val() throws Exception{
        VAR();
        ASSIGN_OP();
        _math_op();
    }

    private void ASSIGN_OP() throws Exception{
        compareAndInc("ASSIGN_OP");
    }
    private void _value() throws Exception{
        int startPos = pos;
        try {
            VAR();
            return;
        } catch (Exception e){
            exception(startPos,e);
        }

        try {
            NUM();
            return;
        } catch (Exception e){
            exception(startPos,e);
        }

        exception("_value");
    }

    private void _math_op() throws Exception{
    	_value();
    	////////////////////
    	int startPos = pos; // только для данного блока
    	try {
    		OP();
    		_value();
    		return;
    	} catch (Exception e) {
    		pos = startPos;
    	}
        ////////////////////
    }
    
    private void OP() throws Exception{
    	compareAndInc("OP");
    }
    
    private void NUM() throws Exception{
        compareAndInc("NUM");
    }

}
