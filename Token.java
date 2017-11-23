public class Token {
    public String Value;
    public TokenType Type;
    public SemanticTokenType SemanticType;
    
    public Token(String value, TokenType type) {
        this(type);
        Value = value;
    }

    public Token(TokenType type) {
        Type = type;
        SemanticType = SemanticTokenType.NULL;
    }

    public SemanticTokenType getSemanticTypeFromType() {
        if(Type == TokenType.INT || Type == TokenType.INT_CONSTANT) return SemanticTokenType.INT;
        else if(Type == TokenType.STRING || Type == TokenType.LITERAL) return SemanticTokenType.STRING;
        else return SemanticTokenType.ERROR;
    }

    @Override
    public String toString() {
        if (this.Type == TokenType.LITERAL
         || this.Type == TokenType.IDENTIFIER)
            return "[" + Value + ", " + Type + ")";
        return "[" + Type + "]";
    }
}