import java.io.FileNotFoundException;

public class Compiler {
    public static void main(String[] args) {
        LexicalAnalyzer Lexer;
        SyntacticalAnalyzer Syntactical;
        if (args.length == 0)
            System.err.println("No file to compile.");
        for (String arg : args) {
            try {
                Lexer = new LexicalAnalyzer(arg);
                Syntactical = new SyntacticalAnalyzer(Lexer);
                if (Lexer.Error || Syntactical.Error)
                    System.err.println("File " + arg + " not compiled.");
                else
                    System.out.println("File " + arg + " compiled with sucess.");
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("File " + arg + " not found.");
            } catch (Exception exception) {
                System.out.print("File " + arg + ": ");
                exception.printStackTrace();
            }
        }
    }
}   