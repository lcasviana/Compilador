import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

class LexicalAnalyzer {
    private char Ch;
    private int Line = 1;
    
    private PushbackReader Stream;

    private Map<String, Token> SymbolTable;
    private Map<String, Token> ReservedWords;
    private ArrayList<Token> tokens;

    LexicalAnalyzer(FileReader file) throws Exception {
        Stream = new PushbackReader(file);
        InitializeReservedWords();
    }

    private void InitializeReservedWords() {
        ReservedWords.put("program", new Token("program", TokenType.PROGRAM));
        ReservedWords.put("end", new Token("end", TokenType.END));
        ReservedWords.put("scan", new Token("scan", TokenType.SCAN));
        ReservedWords.put("print", new Token("print", TokenType.PRINT));
        ReservedWords.put("int", new Token("int", TokenType.INT));
        ReservedWords.put("string", new Token("string", TokenType.STRING));
        ReservedWords.put("if", new Token("if", TokenType.IF));
        ReservedWords.put("then", new Token("then", TokenType.THEN));
        ReservedWords.put("else", new Token("else", TokenType.ELSE));
        ReservedWords.put("do", new Token("do", TokenType.DO));
        ReservedWords.put("while", new Token("while", TokenType.WHILE));
    }

    public void Analyze() throws Exception {
        ArrayList<Token> TokenStream = new ArrayList<>();
        Token token;
        while (Ch != (char) -1) {
            token = GetToken();
            if (token != null) {
                TokenStream.add(token);
                System.out.println(token);
            }
        }
        Stream.close();
        // TODO: Return some shit '3'
    }
    
    private Token GetToken() throws Exception {
        ReadChar();
        
        // Spaces and breaklines
        while (Ch == ' ' || Ch == '\t' || Ch == '\r' || Ch == '\n') {
            if (Ch == '\n')
                Line++;
            ReadChar();
        }

        // Comments and division
        if (Ch == '/') {
            if (ReadChar('/')) { // Inline comments. Ex: // ...
                while (Ch != '\n')
                    ReadChar();
                Line++;
                ReadChar();
                return this.GetToken();
            } else if (ReadChar('*')) { // Comment block. Ex: /* ... */
                int startLine = Line;
                while (Ch != (char) -1) {
                    if (Ch == '\n')
                        Line++;
                    if (ReadChar('*') && ReadChar('/'))
                        return this.GetToken();
                    ReadChar();
                }
                throw new Exception("Invalid token on line " + Line + ". Comment block started on line " + startLine + " was not closed.");
            } else
                return new Token(TokenType.DIVISION);
        }

        // Operators
        switch (Ch) {
            case '=':
                if (ReadChar('='))
                    return new Token(TokenType.EQUALS);
                else
                    return new Token(TokenType.ASSIGN);
            case '>':
                if (ReadChar('='))
                    return new Token(TokenType.GREATERTHAN);
                else
                    return new Token(TokenType.GREATER);
            case '<':
                if (ReadChar('='))
                    return new Token(TokenType.LESSTHAN);
                else
                    return new Token(TokenType.LESS);
            case '!':
                if (ReadChar('='))
                    return new Token(TokenType.DIFFERENT);
                else
                    return new Token(TokenType.NOT);
            case '|':
                if (ReadChar('|'))
                    return new Token(TokenType.OR);
                else
                    throw new Exception("Invalid token on line " + Line + ". Token started with '|'");
            case '&':
                if (ReadChar('&'))
                    return new Token(TokenType.AND);
                else
                    throw new Exception("Invalid token on line " + Line + ". Token started with '&'");
            case ';':
                return new Token(TokenType.SEMICOLON);
            case '(':
                return new Token(TokenType.PARENTHESES_OPEN);
            case ')':
                return new Token(TokenType.PARENTHESES_CLOSE);
            case '+':
                return new Token(TokenType.ADDITION);
            case '-':
                return new Token(TokenType.SUBTRACTION);
            case '*':
                return new Token(TokenType.MULTIPLICATION);
            case ',':
                return new Token(TokenType.COMMA);
        }

        // Integer constants
        if (Character.isDigit(Ch)) {
            int Value = 0;
            do {
                Value *= 10;
                Value += Character.digit(Ch, 10);
            } while (ReadCharNumber());
            return new Number(Value);
        }

        // Literal
        if (Ch == '"') {
            StringBuilder Literal = new StringBuilder();
            ReadChar();
            while (Ch != '\n' && Ch != '"') {
                Literal.append(Ch);
                ReadChar();
            }
            if (Ch == '"')
                return new Token(Literal.toString(), TokenType.LITERAL);
            else
                throw new Exception("Invalid token on line " + Line + ". Literal not closed.");
        }

        // Identifier
        if (Character.isLetter(Ch)) {
            StringBuilder Id = new StringBuilder();
            do {
                Id.append(Ch);
            } while (ReadCharLetter());
            Token Reserved = ReservedWords.get(Id.toString());
            Token Symbol = SymbolTable.get(Id.toString());
            if (Reserved != null)
                return Reserved;
            else if (Symbol == null)
                SymbolTable.put(Id.toString(), Symbol = new Identifier(SymbolTable.size(), Id.toString()));
            return Symbol;
        }

        if (Ch == (char) -1)
            return null;
        else
            throw new Exception("Invalid token on line " + Line + ". Undefined token started with '" + Ch + "'");
    }

    private void ReadChar() throws IOException {
        Ch = (char) Stream.read();
    }

    private boolean ReadChar(char ch) throws IOException {
        ReadChar();
        if (Ch == ch)
            return true;
        Stream.unread(Ch);
        return false;
    }

    private boolean ReadCharNumber() throws IOException {
        ReadChar();
        if (Character.isDigit(Ch))
            return true;
        Stream.unread(Ch);
        return false;
    }

    private boolean ReadCharLetter() throws IOException {
        ReadChar();
        if (Character.isLetter(Ch))
            return true;
        Stream.unread(Ch);
        return false;
    }
}