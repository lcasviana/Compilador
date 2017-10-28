class Identifier : Token {
    public int Position;

    public Identifier(int position, string value) : base(value, TokenType.IDENTIFIER) {
        Position = position;
    }

    public override string ToString() {
        return "[" + Position + ", " + Value + ", " + Type + "]";
    }
}