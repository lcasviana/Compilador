import java.io.*;

class Main {
    public static void main(String[] args) {
        BufferedReader sourceFile;
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        try {
            sourceFile = new BufferedReader(new FileReader(args[0]));
            lexer.Analyze(sourceFile);
            sourceFile.close();
        } catch (Exception e) {

        }
    }
}