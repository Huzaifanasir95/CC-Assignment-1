package Assig1;
import java.io.*;

class Token {
    String type;    // e.g., KEYWORD, IDENTIFIER, NUMBER, OPERATOR, LBRACE, RBRACE, SEMICOLON, etc.
    String lexeme;  // The actual string value
    int line;       // Line number where the token was found

    public Token(String type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + ":" + lexeme + " (Line " + line + ")";
    }
}
