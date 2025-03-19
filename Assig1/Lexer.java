package Assig1;

import java.util.HashSet;
import java.util.Set;
import java.util.*;
class Lexer {
    private String input;
    private int pos = 0;
    private int line = 1;
    private final ErrorHandler errorHandler;
    // DFAs for identifiers, numbers, and operators.
    final Automaton identifierDFA = new Automaton();
    final Automaton numberDFA = new Automaton();
    final Automaton operatorDFA = new Automaton();

    final RegularExpression identifierRE = new RegularExpression("Identifier", "[a-z]+[a-z0-9_]*");
    final RegularExpression numberRE = new RegularExpression("Number", "digit+ ('.' digit{1,5})? ([Ee][+-]? digit+)?");
    final RegularExpression operatorRE = new RegularExpression("Operator", "[+\\-*/%<>=!]|==|<=|>=|!=");

    final NFA identifierNFA = new NFA();
    final NFA numberNFA = new NFA();
    final NFA operatorNFA = new NFA();
    
    // Reserved keywords.
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "if", "else", "while", "for",
        "int", "decimal", "bool", "char", "string",
        "true", "false", "input", "output"
    ));

    public Lexer(String input, ErrorHandler errorHandler) {
        this.input = input;
        this.errorHandler = errorHandler;
        initializeDFAs();
    }

    private void initializeDFAs() {
        // Identifier DFA: accepts one or more lowercase letters.
    	initializeIdentifierNFA();
        initializeNumberNFA();
        initializeOperatorNFA();
        identifierDFA.setStartState("q0");
        identifierDFA.addState("q0", false);
        identifierDFA.addState("q1", true);
        for (char c = 'a'; c <= 'z'; c++) {
            identifierDFA.addTransition("q0", c, "q1");
            identifierDFA.addTransition("q1", c, "q1");
        }

        // Number DFA: supports integer, decimal (up to 5 decimal places), and exponent notation.
        numberDFA.setStartState("q0");
        numberDFA.addState("q0", false);
        numberDFA.addState("q1", true);  // integer part
        numberDFA.addState("q2", false); // decimal point seen
        numberDFA.addState("q3", true);  // fractional part
        numberDFA.addState("q4", false); // exponent symbol seen
        numberDFA.addState("q5", false); // exponent sign seen
        numberDFA.addState("q6", true);  // exponent digits

        for (char c = '0'; c <= '9'; c++) {
            numberDFA.addTransition("q0", c, "q1");
            numberDFA.addTransition("q1", c, "q1");
            numberDFA.addTransition("q2", c, "q3");
            numberDFA.addTransition("q3", c, "q3");
            numberDFA.addTransition("q4", c, "q6");
            numberDFA.addTransition("q5", c, "q6");
            numberDFA.addTransition("q6", c, "q6");
        }
        numberDFA.addTransition("q1", '.', "q2");
        numberDFA.addTransition("q1", 'E', "q4");
        numberDFA.addTransition("q3", 'E', "q4");
        numberDFA.addTransition("q4", '+', "q5");
        numberDFA.addTransition("q4", '-', "q5");

        // Operator DFA: supports +, -, *, /, %, and two-character operators.
        operatorDFA.setStartState("q0");
        operatorDFA.addState("q0", false);
        operatorDFA.addState("q1", true);
        operatorDFA.addState("q2", true);
        for (char op : new char[]{'+', '-', '*', '/', '%', '<', '>', '=', '!'}) {
            operatorDFA.addTransition("q0", op, "q1");
        }
        operatorDFA.addTransition("q1", '=', "q2");
    }

    
    // Initialize Identifier NFA
    private void initializeIdentifierNFA() {
        identifierNFA.setStartState("q0");
        identifierNFA.addState("q0", false);
        identifierNFA.addState("q1", true);
        for (char c = 'a'; c <= 'z'; c++) {
            identifierNFA.addTransition("q0", c, "q1");
            identifierNFA.addTransition("q1", c, "q1");
        }
    }

    // Initialize Number NFA (with epsilon transitions)
    private void initializeNumberNFA() {
        numberNFA.setStartState("q0");
        numberNFA.addState("q0", false);
        numberNFA.addState("q1", true);  // Integer part
        numberNFA.addState("q2", false); // Decimal point
        numberNFA.addState("q3", true);  // Fractional part
        numberNFA.addState("q4", false); // Exponent symbol
        numberNFA.addState("q5", false); // Exponent sign
        numberNFA.addState("q6", true);  // Exponent digits

        // Transitions
        for (char c = '0'; c <= '9'; c++) {
            numberNFA.addTransition("q0", c, "q1");
            numberNFA.addTransition("q1", c, "q1");
            numberNFA.addTransition("q2", c, "q3");
            numberNFA.addTransition("q3", c, "q3");
            numberNFA.addTransition("q4", c, "q6");
            numberNFA.addTransition("q5", c, "q6");
            numberNFA.addTransition("q6", c, "q6");
        }
        numberNFA.addEpsilonTransition("q1", "q2"); // Optional decimal
        numberNFA.addEpsilonTransition("q1", "q4"); // Optional exponent
        numberNFA.addTransition("q2", '.', "q2");
        numberNFA.addTransition("q1", 'E', "q4");
        numberNFA.addTransition("q3", 'E', "q4");
        numberNFA.addTransition("q4", '+', "q5");
        numberNFA.addTransition("q4", '-', "q5");
    }

    // Initialize Operator NFA
    private void initializeOperatorNFA() {
        operatorNFA.setStartState("q0");
        operatorNFA.addState("q0", false);
        operatorNFA.addState("q1", true);
        operatorNFA.addState("q2", true);

        for (char op : new char[]{'+', '-', '*', '/', '%', '<', '>', '=', '!'}) {
            operatorNFA.addTransition("q0", op, "q1");
        }
        operatorNFA.addEpsilonTransition("q1", "q2");
        operatorNFA.addTransition("q1", '=', "q2");
    }

    // Returns a list of Token objects.
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char currentChar = input.charAt(pos);

            // Skip whitespace.
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    line++;
                }
                pos++;
                continue;
            }

            // Scope punctuation.
            if (currentChar == '{') {
                tokens.add(new Token("LBRACE", "{", line));
                pos++;
                continue;
            }
            if (currentChar == '}') {
                tokens.add(new Token("RBRACE", "}", line));
                pos++;
                continue;
            }
            if (currentChar == ';') {
                tokens.add(new Token("SEMICOLON", ";", line));
                pos++;
                continue;
            }

            // Comments.
            if (currentChar == '/') {
                if (pos + 1 < input.length()) {
                    char nextChar = input.charAt(pos + 1);
                    if (nextChar == '/' || nextChar == '*') {
                        handleComments();
                        continue;
                    }
                }
            }

            // String literal.
            if (currentChar == '"') {
                String strLiteral = readStringLiteral();
                tokens.add(new Token("STRING", strLiteral, line));
                continue;
            }

            // Character literal.
            if (currentChar == '\'') {
                String charLiteral = readCharLiteral();
                tokens.add(new Token("CHAR", charLiteral, line));
                continue;
            }

            // Identifiers and keywords.
            if (Character.isLetter(currentChar)) {
                int len = identifierDFA.getLongestAcceptedLength(input, pos);
                if (len > 0) {
                    String lexeme = input.substring(pos, pos + len);
                    int tokenLine = line;
                    pos += len;
                    if (KEYWORDS.contains(lexeme)) {
                        tokens.add(new Token("KEYWORD", lexeme, tokenLine));
                    } else {
                        tokens.add(new Token("IDENTIFIER", lexeme, tokenLine));
                    }
                    continue;
                }
            }

            // Numbers.
            if (Character.isDigit(currentChar)) {
                int len = numberDFA.getLongestAcceptedLength(input, pos);
                if (len > 0) {
                    String lexeme = input.substring(pos, pos + len);
                    int tokenLine = line;
                    pos += len;
                    validateNumber(lexeme);
                    tokens.add(new Token("NUMBER", lexeme, tokenLine));
                    continue;
                }
            }

            // Operators.
            if ("=<>!+-*/%^".indexOf(currentChar) != -1) {
                int len = operatorDFA.getLongestAcceptedLength(input, pos);
                if (len > 0) {
                    String lexeme = input.substring(pos, pos + len);
                    int tokenLine = line;
                    pos += len;
                    tokens.add(new Token("OPERATOR", lexeme, tokenLine));
                    continue;
                }
            }

            errorHandler.reportError(line, "Invalid character: '" + currentChar + "'");
            pos++;
        }
        tokens.add(new Token("EOF", "", line));
        System.out.println("Total tokens: " + tokens.size());
        return tokens;
    }

    private void validateNumber(String value) {
        if (value.contains(".")) {
            String[] parts = value.split("\\.");
            if (parts.length > 1 && parts[1].length() > 5) {
                errorHandler.reportError(line, "Decimal exceeds 5 places: " + value);
            }
        }
        if (value.toUpperCase().contains("E") && !value.matches(".*E[+-]?\\d+")) {
            errorHandler.reportError(line, "Invalid exponent: " + value);
        }
    }

    private void handleComments() {
        if (pos + 1 >= input.length()) {
            pos++;
            return;
        }
        char nextChar = input.charAt(pos + 1);
        // Single-line comment.
        if (nextChar == '/') {
            pos += 2;
            while (pos < input.length() && input.charAt(pos) != '\n') {
                pos++;
            }
        }
        // Multi-line comment.
        else if (nextChar == '*') {
            pos += 2;
            int commentStartLine = line;
            boolean closed = false;
            while (pos < input.length() - 1) {
                if (input.charAt(pos) == '\n') {
                    line++;
                }
                if (input.charAt(pos) == '*' && input.charAt(pos + 1) == '/') {
                    pos += 2;
                    closed = true;
                    break;
                }
                pos++;
            }
            if (!closed) {
                errorHandler.reportError(commentStartLine, "Unclosed multi-line comment");
            }
        }
    }

    private String readStringLiteral() {
        StringBuilder sb = new StringBuilder();
        pos++; // Skip opening quote.
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '"') {
                pos++; // Skip closing quote.
                return sb.toString();
            }
            if (c == '\\') {
                pos++;
                if (pos < input.length()) {
                    char next = input.charAt(pos);
                    switch (next) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        default: sb.append(next); break;
                    }
                }
            } else {
                sb.append(c);
            }
            pos++;
        }
        errorHandler.reportError(line, "Unclosed string literal");
        return sb.toString();
    }

    private String readCharLiteral() {
        StringBuilder sb = new StringBuilder();
        pos++; // Skip opening apostrophe.
        if (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '\\') {
                pos++;
                if (pos < input.length()) {
                    char next = input.charAt(pos);
                    switch (next) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        default: sb.append(next); break;
                    }
                    pos++;
                }
            } else {
                sb.append(c);
                pos++;
            }
        }
        if (pos < input.length() && input.charAt(pos) == '\'') {
            pos++; // Skip closing apostrophe.
        } else {
            errorHandler.reportError(line, "Unclosed character literal");
        }
        return sb.toString();
    }
}
