
---

# Assignment 1: Simple Compiler Frontend

## Course Information
- **Course**: Compiler Construction
- **Section**: CS-A
- **Authors**:
  - **Huzaifa Nasir** (Roll No: 22i-1053)
  - **Maaz Ali** (Roll No: 22i-1042)

## Overview

This project implements a basic compiler frontend in Java, designed to process a simplified programming language. The compiler performs lexical analysis, syntactic analysis, symbol table management, and error reporting. It reads source code from a file (`input.redblack`), tokenizes it using Deterministic Finite Automata (DFAs), parses the tokens to enforce syntax rules, and tracks variable declarations across global and local scopes. The implementation also includes Nondeterministic Finite Automata (NFAs) for demonstration purposes and provides detailed output, including transition tables, tokens, symbol tables, and errors.

---

## Objectives

- Demonstrate lexical analysis using DFAs to tokenize input.
- Implement syntactic analysis to check grammar and manage scopes.
- Build a symbol table to track variable declarations.
- Handle errors with line numbers for lexical and syntactic issues.
- Visualize DFA and NFA transition tables for educational purposes.

---

## Project Structure

- **Package**: `A1`
- **Main Class**: `Compiler.java`
- **Supporting Classes**:
  - **`Token`**: Represents a token with type, lexeme, and line number.
  - **`Automaton`**: Implements a DFA for token recognition.
  - **`NFA`**: Extends `Automaton` to support epsilon transitions (demonstration only).
  - **`RegularExpression`**: Documents token patterns.
  - **`Lexer`**: Performs lexical analysis.
  - **`SymbolTable`**: Manages variable scopes and declarations.
  - **`ErrorHandler`**: Collects and reports errors.
  - **`Parser`**: Performs syntactic analysis and builds the symbol table.
- **Input File**: `input.redblack` (located at `D:\SEM6\CC\Assignemnt1_1\Compiler\src\A1\`)

---

## Language Specification

### Supported Constructs
- **Identifiers**: Single lowercase letters (e.g., `x`, `y`).
- **Types**: `int`, `decimal`, `bool`, `char`, `string`.
- **Numbers**: Integers (e.g., `123`), decimals (e.g., `12.34`, max 5 decimal places), exponents (e.g., `1.2E+3`).
- **Operators**: `+`, `-`, `*`, `/`, `%`, `<`, `>`, `=`, `!`, `==`, `<=`, `>=`, `!=`.
- **Keywords**: `if`, `else`, `while`, `for`, `int`, `decimal`, `bool`, `char`, `string`, `true`, `false`, `input`, `output`.
- **Syntax**:
  - **Declaration**: `type identifier = expression ;` (e.g., `int x = 5;`)
  - **Assignment**: `identifier = expression ;` (e.g., `x = 10;`)
  - **Scope**: `{ statements }` (nested blocks).
  - **Comments**: Single-line (`//`) and multi-line (`/* */`).
  - **Literals**: Strings (e.g., `"hello"`) and characters (e.g., `'a'`), with escape sequences (`\n`, `\t`, `\r`).

### Example Input
```
int x = 5;
{
    int y = x + 3;
}
x = 10;
```

---

## Working of the Code

### 1. **Input Reading**
- **Class**: `Compiler`
- **Function**: `readFile(String path)`
- **Process**:
  - Reads the source code from `input.redblack` into a string using a `BufferedReader`.
  - Appends each line with a newline (`\n`) to preserve line numbering.
- **Output**: A single string containing the entire source code (e.g., `"int x = 5;\n{\n    int y = x + 3;\n}\nx = 10;\n"`).
- **Where**: Called at the start of `Compiler.main`.

### 2. **Lexical Analysis**
- **Class**: `Lexer`
- **Main Function**: `tokenize()`
- **Process**:
  - **Initialization**: 
    - `Lexer` constructor calls `initializeDFAs()` to set up DFAs for identifiers, numbers, and operators.
    - Also initializes NFAs (`initializeIdentifierNFA()`, `initializeNumberNFA()`, `initializeOperatorNFA()`) for demonstration.
  - **Tokenization**:
    - Scans the input character by character using a position pointer (`pos`).
    - Skips whitespace, incrementing `line` for newlines.
    - Recognizes special tokens directly:
      - `{` → `LBRACE`
      - `}` → `RBRACE`
      - `;` → `SEMICOLON`
    - Handles comments via `handleComments()`:
      - `//`: Skips to the next newline.
      - `/* */`: Skips until `*/` or reports an unclosed comment error.
    - Processes literals:
      - `readStringLiteral()`: Reads strings (e.g., `"hello\nworld"`), handling escape sequences.
      - `readCharLiteral()`: Reads characters (e.g., `'a'`, `'\n'`), handling escape sequences.
    - Uses DFAs (`Automaton.getLongestAcceptedLength`) to match:
      - **Identifiers**: `[a-z]+` (e.g., `x`), checked against keywords.
      - **Numbers**: Validates via `validateNumber()` for decimals (≤5 places) and exponents.
      - **Operators**: Single or double characters (e.g., `+`, `==`).
    - Adds an `EOF` token at the end.
- **Supporting Functions**:
  - `validateNumber(String value)`: Ensures number format compliance.
  - `handleComments()`: Manages comment skipping.
  - `readStringLiteral()`: Extracts string content.
  - `readCharLiteral()`: Extracts character content.
- **Output**: A `List<Token>` (e.g., `KEYWORD:int (Line 1)`, `IDENTIFIER:x (Line 1)`, etc.).
- **Where**: Called in `Compiler.main`.

### 3. **Syntactic Analysis**
- **Class**: `Parser`
- **Main Function**: `parse()`
- **Process**:
  - **Initialization**: `Parser` constructor sets up the token list, symbol table, and error handler.
  - **Parsing**:
    - Loops through tokens until `EOF`, calling `parseStatement()` for each statement.
    - **Scope Handling**:
      - `{` (`LBRACE`): Calls `SymbolTable.enterScope()` to create a new scope.
      - `}` (`RBRACE`): Calls `SymbolTable.exitScope()` to end the scope.
    - **Declarations**:
      - Matches `type identifier = expression ;`.
      - Checks: Identifier is single-letter, not redeclared in the current scope, followed by `=`.
      - Adds to `SymbolTable` with type and scope (e.g., `int-global`).
      - Validates variables in the expression via `SymbolTable.lookup()`.
    - **Assignments**:
      - Matches `identifier = expression ;`.
      - Checks: Identifier is declared, followed by `=`.
      - Validates variables in the expression.
    - Ensures semicolons (`;`) terminate statements.
- **Supporting Functions**:
  - `currentToken()`: Retrieves the current token.
  - `nextToken()`: Advances to the next token.
  - `parseStatement()`: Parses individual statements (scopes, declarations, assignments).
  - `isTypeKeyword(String keyword)`: Identifies type keywords.
- **Output**: Updates the `SymbolTable` and logs errors via `ErrorHandler`.
- **Where**: Called in `Compiler.main`.

### 4. **Symbol Management**
- **Class**: `SymbolTable`
- **Process**:
  - **Initialization**: Constructor creates a global scope.
  - **Scope Management**:
    - `enterScope()`: Adds a new local scope.
    - `exitScope()`: Removes the current scope and saves it as completed.
  - **Variable Tracking**:
    - `addSymbol(String name, String type)`: Adds a variable to the current scope.
    - `lookup(String name)`: Checks if a variable exists in any scope.
    - `existsInCurrentScope(String name)`: Prevents redeclaration in the current scope.
    - `currentScopeDepth()`: Determines if a variable is global or local.
  - **Display**: `displaySymbols()` prints global and local scopes.
- **Output**: A structured symbol table (e.g., `x : int-global`, `y : int-local`).
- **Where**: Used by `Parser` during syntactic analysis.

### 5. **Error Handling**
- **Class**: `ErrorHandler`
- **Process**:
  - `reportError(int line, String message)`: Logs errors with line numbers (e.g., `[Line 1] ERROR: Missing semicolon`).
  - `displayErrors()`: Prints all errors.
- **Where**: Called by `Lexer` (e.g., invalid characters) and `Parser` (e.g., syntax errors).

### 6. **Automata Support**
- **Classes**: `Automaton` and `NFA`
- **Process**:
  - **`Automaton`**:
    - `addState`, `setStartState`, `addTransition`: Configures DFA states and transitions.
    - `getLongestAcceptedLength`: Matches the longest valid token prefix.
    - `displayTransitionTable`: Shows DFA structure.
  - **`NFA`**:
    - `addEpsilonTransition`: Adds epsilon transitions (theoretical).
- **Where**: DFAs are used in `Lexer.tokenize()`; NFAs are only displayed.

### 7. **Output**
- **Where**: `Compiler.main`
- **Process**:
  - Prints regular expressions (`Lexer.identifierRE`, etc.).
  - Displays NFA transition tables (`identifierNFA.displayTransitionTable()`, etc.).
  - Lists tokens (`tokens.forEach()`).
  - Shows symbol table (`SymbolTable.displaySymbols()`).
  - Reports errors (`ErrorHandler.displayErrors()`).
  - Displays DFA transition tables (`identifierDFA.displayTransitionTable()`, etc.).

---

## Detailed Workflow

1. **Start (`Compiler.main`)**:
   - Reads `input.redblack` into a string.
   - Initializes `ErrorHandler` and `Lexer`.

2. **Lexical Analysis (`Lexer.tokenize`)**:
   - Sets up DFAs and NFAs.
   - Scans the input, producing tokens using DFAs.
   - Handles comments, literals, and validates numbers.

3. **Intermediate Output**:
   - Prints regular expressions, NFA tables, and tokens.

4. **Syntactic Analysis (`Parser.parse`)**:
   - Processes tokens, enforcing syntax rules.
   - Builds the `SymbolTable` and logs errors.

5. **Final Output**:
   - Displays the symbol table, errors, and DFA tables.

---

## Example Execution
For input:
```
int x = 5;
{
    int y = x + 3;
}
x = 10;
```

- **Tokens**: 
  - `KEYWORD:int (Line 1)`, `IDENTIFIER:x (Line 1)`, `OPERATOR:= (Line 1)`, etc.
- **Symbol Table**:
  - Global: `x : int-global`
  - Local (exited): `y : int-local`
- **Errors**: None (if syntax is correct).

---

## Implementation Details

- **DFAs**: Used for token recognition (identifiers, numbers, operators).
- **NFAs**: Defined but not used in tokenization (educational purpose).
- **Symbol Table**: Stack-based for active scopes, list-based for completed scopes.
- **Error Handling**: Robust, covering lexical (e.g., invalid characters) and syntactic (e.g., missing `;`) errors.

---

## Limitations

- **Language**: Limited to single-letter identifiers and basic expressions.
- **No Semantics**: Lacks type checking or code generation.
- **File Path**: Hardcoded to a Windows directory.
- **NFAs**: Unused in practice despite implementation.

---

## Conclusion

This compiler frontend successfully demonstrates key compiler concepts: lexical analysis with DFAs, syntactic analysis with parsing, and symbol table management. It provides detailed output for understanding the compilation process, making it a valuable educational tool. Future enhancements could include multi-character identifiers, semantic analysis, and a more flexible input mechanism.

--- 
