public class Token {
    String value;
    TokenType type;

    Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    Token(TokenType type) {
        this.type = type;
    }

    Token(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        if(this.type == TokenType.LITERAL || this.type == TokenType.IDENTIFIER)
            return "(" + this.value + ", " + this.type + ")";
        if(this.type != null)
            return "(" + this.type + ")";
        return this.value;
    }
}