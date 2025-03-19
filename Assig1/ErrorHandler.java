package Assig1;
import java.util.*;

class ErrorHandler {
    private List<String> errors = new ArrayList<>();

    public void reportError(int line, String message) {
        String prefix = (line >= 0) ? "[Line " + line + "] " : "";
        errors.add(prefix + "ERROR: " + message);
    }

    public void displayErrors() {
        if (!errors.isEmpty()) {
            System.out.println("\n=== Errors ===");
            errors.forEach(System.out::println);
        }
    }
}