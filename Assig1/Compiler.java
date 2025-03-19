package Assig1;
import java.util.*;
import java.io.*;
public class Compiler {
    public static void main(String[] args) {
        try {
        	
        	
            String input = readFile("D:\\SEM6\\CC\\FR_Assignment1\\A1\\src\\Assig1\\input.redblack");
            ErrorHandler errorHandler = new ErrorHandler();
            Lexer lexer = new Lexer(input, errorHandler);
            List<Token> tokens = lexer.tokenize();
            
            System.out.println("\n=== Regular Expressions ===");
            System.out.println(lexer.identifierRE);
            System.out.println(lexer.numberRE);
            System.out.println(lexer.operatorRE);

            System.out.println("\n=== Identifier NFA Transition Table ===");
            lexer.identifierNFA.displayTransitionTable();
            System.out.println("\n=== Number NFA Transition Table ===");
            lexer.numberNFA.displayTransitionTable();
            System.out.println("\n=== Operator NFA Transition Table ===");
            lexer.operatorNFA.displayTransitionTable();
            
            System.out.println("=== Tokens ===");
            tokens.forEach(System.out::println);

            SymbolTable symbolTable = new SymbolTable();
            Parser parser = new Parser(tokens, symbolTable, errorHandler);
            parser.parse();

            symbolTable.displaySymbols();
            errorHandler.displayErrors();

            // Display DFA transition tables.
            System.out.println("\n=== Identifier DFA Transition Table ===");
            lexer.identifierDFA.displayTransitionTable();
            System.out.println("\n=== Number DFA Transition Table ===");
            lexer.numberDFA.displayTransitionTable();
            System.out.println("\n=== Operator DFA Transition Table ===");
            lexer.operatorDFA.displayTransitionTable();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
