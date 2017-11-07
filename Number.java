public class Number extends Token {
    public int Value;

    public Number(int value) {
        super(TokenType.INT_CONSTANT);
        Value = value;
    }

    @Override
    public String toString() {
        return "[" + Value + ", " + Type + "]";
    }
}