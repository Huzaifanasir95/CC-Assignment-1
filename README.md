
Overview
This project is a basic compiler frontend written in Java, designed as part of an academic assignment for a Compiler Construction course. It processes a simplified programming language by performing lexical analysis, syntactic analysis, symbol table management, and error reporting. The compiler reads source code from a file (e.g., input.redblack), tokenizes it, checks its syntax, and tracks variable declarations across scopes.

Authors
Huzaifa Nasir (Roll No: 22i-1053, Section: CS-A)
Maaz Ali (Roll No: 22i-1042, Section: CS-A)
Features
Lexical Analysis: Breaks source code into tokens using Deterministic Finite Automata (DFAs).
Syntactic Analysis: Parses tokens to enforce syntax rules and build a symbol table.
Symbol Table: Manages variable declarations with global and local scopes.
Error Handling: Reports lexical and syntactic errors with line numbers.
Automata Visualization: Displays DFA and NFA transition tables for educational purposes.
Supported Constructs:
Variable declarations (e.g., int x = 5;)
Assignments (e.g., x = 10;)
Nested scopes with { and }
Comments (// and /* */)
String and character literals
Basic operators (+, -, *, /, %, <, >, =, !, ==, <=, >=, !=)
