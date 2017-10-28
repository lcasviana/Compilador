using System;
using System.Collections.Generic;
using System.IO;

class LexicalAnalyzer {
    private char Ch;
    public int Line = 1;
    
    private StreamReader Stream;

    private List<Token> SymbolTable = new List<Token>();
    private List<Token> ReservedWords = new List<Token>(new Token[] {
        new Token("program", TokenType.PROGRAM),
        new Token("end", TokenType.END),
        new Token("scan", TokenType.SCAN),
        new Token("print", TokenType.PRINT),
        new Token("int", TokenType.INT),
        new Token("string", TokenType.STRING),
        new Token("if", TokenType.IF),
        new Token("then", TokenType.THEN),
        new Token("else", TokenType.ELSE),
        new Token("do", TokenType.DO),
        new Token("while", TokenType.WHILE)
    });

    public LexicalAnalyzer(string File) {
        Stream = new StreamReader(File);
    }

    public (List<Token>, List<Token>) Analyze() {
        List<Token> TokenStream = new List<Token>();
        Token token;
        while (!Stream.EndOfStream) {
            token = GetToken();
            if (token != null) {
                TokenStream.Add(token);
                System.Console.WriteLine(token);
            }
        }
        Stream.Close();
        return (TokenStream, SymbolTable);
    }

    public Token GetToken() {
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
                return GetToken();
            } else if (ReadChar('*')) { // Comment block. Ex: /* ... */
                int startLine = Line;
                while (!Stream.EndOfStream) {
                    if (Ch == '\n')
                        Line++;
                    if (ReadChar('*') && ReadChar('/'))
                        return GetToken();
                    ReadChar();
                }
                throw new Exception($"Invalid token on line {Line}. Comment block started on line {startLine} was not closed.");
            } else {
                return new Token(TokenType.DIVISION);
            }
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
                    return new Token(TokenType.EXCLAMATION);
            case '|':
                if (ReadChar('|'))
                    return new Token(TokenType.OR);
                else
                    throw new Exception($"Invalid token on line {Line}. Token started with '|'");
            case '&':
                if (ReadChar('&'))
                    return new Token(TokenType.AND);
                else
                    throw new Exception($"Invalid token on line {Line}. Token started with '&'");
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
        if (Char.IsDigit(Ch)) {
            int Value = 0;
            do {
                Value *= 10;
                Value += (int) Char.GetNumericValue(Ch);
            } while (ReadCharNumber());
            return new Number(Value);
        }

        // Literal
        if (Ch == '"') {
            string Literal = "";
            ReadChar();
            while (Ch != '\n' && Ch != '"') {
                Literal += Ch;
                ReadChar();
            }
            if (Ch == '"')
                return new Token(Literal, TokenType.LITERAL);
            else
                throw new Exception($"Invalid token on line {Line}. Literal not closed.");
        }

        // Identifier
        if (Char.IsLetter(Ch)) {
            string Id = "";
            do {
                Id += Ch;
            } while (ReadCharLetter());
            Token Reserved = ReservedWords.Find(t => t.Value == Id);
            Token Symbol = SymbolTable.Find(s => s.Value == Id);
            if (Reserved != null) // Check ReservedWords
                return Reserved; 
            else if (Symbol == null)
                SymbolTable.Add(Symbol = new Identifier(SymbolTable.Count, Id));
            return Symbol;
        }

        if (!Stream.EndOfStream)
            throw new Exception($"Invalid token on line {Line}. Undefined token started with '{Ch}'");
        else
            return null;
    }

    private void ReadChar() {
        Ch = (char) Stream.Read();
    }

    private bool ReadChar(char Ch) {
        if ((char) Stream.Peek() == Ch) {
            ReadChar();
            return true;
        }
        return false;
    }

    private bool ReadCharNumber() {
        if (Char.IsDigit((char) Stream.Peek())) {
            ReadChar();
            return true;
        }
        return false;
    }

    private bool ReadCharLetter() {
        if (Char.IsLetter((char) Stream.Peek())) {
            ReadChar();
            return true;
        }
        return false;
    }
}