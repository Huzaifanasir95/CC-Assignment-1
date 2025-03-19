package Assig1;

import java.util.*;

class Parser {
    private List<Token> tokens;
    private int index = 0;
    private final SymbolTable symbolTable;
    private final ErrorHandler errorHandler;

    public Parser(List<Token> tokens, SymbolTable symbolTable, ErrorHandler errorHandler) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
        this.errorHandler = errorHandler;
    }

    private Token currentToken() {
        return tokens.get(index);
    }

    private void nextToken() {
        index++;
    }

    public void parse() {
        while (index < tokens.size() && !currentToken().type.equals("EOF")) {
            parseStatement();
        }
    }

    private void parseStatement() {
        Token token = currentToken();
        if (token.type.equals("LBRACE")) {
            symbolTable.enterScope();
            nextToken(); // consume LBRACE
            while (index < tokens.size() &&
                   !currentToken().type.equals("RBRACE") &&
                   !currentToken().type.equals("EOF")) {
                parseStatement();
            }
            if (index < tokens.size() && currentToken().type.equals("RBRACE")) {
                nextToken(); // consume RBRACE
                symbolTable.exitScope();
            } else {
                errorHandler.reportError(token.line, "Missing closing brace");
            }
        }
        // Declaration: a type keyword (int, decimal, bool, char, string)
        else if (token.type.equals("KEYWORD") && isTypeKeyword(token.lexeme)) {
            String typeKeyword = token.lexeme;
            int declLine = token.line;
            nextToken(); // consume type keyword
            if (index < tokens.size() && currentToken().type.equals("IDENTIFIER")) {
                Token identToken = currentToken();
                String ident = identToken.lexeme;
                if (ident.length() != 1) {
                    errorHandler.reportError(identToken.line,
                        "Invalid identifier '" + ident + "'. Must be a single lowercase letter.");
                }
                if (symbolTable.existsInCurrentScope(ident)) {
                    errorHandler.reportError(identToken.line,
                        "Variable '" + ident + "' already declared in this scope.");
                } else {
                    String scopeLabel = (symbolTable.currentScopeDepth() == 1) ? "global" : "local";
                    symbolTable.addSymbol(ident, typeKeyword + "-" + scopeLabel);
                }
                nextToken(); // consume identifier
                if (index < tokens.size() && currentToken().type.equals("OPERATOR")
                    && currentToken().lexeme.equals("=")) {
                    nextToken(); // consume '='
                } else {
                    errorHandler.reportError(declLine,
                        "Expected '=' in declaration for variable " + ident);
                }
                // Process the initialization expression.
                while (index < tokens.size() &&
                       !currentToken().type.equals("SEMICOLON") &&
                       !currentToken().type.equals("EOF")) {
                    Token exprToken = currentToken();
                    if (exprToken.type.equals("IDENTIFIER")) {
                        if (!symbolTable.lookup(exprToken.lexeme)) {
                            errorHandler.reportError(exprToken.line,
                                "Variable '" + exprToken.lexeme + "' used in initialization is not declared.");
                        }
                    }
                    nextToken();
                }
                if (index < tokens.size() && currentToken().type.equals("SEMICOLON")) {
                    nextToken(); // consume semicolon
                } else {
                    errorHandler.reportError(declLine,
                        "Missing semicolon after declaration of " + ident);
                }
            } else {
                errorHandler.reportError(declLine, "Expected identifier after type keyword " + typeKeyword);
            }
        }
        // Assignment statement.
        else if (token.type.equals("IDENTIFIER")) {
            int assignLine = token.line;
            String ident = token.lexeme;
            if (!symbolTable.lookup(ident)) {
                errorHandler.reportError(token.line, "Variable '" + ident + "' is not declared.");
            }
            nextToken(); // consume identifier
            if (index < tokens.size() && currentToken().type.equals("OPERATOR")
                && currentToken().lexeme.equals("=")) {
                nextToken(); // consume '='
            } else {
                errorHandler.reportError(assignLine, "Expected '=' in assignment for variable " + ident);
            }
            // Process right-hand side expression.
            while (index < tokens.size() &&
                   !currentToken().type.equals("SEMICOLON") &&
                   !currentToken().type.equals("EOF")) {
                Token exprToken = currentToken();
                if (exprToken.type.equals("IDENTIFIER")) {
                    if (!symbolTable.lookup(exprToken.lexeme)) {
                        errorHandler.reportError(exprToken.line,
                            "Variable '" + exprToken.lexeme + "' is not declared.");
                    }
                }
                nextToken();
            }
            if (index < tokens.size() && currentToken().type.equals("SEMICOLON")) {
                nextToken(); // consume semicolon
            } else {
                errorHandler.reportError(assignLine, "Missing semicolon in assignment for variable " + ident);
            }
        }
        // For any other tokens (like SEMICOLON), just consume.
        else {
            nextToken();
        }
    }

    private boolean isTypeKeyword(String keyword) {
        return keyword.equals("int") || keyword.equals("decimal") || keyword.equals("bool") ||
               keyword.equals("char") || keyword.equals("string");
    }
}
