public class SyntacticalAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token token;
    private Token lastToken;
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
        this.lastToken = this.token;
        this.token = lexicalAnalyzer.GetToken();
        this.line = lexicalAnalyzer.Line;
    }

    private boolean eatIf(TokenType tokenType, boolean checkType) throws Exception
    {
        if (this.token != null && this.token.Type == tokenType)
        {
            if(checkType && this.token.Type == TokenType.IDENTIFIER && this.token.SemanticType == SemanticTokenType.NULL){
                throw new Exception("Using undeclared variable '"+token.Value+"'on line " + line);
            }
            advance();
            return true;
        }
        return false;
    }

    private boolean eat(TokenType tokenType, boolean checkType) throws Exception
    {
        if (this.token != null && this.token.Type == tokenType)
        {
            if(checkType && this.token.Type == TokenType.IDENTIFIER && this.token.SemanticType == SemanticTokenType.NULL){
                throw new Exception("Using undeclared variable '"+token.Value+"'on line " + line);
            }
            advance();
            return true;
        }
        throw new Exception("Expected token '"+ tokenType +"' not found. Found '"+token.Type+"' instead on line "+line+".");
    }

    public void program() throws Exception
    {
        eat(TokenType.PROGRAM, false);
        declList();
        stmtList(true);
        eat(TokenType.END, false);
        eat(TokenType.EOF, false);
    }

    private void declList() throws Exception
    {
        while (eatIf(TokenType.INT, false) || eatIf(TokenType.STRING, false)) // Type
        {
            SemanticTokenType type = lastToken.getSemanticTypeFromType();
            do
            {
                eat(TokenType.IDENTIFIER, false);
                if(lexicalAnalyzer.SymbolTable.get(lastToken.Value).SemanticType != SemanticTokenType.NULL) {
                    throw new Exception("Semantic Exception: " + lastToken.Value + " on line "+ line +" is already declared");
                }
                lastToken.SemanticType = type;
            } while (eatIf(TokenType.COMMA, false));
            eat(TokenType.SEMICOLON, false);
        }
    }

    private boolean stmtList(boolean throwsException) throws Exception
    {
        do
        {
            if (eatIf(TokenType.IDENTIFIER, true)) // Assign Stmt
            {
                SemanticTokenType tokenType = lastToken.SemanticType;
                eat(TokenType.ASSIGN, false);
                SemanticTokenType exprType = simpleExpr();
                if(tokenType != exprType) {
                    throw new Exception("Semantic Error: Expression ("+tokenType+") and Variable ("+exprType+") have incompatible type on line " + line);
                }
                eat(TokenType.SEMICOLON, false);
            }
            else if (eatIf(TokenType.IF, false)) // If stmt
            {
                expression();
                eat(TokenType.THEN, false);
                stmtList(true);
                if (eatIf(TokenType.ELSE, false))
                {
                    stmtList(true);
                }
                eat(TokenType.END, false);
            }
            else if (eatIf(TokenType.DO, false)) // Do while
            {
                stmtList(true);
                eat(TokenType.WHILE, false);
                expression();
                eat(TokenType.END, false);
            }
            else if (eatIf(TokenType.SCAN, false)) // scan();
            {
                eat(TokenType.PARENTHESES_OPEN, false);
                eat(TokenType.IDENTIFIER, true);
                eat(TokenType.PARENTHESES_CLOSE, false);
                eat(TokenType.SEMICOLON, false);
            }
            else if (eatIf(TokenType.PRINT, false)) // print();
            {
                eat(TokenType.PARENTHESES_OPEN, false);
                if (eatIf(TokenType.LITERAL, false)) { }
                else simpleExpr();
                eat(TokenType.PARENTHESES_CLOSE, false);
                eat(TokenType.SEMICOLON, false);
            }
            else if (throwsException) {
                throw new Exception("Unexpected token '"+token.Type+"' found on line "+line+".");
            }
            else {
                return false;
            }
        } while (stmtList(false));
        return false;
    }

    private SemanticTokenType expression() throws Exception
    {
        SemanticTokenType type = simpleExpr();
        if (eatIf(TokenType.EQUALS, false)
            || eatIf(TokenType.GREATER, false)
            || eatIf(TokenType.GREATERTHAN, false)
            || eatIf(TokenType.LESS, false)
            || eatIf(TokenType.LESSTHAN, false)
            || eatIf(TokenType.DIFFERENT, false))
        {
            simpleExpr();
        }
        return type;
    }

    private SemanticTokenType simpleExpr() throws Exception
    {
        SemanticTokenType type = factor();
        mulop();
        addop();
        return type;
    }

    private void mulop() throws Exception
    {
        if (eatIf(TokenType.MULTIPLICATION, false)
            || eatIf(TokenType.DIVISION, false)
            || eatIf(TokenType.AND, false))
        {
            factor();
            mulop();
        }
    }

    private void addop() throws Exception
    {
        if (eatIf(TokenType.ADDITION, false)
            || eatIf(TokenType.SUBTRACTION, false)
            || eatIf(TokenType.OR, false))
        {
            factor();
            mulop();
            addop();
        }
    }

    private SemanticTokenType factor() throws Exception
    {
        if (eatIf(TokenType.NOT, false) || eatIf(TokenType.SUBTRACTION, false)) { }
        if (eatIf(TokenType.IDENTIFIER, true)
            || eatIf(TokenType.INT_CONSTANT, false)
            || eatIf(TokenType.LITERAL, false))
        {
            if(lastToken.Type == TokenType.IDENTIFIER) return lastToken.SemanticType;
            else return lastToken.getSemanticTypeFromType();
        }
        if (eatIf(TokenType.PARENTHESES_OPEN, false))
        {
            SemanticTokenType type = expression();
            eat(TokenType.PARENTHESES_CLOSE, false);
            return type;
        }
        throw new Exception("Unexpected token found '"+token.Type+"' on line "+line+". Expected "+TokenType.IDENTIFIER+", "+TokenType.INT_CONSTANT+", "+TokenType.LITERAL+" or "+TokenType.PARENTHESES_OPEN);
    }
}