import java.io.*;

class LexicalAnalyzer {
    int read;
    int line = 1, colune = 1;

    void Analyze(BufferedReader sourceFile) throws Exception {
        read = sourceFile.read();
        while (read != -1) {
            Token token = GetToken(sourceFile);
            System.out.println(token.value);
        }
    }
    
    Token GetToken(BufferedReader sourceFile) throws Exception {
        Token token;
        while (true) {
            if ((char) read == ' ') {
                colune += 1;
                read = sourceFile.read();
            } else if ((char) read == '\t') {
                colune += 4;
                read = sourceFile.read();
            } else if ((char) read == '\n') {
                line += 1;
                colune = 1;
                read = sourceFile.read();
            } else {
                colune += 1;
                token = new Token("" + (char) read, TokenType.IDENTIFIER);
                read = sourceFile.read();
                break;
            }
        }
        return token;
    }

    boolean IsLetter() {
        char c = (char) read;
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    boolean IsDigit() {
        char c = (char) read;
        return c >= '0' && c <= '9';
    }

    boolean IsCharacter() {
        char c = (char) read;
        return c >= 0 && c <= 255 && c != '“' && c != '”';
    }
}