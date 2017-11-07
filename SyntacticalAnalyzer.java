public class SyntacticalAnalyzer {
    public boolean Error;
    private Token token;
    public int Line = 1;
    
    private LexicalAnalyzer Lexer;

    public SyntacticalAnalyzer(LexicalAnalyzer lexer) {
        Lexer = lexer;
        try {
            token = Lexer.NextToken();
            program();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void Advance() {
        token = Lexer.NextToken();
        Line = Lexer.Line;
    }

    private boolean Eat(TokenType type) throws Exception {
        if (token.Type != type) {
            System.err.println("Expected token " + type + " not found. Found " + token.Type + " instead on line " + Line + ".");
            Error = true;
        } else
            Advance();
        return true;
    }

    private boolean EatIf(TokenType type) throws Exception {
        if (token.Type == type)
            return Eat(type);
        return false;
    }

    public void program() throws Exception {
        Eat(TokenType.PROGRAM);
        declList();
        stmtList(true);
        Eat(TokenType.END);
        Eat(TokenType.EOF);
    }

    private void declList() throws Exception {
        while (EatIf(TokenType.INT)
            || EatIf(TokenType.STRING)) { // Type
            while (Eat(TokenType.IDENTIFIER)
                && EatIf(TokenType.COMMA));
            Eat(TokenType.SEMICOLON);
        }
    }

    private boolean stmtList(boolean exception) throws Exception {
        do {
            if (EatIf(TokenType.IDENTIFIER)) { // Assign Stmt
                Eat(TokenType.ASSIGN);
                simpleExpr();
                Eat(TokenType.SEMICOLON);
            } else if (EatIf(TokenType.IF)) { // If stmt
                expression();
                Eat(TokenType.THEN);
                stmtList(true);
                if (EatIf(TokenType.ELSE))
                    stmtList(true);
                Eat(TokenType.END);
            } else if (EatIf(TokenType.DO)) { // Do while
                stmtList(true);
                Eat(TokenType.WHILE);
                expression();
                Eat(TokenType.END);
            } else if (EatIf(TokenType.SCAN)) { // scan();
                Eat(TokenType.PARENTHESES_OPEN);
                Eat(TokenType.IDENTIFIER);
                Eat(TokenType.PARENTHESES_CLOSE);
                Eat(TokenType.SEMICOLON);
            } else if (EatIf(TokenType.PRINT)) { // print();
                Eat(TokenType.PARENTHESES_OPEN);
                if (EatIf(TokenType.LITERAL));
                else simpleExpr();
                Eat(TokenType.PARENTHESES_CLOSE);
                Eat(TokenType.SEMICOLON);
            } else if (exception)
                throw new Exception("Expected token xXx not found. Found " + token.Type + " instead on line " + Line + ".");
            else
                return false;
        } while (stmtList(false));
        return false;
    }

    private void expression() throws Exception {
        if (EatIf(TokenType.EQUALS)
         || EatIf(TokenType.GREATER)
         || EatIf(TokenType.GREATERTHAN)
         || EatIf(TokenType.LESS)
         || EatIf(TokenType.LESSTHAN)
         || EatIf(TokenType.DIFFERENT))
            simpleExpr();
    }

    private void simpleExpr() throws Exception {
        if (EatIf(TokenType.NOT)
         || EatIf(TokenType.SUBTRACTION));
        factor();
        mulop();
        addop();
    }

    private void factor() throws Exception {
        if (EatIf(TokenType.IDENTIFIER)
         || EatIf(TokenType.INT_CONSTANT)
         || EatIf(TokenType.LITERAL))
            return;
        if (EatIf(TokenType.PARENTHESES_OPEN)) {
            expression();
            Eat(TokenType.PARENTHESES_CLOSE);
            return;
        }
        throw new Exception("Expected token xXx not found. Found " + token.Type + " instead on line " + Line + ".");
    }

    private void addop() throws Exception {
        if (EatIf(TokenType.ADDITION)
         || EatIf(TokenType.SUBTRACTION)
         || EatIf(TokenType.OR)) {
            factor();
            mulop();
            addop();
        }
    }

    private void mulop() throws Exception {
        if (EatIf(TokenType.MULTIPLICATION)
         || EatIf(TokenType.DIVISION)
         || EatIf(TokenType.AND)) {
            factor();
            mulop();
        }
    }
}