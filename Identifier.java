public class Identifier extends Token {
    public int Position;

    public Identifier(int position, String value) {
        super(value, TokenType.IDENTIFIER);
        Position = position;
    }
    
    @Override
    public String toString() {
        return "[" + Position + ", " + Value + ", " + Type + "]";
    }
}