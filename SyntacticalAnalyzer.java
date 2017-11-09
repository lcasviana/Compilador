public class SyntacticalAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token token;
    private int line;
    public boolean Error = false;

    public SyntacticalAnalyzer(LexicalAnalyzer lexicalAnalyzer) throws Exception
    {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.token = lexicalAnalyzer.GetToken();
        this.line = 1;
        program();
    }

    private void advance() throws Exception
    {
        this.token = lexicalAnalyzer.GetToken();
        this.line = lexicalAnalyzer.Line;
    }

    private boolean eatIf(TokenType tokenType) throws Exception
    {
        if (this.token != null && this.token.Type == tokenType)
        {
            advance();
            return true;
        }
        return false;
    }

    private boolean eat(TokenType tokenType) throws Exception
    {
        if (this.token != null && this.token.Type == tokenType)
        {
            advance();
            return true;
        }
        this.Error = true;
        throw new Exception("Expected token '"+ tokenType +"' not found. Found '"+token.Type+"' instead on line "+line+".");
    }

    public void program() throws Exception
    {
        eat(TokenType.PROGRAM);
        declList();
        stmtList(true);
        eat(TokenType.END);
        eat(TokenType.EOF);
    }

    private void declList() throws Exception
    {
        while (eatIf(TokenType.INT) || eatIf(TokenType.STRING)) // Type
        {
            do
            {
                eat(TokenType.IDENTIFIER);
            } while (eatIf(TokenType.COMMA));
            eat(TokenType.SEMICOLON);
        }
    }

    private boolean stmtList(boolean throwsException) throws Exception
    {
        do
        {
            if (eatIf(TokenType.IDENTIFIER)) // Assign Stmt
            {
                eat(TokenType.ASSIGN);
                simpleExpr();
                eat(TokenType.SEMICOLON);
            }
            else if (eatIf(TokenType.IF)) // If stmt
            {
                expression();
                eat(TokenType.THEN);
                stmtList(true);
                if (eatIf(TokenType.ELSE))
                {
                    stmtList(true);
                }
                eat(TokenType.END);
            }
            else if (eatIf(TokenType.DO)) // Do while
            {
                stmtList(true);
                eat(TokenType.WHILE);
                expression();
                eat(TokenType.END);
            }
            else if (eatIf(TokenType.SCAN)) // scan();
            {
                eat(TokenType.PARENTHESES_OPEN);
                eat(TokenType.IDENTIFIER);
                eat(TokenType.PARENTHESES_CLOSE);
                eat(TokenType.SEMICOLON);
            }
            else if (eatIf(TokenType.PRINT)) // print();
            {
                eat(TokenType.PARENTHESES_OPEN);
                if (eatIf(TokenType.LITERAL)) { }
                else simpleExpr();
                eat(TokenType.PARENTHESES_CLOSE);
                eat(TokenType.SEMICOLON);
            }
            else if (throwsException)
            {
                throw new Exception("Unexpected token '"+token.Type+"' found on line {line}.");
            }
            else
            {
                return false;
            }
        } while (stmtList(false));
        return false;
    }

    private void expression() throws Exception
    {
        simpleExpr();
        if (eatIf(TokenType.EQUALS)
            || eatIf(TokenType.GREATER)
            || eatIf(TokenType.GREATERTHAN)
            || eatIf(TokenType.LESS)
            || eatIf(TokenType.LESSTHAN)
            || eatIf(TokenType.DIFFERENT))
        {
            simpleExpr();
        }
    }

    private void simpleExpr() throws Exception
    {
        if (eatIf(TokenType.NOT) || eatIf(TokenType.SUBTRACTION)) { }
        factor();
        mulop();
        addop();
    }

    private void mulop() throws Exception
    {
        if (eatIf(TokenType.MULTIPLICATION)
            || eatIf(TokenType.DIVISION)
            || eatIf(TokenType.AND))
        {
            factor();
            mulop();
        }
    }

    private void addop() throws Exception
    {
        if (eatIf(TokenType.ADDITION)
            || eatIf(TokenType.SUBTRACTION)
            || eatIf(TokenType.OR))
        {
            factor();
            mulop();
            addop();
        }
    }

    private void factor() throws Exception
    {
        if (eatIf(TokenType.IDENTIFIER)
            || eatIf(TokenType.INT_CONSTANT)
            || eatIf(TokenType.LITERAL))
        {
            return;
        }
        if (eatIf(TokenType.PARENTHESES_OPEN))
        {
            expression();
            eat(TokenType.PARENTHESES_CLOSE);
            return;
        }
        throw new Exception("Unexpected token found '"+token.Type+"' on line "+line+". Expected "+TokenType.IDENTIFIER+", "+TokenType.INT_CONSTANT+", "+TokenType.LITERAL+" or "+TokenType.PARENTHESES_OPEN);
    }
}