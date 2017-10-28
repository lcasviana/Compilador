using System;
using System.IO;

namespace Compilador
{
    class Program
    {
        static void Main(string[] args)
        {
            LexicalAnalyzer lexer;
            SyntacticallAnalyzer syntaxer;
            if (args.Length == 0)
                System.Console.WriteLine("Nenhum arquivo informado.");
            foreach (var arg in args)
            {
                if (File.Exists(arg))
                {
                    lexer = new LexicalAnalyzer(arg);
                    syntaxer = new SyntacticallAnalyzer(lexer);
                    try
                    {
                        syntaxer.program();
                        System.Console.WriteLine($"Program {arg} compiled successfully.");
                    }
                    catch (Exception e)
                    {
                        System.Console.WriteLine($"Program {arg}: {e.ToString()}");
                    }
                    System.Console.WriteLine();
                }
                else
                    Console.WriteLine($"Arquivo {arg} não existe.");
            }
        }
    }
}