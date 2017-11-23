public class Token {
    public String Value;
    public TokenType Type;
    public SemanticTokenType SemanticType;
    
    public Token(String value, TokenType type) {
        Value = value;
        Type = type;
    }

    public Token(TokenType type) {
        Type = type;
    }

    public SemanticTokenType getSemanticTypeFromType() {
        if(Type == TokenType.INT) return SemanticTokenType.INT;
        else return SemanticTokenType.STRING;
    }

    @Override
    public String toString() {
        if (this.Type == TokenType.LITERAL
         || this.Type == TokenType.IDENTIFIER)
            return "[" + Value + ", " + Type + ")";
        return "[" + Type + "]";
    }
}