package Assig1;
import java.util.*;

class Automaton {
    private Set<String> states = new HashSet<>();
    private Map<String, Map<Character, String>> transitions = new HashMap<>();
    private String startState;
    private Set<String> acceptStates = new HashSet<>();

    public void addState(String state, boolean isAccept) {
        states.add(state);
        if (isAccept) {
            acceptStates.add(state);
        }
    }

    public void setStartState(String state) {
        startState = state;
    }

    public void addTransition(String from, char input, String to) {
        transitions.putIfAbsent(from, new HashMap<>());
        transitions.get(from).put(input, to);
    }

    // Returns the length of the longest prefix (from 'start') accepted by this DFA.
    public int getLongestAcceptedLength(String input, int start) {
        String state = startState;
        int longestAccepted = -1;
        int length = 0;
        if (acceptStates.contains(state)) {
            longestAccepted = 0;
        }
        for (int i = start; i < input.length(); i++) {
            char c = input.charAt(i);
            Map<Character, String> stateTrans = transitions.get(state);
            if (stateTrans == null || !stateTrans.containsKey(c)) {
                break;
            }
            state = stateTrans.get(c);
            length++;
            if (acceptStates.contains(state)) {
                longestAccepted = length;
            }
        }
        return longestAccepted >= 0 ? longestAccepted : 0;
    }

    public void displayTransitionTable() {
        System.out.println("Total States: " + states.size());
        System.out.println("States: " + states);
        System.out.println("Accepting States: " + acceptStates);
        System.out.println("Transitions:");
        for (String state : transitions.keySet()) {
            System.out.println("  " + state + " -> " + transitions.get(state));
        }
    }
}