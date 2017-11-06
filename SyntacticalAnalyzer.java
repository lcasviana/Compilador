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
        if (token.Type == type) {
            Advance();
            return true;
        }
        System.err.println("Expected token " + type + " not found. Found " + token.Type + " instead on line " + Line + ".");
        Error = true;
        Advance();
        return Eat(type);
    }

    private boolean EatIf(TokenType type) throws Exception {
        if (token.Type == type)
            return Eat(type);
        return false;
    }

    public void program() throws Exception {
        Eat(TokenType.PROGRAM);
        declList();
        stmtList();
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

    private boolean stmtList() throws Exception {
        do {
            if (EatIf(TokenType.IDENTIFIER)) { // Assign Stmt
                Eat(TokenType.ASSIGN);
                simpleExpr();
                Eat(TokenType.SEMICOLON);
            } else if (EatIf(TokenType.IF)) { // If stmt
                expression();
                Eat(TokenType.THEN);
                stmtList();
                if (EatIf(TokenType.ELSE))
                    stmtList();
                Eat(TokenType.END);
            } else if (EatIf(TokenType.DO)) { // Do while
                stmtList();
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
            } else
                return false;
        } while (stmtList());
        return false;
    }

    private void expression() throws Exception {
        simpleExpr();
        if (relop(false));
            simpleExpr();
    }

    private void simpleExpr() throws Exception {
        term();
        if (addop())
            term();
    }

    private void term() throws Exception {
        factorA();
        if (mulop())
            factorA();
    }

    private void factorA() throws Exception {
        if (EatIf(TokenType.NOT)
         || EatIf(TokenType.SUBTRACTION));
        factor();
    }

    private void factor() throws Exception {
        if (EatIf(TokenType.IDENTIFIER)
         || EatIf(TokenType.INT_CONSTANT))
            return;
        if (EatIf(TokenType.PARENTHESES_OPEN)) {
            expression();
            Eat(TokenType.PARENTHESES_CLOSE);
            return;
        }
        throw new Exception("Unexpected token found " + token.Type + " on line " + Line + ". Expected IDENTIFIER, INT_CONSTANT or PARENTHESES_OPEN");
    }

    private boolean relop(boolean Throws) throws Exception {
        if (EatIf(TokenType.EQUALS)
         || EatIf(TokenType.GREATER)
         || EatIf(TokenType.GREATERTHAN)
         || EatIf(TokenType.LESS)
         || EatIf(TokenType.LESSTHAN)
         || EatIf(TokenType.DIFFERENT))
            return true;
        if (Throws)
            throw new Exception("Unexpected token found " + token.Type + " on line " + Line + ". Expected EQUALS, GREATER, GREATERTHAN, LESS, LESSTHAN or DIFFERENT.");
        return false;
    }

    private boolean addop() throws Exception {
        if (EatIf(TokenType.ADDITION)
         || EatIf(TokenType.SUBTRACTION)
         || EatIf(TokenType.OR))
            return true;
        return false;
    }

    private boolean mulop() throws Exception {
        if (EatIf(TokenType.MULTIPLICATION)
         || EatIf(TokenType.DIVISION)
         || EatIf(TokenType.AND))
            return true;
        return false;
    }
}