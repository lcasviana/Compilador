class Token {
    public string Value;
    public TokenType Type;

    public Token(string value, TokenType type) {
        Value = value;
        Type = type;
    }

    public Token(TokenType type) {
        Type = type;
    }

    public override string ToString() {
        if (Type == TokenType.LITERAL || Type == TokenType.IDENTIFIER)
            return "[" + Value + ", " + Type + "]";
        return "[" + Type + "]";
    }
}