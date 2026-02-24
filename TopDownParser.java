import java.util.*;

public class TopDownParser {

    static Scanner scanner = new Scanner(System.in);

    // Encapsulate parser state to pass around easily
    static class ParserState {
        char condition = 'q'; // q: normal, b: backtrack, t: terminate
        int position = 1;
        int counter = 0;
        List<String> solution = new ArrayList<>();
        List<String> alternatives = new ArrayList<>();

        public ParserState(String initialAlt) {
            solution.add("#");
            alternatives.add(initialAlt);
        }

        // Copy constructor for backtracking/branching if needed,
        // though we are modifying state in place for this specific algorithm style
        public ParserState(ParserState other) {
            this.condition = other.condition;
            this.position = other.position;
            this.counter = other.counter;
            this.solution = new ArrayList<>(other.solution);
            this.alternatives = new ArrayList<>(other.alternatives);
        }

        void print(String stage) {
            String sol = String.join("", solution);
            String alt = String.join("", alternatives);
            System.out.printf("(%c,%d,%s,%s)\t(%s)%n", condition, position, sol, alt, stage);
        }
    }

    static class Grammar {
        List<String> rulesLeft = new ArrayList<>();
        List<String> rulesRight = new ArrayList<>();
        List<String> newRulesLeft = new ArrayList<>(); // S1, S2, etc.

        // Specific semantic groups for this specific parser logic
        List<String> sLeft = new ArrayList<>();
        List<String> sRight = new ArrayList<>();
        List<String> tLeft = new ArrayList<>();
        List<String> tRight = new ArrayList<>();

        void splitRules() {
            int mid = rulesRight.size() / 2;
            for (int i = 0; i < mid; i++) {
                sLeft.add(newRulesLeft.get(i));
                sRight.add(rulesRight.get(i));
            }
            for (int i = mid; i < rulesRight.size(); i++) {
                tLeft.add(newRulesLeft.get(i));
                tRight.add(rulesRight.get(i));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Implementation of top-down parser");
        System.out.println(
                "Please provide the derivation rules!\nA The derivation rules should be of the following form:: A->aS");
        System.out.println("S->T+S S->T T->a T->b end"); // Prompt hint matching sample

        Grammar grammar = new Grammar();
        readRules(grammar);

        System.out.println("\nThank you!\nPlease provide the task to be solved!");
        String taskString = scanner.next();
        List<String> taskVector = new ArrayList<>();
        for (char c : taskString.toCharArray())
            taskVector.add(String.valueOf(c));

        printSetup(grammar, taskVector);
        generateAlternativeNames(grammar);
        grammar.splitRules();

        System.out.println("\n\nDerivation: \n");
        ParserState state = new ParserState(grammar.rulesLeft.get(0));
        state.print("initial");

        process(grammar, taskVector, state);
    }

    // Recursive main loop
    public static void process(Grammar g, List<String> task, ParserState state) {
        // We use a simplified state machine approach here

        String head = state.alternatives.isEmpty() ? "" : state.alternatives.get(0);

        // 1. Expand S
        if (head.equals("S")) {
            expandS(g, state);
        }
        // 2. Terminal 'a' match success (Hardcoded limit condition from original)
        else if (head.equals("a")) {
            matchA(state);
        }
        // 3. Expand T
        else if (!head.isEmpty() && head.charAt(0) == 'T') {
            expandT(g, state);
        }
        // 4. Specific Backtracking State (Counter == 4)
        else if (state.counter == 4) {
            handleComplexBacktrack(g, state);
        }
        // 5. Input Bounds Check / Backtracking
        else if (state.position > 3) { // Hardcoded limit from original
            handleBoundsCheck(state);
        }
        // 6. Successful Input Match
        else if (!head.isEmpty() && head.charAt(0) == task.get(state.position - 1).charAt(0)) {
            matchInput(state);
        }
        // 7. Mismatch & Backtrack
        else if (shouldBacktrack(g, state, task)) {
            handleMismatch(g, state);
        }
        // 8. End
        else {
            System.out.println("end of program!");
            System.exit(0);
        }

        // Recursive call
        process(g, task, state);
    }

    // --- Action Methods ---

    private static void expandS(Grammar g, ParserState s) {
        if (s.solution.get(0).equals("#"))
            s.solution.clear();
        s.solution.add(g.sLeft.get(0));
        s.alternatives.clear();
        s.alternatives.add(g.sRight.get(0));
        s.print("S extension");
    }

    private static void matchA(ParserState s) {
        s.position++;
        s.solution.add(s.alternatives.get(0));
        s.alternatives.set(0, "#");
        s.print("uccessful input matching");

        s.condition = 't';
        s.print("successful analysis!");
        System.exit(0);
    }

    private static void expandT(Grammar g, ParserState s) {
        String head = s.alternatives.get(0);
        if (s.counter == 4 && !head.equals("T")) {
            s.condition = 'q';
            s.solution.remove(s.solution.size() - 1);
            s.solution.add(g.sLeft.get(1));
            s.alternatives.set(0, g.sRight.get(1));
            s.print("backtrack in the extension");
        } else if (s.counter == 4 && head.equals("T")) {
            s.solution.add(g.tLeft.get(0));
            s.alternatives.set(0, g.tRight.get(0));
            s.print("T extension");
        } else {
            s.solution.add(g.tLeft.get(0));
            String newAlt = g.tRight.get(0).charAt(0) + head.substring(1);
            s.alternatives.set(0, newAlt);
            s.print("T extension");
        }
    }

    private static void handleComplexBacktrack(Grammar g, ParserState s) {
        char headChar = s.alternatives.get(0).charAt(0);
        if (headChar == g.tRight.get(0).charAt(0)) {
            s.condition = 'q';
            s.solution.remove(s.solution.size() - 1);
            s.solution.add(g.tLeft.get(1));

            String newAlt = g.tRight.get(1).charAt(0) + s.alternatives.get(0).substring(1);
            s.alternatives.set(0, newAlt);
            s.print("backtrack in the extension");
        } else if (headChar == g.tRight.get(1).charAt(0)) {
            s.condition = 'b';
            s.solution.remove(s.solution.size() - 1);
            s.alternatives.set(0, g.sRight.get(0));
            s.print("backtrack in the extension");
        }
    }

    private static void handleBoundsCheck(ParserState s) {
        if (s.condition == 'q') {
            s.condition = 'b';
            s.print("unsuccessful input matching");
        } else {
            s.condition = 'b';
            s.position--;
            String lastSol = s.solution.get(s.solution.size() - 1);
            s.solution.remove(s.solution.size() - 1);
            s.alternatives.set(0, lastSol + s.alternatives.get(0));
            s.print("backtrack in input");
            s.counter++;
        }
    }

    private static void matchInput(ParserState s) {
        s.position++;
        char match = s.alternatives.get(0).charAt(0);
        s.solution.add(String.valueOf(match));
        s.alternatives.set(0, s.alternatives.get(0).substring(1));
        s.print("successful input matching");
        s.counter++;
    }

    private static boolean shouldBacktrack(Grammar g, ParserState s, List<String> task) {
        if (s.alternatives.isEmpty())
            return false;
        char head = s.alternatives.get(0).charAt(0);
        char input = task.get(s.position - 1).charAt(0);

        boolean isT1 = head == g.tRight.get(0).charAt(0);
        boolean isT2 = head == g.tRight.get(1).charAt(0);

        return (head != input) && (isT1 || isT2);
    }

    private static void handleMismatch(Grammar g, ParserState s) {
        char head = s.alternatives.get(0).charAt(0);
        if (head == g.tRight.get(0).charAt(0) && s.condition == 'q') {
            s.condition = 'b';
            s.print("unsuccessful input matching)");
        } else {
            s.condition = 'q';
            String newAlt = g.tRight.get(1).charAt(0) + s.alternatives.get(0).substring(1);
            s.alternatives.set(0, newAlt);
            s.solution.remove(s.solution.size() - 1);
            s.solution.add(g.tLeft.get(1));
            s.print("backtrack in extension");
        }
    }

    // --- Setup Helpers ---

    private static void readRules(Grammar g) {
        String token = scanner.next();

        while (!token.equals("end")) {
            String s = token;
            int pos;
            while ((pos = s.indexOf("->")) != -1) {
                g.rulesLeft.add(s.substring(0, pos));
                s = s.substring(pos + 2);
                g.rulesRight.add(s);
            }
            token = scanner.next();
        }
    }

    private static void generateAlternativeNames(Grammar g) {
        Map<Character, Integer> counts = new HashMap<>(); // S -> count

        // Count non-terminals
        for (String rule : g.rulesLeft) {
            char key = rule.charAt(0);
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        // Generate S1, S2 output
        int globalCounter = 0;
        // We need to iterate in order of rulesLeft to match original logic
        // Original logic is a bit convoluted with double loops, but effectively assigns
        // S1, S2, T1, T2

        // Let's replicate the exact "elementsLeft" logic efficiently
        List<Character> uniqueLefts = new ArrayList<>();
        for (String r : g.rulesLeft) {
            char c = r.charAt(0);
            if (!uniqueLefts.contains(c))
                uniqueLefts.add(c);
        }

        for (char nonTerminal : uniqueLefts) {
            System.out.print(nonTerminal + " alternatives: ");
            int count = counts.get(nonTerminal);
            for (int j = 0; j < count; j++) {
                String right = g.rulesRight.get(globalCounter);
                System.out.print(nonTerminal + Integer.toString(j + 1) + " = " + right + ". ");
                g.newRulesLeft.add(nonTerminal + Integer.toString(j + 1));
                globalCounter++;
            }
            System.out.println();
        }
    }

    private static void printSetup(Grammar g, List<String> task) {
        System.out.print("This is on the left side: ");
        g.rulesLeft.forEach(s -> System.out.print(s + " "));
        System.out.print("\nThis is on the right side: ");
        g.rulesRight.forEach(s -> System.out.print(s + " "));
        System.out.print("\nThis is the task to be solved: ");
        task.forEach(System.out::print);
        System.out.println("\n");
    }
}
