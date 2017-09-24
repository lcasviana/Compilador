class Num extends Token{
    int value;

    Num(int value) {
        super(TokenType.INT_CONSTANT);
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + this.value + ", " + this.type + ")";
    }
}