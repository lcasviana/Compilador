public class Token {
    public String Value;
    public TokenType Type;

    public Token(String value, TokenType type) {
        Value = value;
        Type = type;
    }

    public Token(TokenType type) {
        Type = type;
    }

    @Override
    public String toString() {
        if(this.Type == TokenType.LITERAL || this.Type == TokenType.IDENTIFIER)
            return "[" + Value + ", " + Type + ")";
        return "[" + Type + "]";
    }
}