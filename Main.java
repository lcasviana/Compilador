import java.io.*;

class Main {
    public static void main(String[] args) {
        PushbackReader sourceFile;
        LexicalAnalyzer lexer;
        try {
            sourceFile = new PushbackReader(new FileReader(args[0]));
            lexer = new LexicalAnalyzer(sourceFile);
            lexer.Analyze();
            sourceFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   