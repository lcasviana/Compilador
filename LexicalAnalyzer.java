import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

class LexicalAnalyzer {
    private char ch;
    private int line = 1;
    private ArrayList<Token> tokens;
    private PushbackReader sourceFile;

    LexicalAnalyzer(PushbackReader sourceFile) {
        this.sourceFile = sourceFile;
        this.tokens = new ArrayList<>();
        this.tokens.add(new Token("program", TokenType.PROGRAM));
        this.tokens.add(new Token("end", TokenType.END));
        this.tokens.add(new Token("scan", TokenType.SCAN));
        this.tokens.add(new Token("print", TokenType.PRINT));
        this.tokens.add(new Token("int", TokenType.INT));
        this.tokens.add(new Token("string", TokenType.STRING));
        this.tokens.add(new Token("if", TokenType.IF));
        this.tokens.add(new Token("then", TokenType.THEN));
        this.tokens.add(new Token("else", TokenType.ELSE));
        this.tokens.add(new Token("do", TokenType.DO));
        this.tokens.add(new Token("while", TokenType.WHILE));
    }

    void Analyze() throws Exception {
        readch();
        while (this.ch != (char) -1) {
            Token token = GetToken();
            System.out.println(token);
            readch();
        }
    }
    
    private Token GetToken() throws Exception {
        // Ignore spaces and breaklines
        while (true) {
            if(ch == ' ' || ch == '\t' || ch == '\r'){
                readch();
            }else if(ch == '\n'){
                this.line++;
                readch();
            }else{
                break;
            }
        }

        // Ignore comments
        if (this.ch == '/') {
            if (this.readch('/')) { // Inline comments. Ex: // ...
                while (this.ch != '\n'){
                    this.readch();
                }
                this.line++;
                return this.GetToken();

            } else if(this.readch('*')) { // Comment block. Ex: /* ... */
                int startLine = this.line;
                while (this.ch != (char)-1) {
                    if(this.ch == '\n')
                        this.line++;
                    if(this.readch('*') && this.readch('/')){
                        return this.GetToken();
                    }
                    this.readch();
                }
                throw new Exception("Invalid token on line " + this.line + ". Comment block started on line " + startLine + " was not closed.");
            } else {
                return new Token("/");
            }
        }

        // Operators
        switch (this.ch) {
            case '=':
                if (this.readch('=')) {
                    return new Token(TokenType.EQUALS);
                } else {
                    return new Token(TokenType.ASSIGN);
                }
            case '>':
                if (this.readch('=')) {
                    return new Token(TokenType.GREATERTHAN);
                } else {
                    return new Token(TokenType.GREATER);
                }
            case '<':
                if (this.readch('=')) {
                    return new Token(TokenType.LESSTHAN);
                } else {
                    return new Token(TokenType.LESS);
                }
            case '!':
                if (this.readch('=')) {
                    return new Token(TokenType.DIFFERENT);
                } else {
                    throw new Exception("Invalid token on line " + this.line + ". Token started with '!'");
                }
            case '|':
                if (this.readch('|')) {
                    return new Token(TokenType.OR);
                } else {
                    throw new Exception("Invalid token on line " + this.line + ". Token started with '|'");
                }
            case '&':
                if (this.readch('&')) {
                    return new Token(TokenType.AND);
                } else {
                    throw new Exception("Invalid token on line " + this.line + ". Token started with '&'");
                }
            case ';':
            case '(':
            case ')':
            case '+':
            case '-':
            case '*':
            case ',':
                return new Token(String.valueOf(ch));
        }

        // Integer constants
        if (Character.isDigit(this.ch)) {
            int value = 0;
            do {
                value *= 10;
                value +=  Character.digit(this.ch,10);
                this.readch();
            } while (Character.isDigit(this.ch));
            this.sourceFile.unread(this.ch);
            this.ch = ' ';

            return new Num(value);
        }

        // Literal
        if (this.ch == '"') {
            StringBuilder literal = new StringBuilder();

            this.readch();
            while (this.ch != '\n' && this.ch != '"') {
                literal.append(this.ch);
                this.readch();
            }
            // Close literal
            if (this.ch == '"') {
                return new Token(literal.toString(), TokenType.LITERAL);
            } else {
                throw new Exception("Invalid token on line " + this.line + ". Literal not closed.");
            }
        }

        // Identifier
        if (Character.isLetter(this.ch)) {
            StringBuilder id = new StringBuilder();

            do {
                id.append(ch);
                this.readch();
            } while (Character.isLetterOrDigit(this.ch));
            this.sourceFile.unread(this.ch);
            this.ch = ' ';

            String finalId = id.toString();
            Optional<Token> opToken = tokens.stream().filter(t -> finalId.equals(t.value)).findFirst();
            Token token;
            if (opToken.isPresent()) { // Check if is on token list
                token = opToken.get();
            }else{
                token = new Token(id.toString(), TokenType.IDENTIFIER);
                this.tokens.add(token);
            }
            return token;
        }
        throw new Exception("Invalid token on line " + this.line + ". Undefined token started with '" + this.ch + "'");
    }

    private void readch() throws IOException {
        int read = sourceFile.read();
        this.ch = (char) read;
    }

    private boolean readch(char c) throws IOException {
        readch();
        if(this.ch == c){
            return true;
        }
        this.sourceFile.unread(this.ch);
        this.ch = ' ';
        return false;
    }
}