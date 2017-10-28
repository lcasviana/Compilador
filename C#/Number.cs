class Number : Token {
    public new int Value;

    public Number(int value) : base(TokenType.INT_CONSTANT) {
        Value = value;
    }

    public override string ToString() {
        return "[" + Value + ", " + Type + "]";
    }
}