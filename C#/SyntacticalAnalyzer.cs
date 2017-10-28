using System;

namespace Compilador
{
    class SyntacticallAnalyzer
    {
        private readonly LexicalAnalyzer lexicalAnalyzer;
        private Token token;
        private int line;

        public SyntacticallAnalyzer(LexicalAnalyzer lexicalAnalyzer)
        {
            this.lexicalAnalyzer = lexicalAnalyzer;
            token = lexicalAnalyzer.GetToken();
            line = 1;
        }

        private void advance()
        {
            token = lexicalAnalyzer.GetToken();
            line = lexicalAnalyzer.Line;
        }

        private bool eat(TokenType tokenType, bool throwsException = true)
        {
            if (token?.Type == tokenType)
            {
                advance();
                return true;
            }
            if (throwsException) throw new Exception($"Expected token '{tokenType}' not found. Found '{token?.Type}' instead on line {line}.");
            return false;
        }

        public void program()
        {
            eat(TokenType.PROGRAM);
            declList();
            stmtList();
            eat(TokenType.END);
        }

        private void declList()
        {
            while (eat(TokenType.INT, throwsException: false) || eat(TokenType.STRING, throwsException: false)) // Type
            {
                do
                {
                    eat(TokenType.IDENTIFIER);
                } while (eat(TokenType.COMMA, throwsException: false));
                eat(TokenType.SEMICOLON);
            }
        }

        private bool stmtList(bool throwsException = true)
        {
            do
            {
                if (eat(TokenType.IDENTIFIER, throwsException: false)) // Assign Stmt
                {
                    eat(TokenType.ASSIGN);
                    simpleExpr();
                    eat(TokenType.SEMICOLON);
                }
                else if (eat(TokenType.IF, throwsException: false)) // If stmt
                {
                    expression();
                    eat(TokenType.THEN);
                    stmtList();
                    if (eat(TokenType.ELSE, throwsException: false))
                    {
                        stmtList();
                    }
                    eat(TokenType.END);
                }
                else if (eat(TokenType.DO, throwsException: false)) // Do while
                {
                    stmtList();
                    eat(TokenType.WHILE);
                    expression();
                    eat(TokenType.END);
                }
                else if (eat(TokenType.SCAN, throwsException: false)) // scan();
                {
                    eat(TokenType.PARENTHESES_OPEN);
                    eat(TokenType.IDENTIFIER);
                    eat(TokenType.PARENTHESES_CLOSE);
                    eat(TokenType.SEMICOLON);
                }
                else if (eat(TokenType.PRINT, throwsException: false)) // print();
                {
                    eat(TokenType.PARENTHESES_OPEN);
                    if (eat(TokenType.LITERAL, throwsException: false)) { }
                    else simpleExpr();
                    eat(TokenType.PARENTHESES_CLOSE);
                    eat(TokenType.SEMICOLON);
                }
                else if (throwsException)
                {
                    throw new Exception($"Unexpected token '{token.Type}' found on line {line}.");
                }
                else
                {
                    return false;
                }
            } while (stmtList(throwsException: false));
            return false;
        }

        private void expression()
        {
            simpleExpr();
            if (eat(TokenType.EQUALS, throwsException: false)
                || eat(TokenType.GREATER, throwsException: false)
                || eat(TokenType.GREATERTHAN, throwsException: false)
                || eat(TokenType.LESS, throwsException: false)
                || eat(TokenType.LESSTHAN, throwsException: false)
                || eat(TokenType.DIFFERENT, throwsException: false))
            {
                simpleExpr();
            }
        }

        private void simpleExpr()
        {
            if (eat(TokenType.EXCLAMATION, throwsException: false) || eat(TokenType.SUBTRACTION, throwsException: false)) { }
            factor();
            mulop();
            addop();
        }

        private void mulop()
        {
            if (eat(TokenType.MULTIPLICATION, throwsException: false)
                || eat(TokenType.DIVISION, throwsException: false)
                || eat(TokenType.AND, throwsException: false))
            {
                factor();
                mulop();
            }
        }

        private void addop()
        {
            if (eat(TokenType.ADDITION, throwsException: false)
                || eat(TokenType.SUBTRACTION, throwsException: false)
                || eat(TokenType.OR, throwsException: false))
            {
                factor();
                mulop();
                addop();
            }
        }

        private void factor()
        {
            if (eat(TokenType.IDENTIFIER, throwsException: false)
                || eat(TokenType.INT_CONSTANT, throwsException: false)
                || eat(TokenType.LITERAL, throwsException: false))
            {
                return;
            }
            if (eat(TokenType.PARENTHESES_OPEN, throwsException: false))
            {
                expression();
                eat(TokenType.PARENTHESES_CLOSE);
                return;
            }
            throw new Exception($"Unexpected token found '{token.Type}' on line {line}. Expected {TokenType.IDENTIFIER}, {TokenType.INT_CONSTANT}, {TokenType.LITERAL} or {TokenType.PARENTHESES_OPEN}");
        }
    }
}