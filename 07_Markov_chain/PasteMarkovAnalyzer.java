import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reads paste FSM logs and prints Markov-chain transition probabilities.
 *
 * Expected log format:
 * FSM_LOG|time=...|event=...|from=...|to=...|user=...
 *
 * Compile:
 * javac PasteMarkovAnalyzer.java
 *
 * Execute:
 * java PasteMarkovAnalyzer paste_fsm_log.txt
 */
public class PasteMarkovAnalyzer {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java PasteMarkovAnalyzer <paste_fsm_log.txt>");
            return;
        }

        MarkovCounter overallCounter = new MarkovCounter();
        Map<String, MarkovCounter> countersByUser = new TreeMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("FSM_LOG|")) {
                continue;
            }

            Map<String, String> fields = parseFields(line);
            String from = fields.get("from");
            String to = fields.get("to");
            String user = fields.get("user");

            if (from == null || to == null || from.length() == 0 || to.length() == 0) {
                continue;
            }

            overallCounter.addTransition(from, to);

            if (user != null && user.length() > 0) {
                MarkovCounter userCounter = countersByUser.get(user);
                if (userCounter == null) {
                    userCounter = new MarkovCounter();
                    countersByUser.put(user, userCounter);
                }
                userCounter.addTransition(from, to);
            }
        }
        reader.close();

        System.out.println("=== Overall Markov Transition Probabilities ===");
        overallCounter.printProbabilities();

        if (!countersByUser.isEmpty()) {
            System.out.println();
            System.out.println("=== Markov Transition Probabilities By User ===");
            for (Map.Entry<String, MarkovCounter> entry : countersByUser.entrySet()) {
                System.out.println();
                System.out.println("User: " + entry.getKey());
                entry.getValue().printProbabilities();
            }
        }
    }

    private static Map<String, String> parseFields(String line) {
        Map<String, String> fields = new TreeMap<>();
        String[] parts = line.split("\\|");
        for (int i = 1; i < parts.length; i++) {
            int equalsIndex = parts[i].indexOf('=');
            if (equalsIndex == -1) {
                continue;
            }
            String key = parts[i].substring(0, equalsIndex);
            String value = parts[i].substring(equalsIndex + 1);
            fields.put(key, value);
        }
        return fields;
    }

    static class MarkovCounter {
        private final Map<String, Map<String, Integer>> transitionCounts = new TreeMap<>();

        public void addTransition(String from, String to) {
            Map<String, Integer> outgoing = transitionCounts.get(from);
            if (outgoing == null) {
                outgoing = new TreeMap<>();
                transitionCounts.put(from, outgoing);
            }

            Integer count = outgoing.get(to);
            if (count == null) {
                count = Integer.valueOf(0);
            }
            outgoing.put(to, Integer.valueOf(count.intValue() + 1));
        }

        public void printProbabilities() {
            if (transitionCounts.isEmpty()) {
                System.out.println("No valid FSM transitions were found.");
                return;
            }

            for (Map.Entry<String, Map<String, Integer>> fromEntry : transitionCounts.entrySet()) {
                String from = fromEntry.getKey();
                Map<String, Integer> outgoing = fromEntry.getValue();
                int total = 0;
                for (Integer count : outgoing.values()) {
                    total += count.intValue();
                }

                for (Map.Entry<String, Integer> toEntry : outgoing.entrySet()) {
                    String to = toEntry.getKey();
                    int count = toEntry.getValue().intValue();
                    double probability = (double) count / (double) total;
                    System.out.printf("%s -> %s | count=%d | total=%d | probability=%.4f%n",
                            from, to, count, total, probability);
                }
            }
        }
    }
}
