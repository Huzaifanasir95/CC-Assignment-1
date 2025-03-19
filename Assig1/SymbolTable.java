package Assig1;

import java.util.*;

class SymbolTable {
    // The stack holds active scopes.
    private Stack<Map<String, String>> scopes = new Stack<>();
    // Completed scopes are stored here for display purposes.
    private List<Map<String, String>> completedScopes = new ArrayList<>();

    public SymbolTable() {
        // Global scope.
        scopes.push(new HashMap<>());
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) {
            // Save the local scope for later display.
            Map<String, String> completed = scopes.peek();
            completedScopes.add(new HashMap<>(completed));
            scopes.pop();
        }
    }

    // Adds a symbol with its type (e.g., "int-global" or "int-local").
    public void addSymbol(String name, String type) {
        scopes.peek().put(name, type);
    }

    // Checks if a symbol exists in any active scope.
    public boolean lookup(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    // Checks if a symbol exists in the current (top) scope.
    public boolean existsInCurrentScope(String name) {
        return scopes.peek().containsKey(name);
    }

    // Returns the current scope depth (1 = global, >1 = local).
    public int currentScopeDepth() {
        return scopes.size();
    }

    public void displaySymbols() {
        System.out.println("\n=== Symbol Table ===");
        // Display global scope.
        System.out.println("global scope:");
        Map<String, String> global = scopes.firstElement();
        for (Map.Entry<String, String> entry : global.entrySet()) {
            System.out.println("  " + entry.getKey() + " : " + entry.getValue());
        }
        // Display completed (exited) local scopes.
        for (int i = 0; i < completedScopes.size(); i++) {
            System.out.println("local scope (exited):");
            Map<String, String> local = completedScopes.get(i);
            for (Map.Entry<String, String> entry : local.entrySet()) {
                System.out.println("  " + entry.getKey() + " : " + entry.getValue());
            }
        }
        // Display any remaining active local scopes (if any beyond global).
        if (scopes.size() > 1) {
            for (int i = 1; i < scopes.size(); i++) {
                System.out.println("local scope (active):");
                Map<String, String> local = scopes.get(i);
                for (Map.Entry<String, String> entry : local.entrySet()) {
                    System.out.println("  " + entry.getKey() + " : " + entry.getValue());
                }
            }
        }
    }
}