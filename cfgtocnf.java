import java.util.*;

public class GrammarConverter {

    // Function to remove specific characters at positions
    public static String removeChar(String str, String pos) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (!pos.contains(String.valueOf(i))) {
                newString.append(str.charAt(i));
            }
        }
        return newString.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input symbols
        System.out.print("Enter the NonTerminal Symbols: ");
        List<String> NT_symbol = Arrays.asList(scanner.nextLine().split(" "));

        System.out.print("Enter the Terminal Symbols: ");
        List<String> T_symbol = Arrays.asList(scanner.nextLine().split(" "));

        Map<String, List<String>> main = new HashMap<>();
        Map<String, String> new_NT_for_T = new HashMap<>(); // Map for new non-terminals for terminals

        // Eliminating NULL Production
        List<String> nullable = new ArrayList<>();

        // Input production rules
        for (String nt : NT_symbol) {
            System.out.print(nt + " -> ");
            main.put(nt, Arrays.asList(scanner.nextLine().split("/")));
        }

        // Find nullable productions
        for (String nt : main.keySet()) {
            for (String prod : main.get(nt)) {
                if (prod.contains("^")) { // '^' represents epsilon/null production
                    nullable.add(nt);
                }
            }
        }

        // Remove duplicates from nullable Non-Terminals
        nullable = new ArrayList<>(new HashSet<>(nullable));

        // Handle nullable productions
        for (String nt : main.keySet()) {
            List<String> updatedProductions = new ArrayList<>(main.get(nt));
            for (String prod : main.get(nt)) {
                String pos = "";
                List<String> possibleCombinations = new ArrayList<>();

                for (int i = 0; i < prod.length(); i++) {
                    if (nullable.contains(String.valueOf(prod.charAt(i)))) {
                        pos += i;
                    }
                }

                for (int b = 0; b < pos.length(); b++) {
                    possibleCombinations.addAll(generatePermutations(pos, b + 1));
                }

                for (String comb : possibleCombinations) {
                    String newProd = removeChar(prod, comb);
                    if (!newProd.equals("^")) {
                        updatedProductions.add(newProd);
                    }
                }
            }

            // Replace productions and remove empty ones
            main.put(nt, updatedProductions);
            main.get(nt).removeIf(p -> p.equals(""));
        }

        // Removing unit productions
        Map<String, List<String>> unitProductions = new HashMap<>();
        for (String nt : NT_symbol) {
            unitProductions.put(nt, new ArrayList<>());
        }

        for (String nt : main.keySet()) {
            for (String prod : main.get(nt)) {
                if (NT_symbol.contains(prod)) {
                    unitProductions.get(nt).add(prod);
                }
            }
        }

        for (String nt : unitProductions.keySet()) {
            for (String unit : unitProductions.get(nt)) {
                main.get(nt).remove(unit);
                for (String prod : main.get(unit)) {
                    if (!main.get(nt).contains(prod)) {
                        main.get(nt).add(prod);
                    }
                }
            }
        }

        // Printing the grammar after removing NULL and Unit productions
        System.out.println("\nAFTER REMOVING NULL AND UNIT PRODUCTION");
        for (String nt : main.keySet()) {
            System.out.println(nt + " -> " + String.join("/", main.get(nt)));
        }

        // Converting to CNF form
        System.out.println("\nConverting to CNF form:");
        int letter = 0; // To generate new non-terminal symbols

        // Map each terminal to a unique non-terminal
        for (String term : T_symbol) {
            String newNT = "X" + letter++;
            new_NT_for_T.put(term, newNT);
            main.put(newNT, Collections.singletonList(term));
        }

        for (String nt : NT_symbol) {
            List<String> updatedProductions = new ArrayList<>();
            for (String prod : main.get(nt)) {
                StringBuilder newProd = new StringBuilder();

                for (char ch : prod.toCharArray()) {
                    String strCh = String.valueOf(ch);
                    if (T_symbol.contains(strCh)) {
                        newProd.append(new_NT_for_T.get(strCh));
                    } else {
                        newProd.append(ch);
                    }
                }

                String transformed = newProd.toString();
                while (transformed.length() > 2) {
                    String newNT = "X" + letter++;
                    NT_symbol.add(newNT);
                    main.put(newNT, Collections.singletonList(transformed.substring(transformed.length() - 2)));
                    transformed = transformed.substring(0, transformed.length() - 2) + newNT;
                }

                updatedProductions.add(transformed);
            }

            main.put(nt, updatedProductions);
        }

        // Output the CNF form grammar
        for (String nt : main.keySet()) {
            System.out.println(nt + " -> " + String.join("/", main.get(nt)));
        }

        scanner.close();
    }

    // Helper function to generate permutations
    public static List<String> generatePermutations(String str, int length) {
        List<String> result = new ArrayList<>();
        generatePermutationsHelper("", str, length, result);
        return result;
    }

    private static void generatePermutationsHelper(String prefix, String str, int length, List<String> result) {
        if (prefix.length() == length) {
            result.add(prefix);
            return;
        }

        for (int i = 0; i < str.length(); i++) {
            generatePermutationsHelper(prefix + str.charAt(i), str.substring(0, i) + str.substring(i + 1), length, result);
        }
    }
}
