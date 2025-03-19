package Assig1;

class NFA extends Automaton {
    public void addEpsilonTransition(String from, String to) {
        addTransition(from, 'ε', to); // Use 'ε' to represent epsilon transitions
    }
}