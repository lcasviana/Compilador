enum TokenType {
    PROGRAM,
    END,
    SCAN,
    PRINT,
    INT,
    STRING,
    IF,
    THEN,
    ELSE,
    DO,
    WHILE,

    INT_CONSTANT, // digit{digit}
    LITERAL, // "{caracter ASCII}"
    IDENTIFIER, // letter{letter|digit}

    EQUALS, // ==
    DIFFERENT, // !=
    GREATER, // >
    GREATERTHAN, // >=
    LESS, // <
    LESSTHAN, // <=
    ASSIGN, // =
    AND, // &&
    OR, // ||
    NOT, // !

    SEMICOLON, // ;
    PARENTHESES_OPEN, // (
    PARENTHESES_CLOSE, // )
    ADDITION, // +
    SUBTRACTION, // -
    MULTIPLICATION, // *
    DIVISION, // /
    COMMA // ,
}