package alxkolb.yoi.utils;

public class Token {
    private String value;
    private String type;

    public Token(String value, String type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return (this.value + "  @  " + this.type);
    }

    public String getValue() {
        return value;
    }

    public String getType(){
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }
}

