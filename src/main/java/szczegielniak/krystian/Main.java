package szczegielniak.krystian;

import szczegielniak.krystian.engine.SearchEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String QUERY_MODE_COMMAND = "?";
    private static final String INDEX_MODE_COMMAND = "+";
    private static final String QUERY_MODE_PROMPT = "[QUERY]==> Type your query or switch mode: ";
    private static final String INDEX_MODE_PROMPT = "[INDEX]==> Insert a document or switch mode: ";

    private static final String WELCOME_MESSAGE = "======================================== WELCOME TO MY SIMPLE SEARCH ENGINE ========================================\n"
            + "It may operate in one of two modes: Query and Index.\n"
            + "Which one you are currently using is denoted by the command prompt:\n"
            + "\t- Query: " + QUERY_MODE_PROMPT + "\n"
            + "\t- Index: " + INDEX_MODE_PROMPT + "\n"
            + "\nTo switch between modes use following commands:\n"
            + "\t- ? - switch to Query mode\n"
            + "\t- + - switch to Index mode\n"
            + "====================================================================================================================\n"
            + "\n======================================== INDEX MODE ========================================\n"
            + "# While in Index mode you can index your documents.\n"
            + "# One line of your input is treated as one document.\n"
            + "# You can enter multiple documents in one go.\n"
            + "# When you have entered all documents you want to be indexed, "
            + "enter an empty line to perform the indexing.\n"
            + "# Information about documents you wished to be indexed should be displayed in the command line.\n"
            + "============================================================================================\n"
            + "\n======================================== QUERY MODE ========================================\n"
            + "# Inside the Query mode you just input the terms you wish to search the index for.\n"
            + "# After entering a search term, the result will be displayed in the command line.\n"
            + "============================================================================================\n"
            + "\n======================================== EXAMPLE OF USAGE ========================================\n"
            + "\n"
            + "[INDEX]==> Insert a document or switch mode: ThE BRoWN;;,Fox   \tJuMpED::::;ovEr;  thE BroWN dOG\n"
            + "Insert next or press enter to start indexing ==> THE LazY '''\"BRown DOG saT in ThE CorNer\n"
            + "Insert next or press enter to start indexing ==> THE reD :::;;\"foX \tBIT thE lazy DOg\n"
            + "Insert next or press enter to start indexing ==>\n"
            + "Indexing of 3 documents started.\n"
            + "\n"
            + "Indexing document: { ThE BRoWN;;,Fox   \tJuMpED::::;ovEr;  thE BroWN dOG }.\n"
            + "After parsing: { the brown fox jumped over the brown dog }.\n"
            + "Document indexed with ID: 1.\n"
            + "\n"
            + "Indexing document: { THE LazY '''\"BRown DOG saT in ThE CorNer }.\n"
            + "After parsing: { the lazy brown dog sat in the corner }.\n"
            + "Document indexed with ID: 2.\n"
            + "\n"
            + "Indexing document: { THE reD :::;;\"foX \tBIT thE lazy DOg }.\n"
            + "After parsing: { the red fox bit the lazy dog }.\n"
            + "Document indexed with ID: 3.\n"
            + "\n"
            + "Indexing of your documents finished.\n"
            + "[INDEX]==> Insert a document or switch mode: ?\n"
            + "Switching to QUERY Mode.\n"
            + "[QUERY]==> Type your query or switch mode: bRoWN\n"
            + "Result: [Document 1, Document 2]\n"
            + "[QUERY]==> Type your query or switch mode: sAt\n"
            + "Result: [Document 2]\n"
            + "[QUERY]==> Type your query or switch mode: krAkeN\n"
            + "No results found for query: krAkeN\n"
            + "[QUERY]==> Type your query or switch mode: hAir\n"
            + "No results found for query: hAir\n"
            + "[QUERY]==> Type your query or switch mode: +\n"
            + "Switching to INDEX Mode.\n"
            + "[INDEX]==> Insert a document or switch mode: BroWn:the\t hair,brown;the:eYEs,.BROwN;;the:snickeRS\n"
            + "Insert next or press enter to start indexing ==>\n"
            + "Indexing of 1 documents started.\n"
            + "\n"
            + "Indexing document: { BroWn:the\t hair,brown;the:eYEs,.BROwN;;the:snickeRS }.\n"
            + "After parsing: { brown the hair brown the eyes brown the snickers }.\n"
            + "Document indexed with ID: 4.\n"
            + "\n"
            + "Indexing of your documents finished.\n"
            + "[INDEX]==> Insert a document or switch mode: ?\n"
            + "Switching to QUERY Mode.\n"
            + "[QUERY]==> Type your query or switch mode: browN\n"
            + "Result: [Document 4, Document 1, Document 2]\n"
            + "[QUERY]==> Type your query or switch mode: haIR\n"
            + "Result: [Document 4]\n"
            + "[QUERY]==> Type your query or switch mode: kraKen\n"
            + "No results found for query: kraKen\n"
            + "[QUERY]==> Type your query or switch mode: +\n"
            + "Switching to INDEX Mode.\n"
            + "[INDEX]==> Insert a document or switch mode:\n"
            + "==================================================================================================\n\n";

    private static final SearchEngine SEARCH_ENGINE = new SearchEngine();

    private static WORKING_MODE currentMode = WORKING_MODE.INDEX;
    private static List<String> bufferedInputs = new ArrayList<>();

    public static void main(String[] args) {
        welcome();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (currentMode == WORKING_MODE.INDEX) {
                handleIndexModeInput(scanner);
            } else {
                handleQueryModeInput(scanner);
            }
        }
    }

    private static void handleIndexModeInput(Scanner scanner) {
        prompt();
        while (true) {
            String input = scanner.nextLine();
            if (QUERY_MODE_COMMAND.equalsIgnoreCase(input)) {
                if (!bufferedInputs.isEmpty()) {
                    cannotSwitchModesWhenAddingDocuments();
                    addAnotherOrIndexAll();
                    continue;
                }
                flipModes();
                switchingModesMessage();
                break;
            }
            if (INDEX_MODE_COMMAND.equalsIgnoreCase(input)) {
                sameModeMessage();
                prompt();
                continue;
            }
            if (input.length() == 0) {
                startIndexing();
                SEARCH_ENGINE.index(bufferedInputs);
                bufferedInputs = new ArrayList<>();
                indexingFinished();
                prompt();
            } else {
                addAnotherOrIndexAll();
                bufferedInputs.add(input);
            }
        }
    }

    private static void startIndexing() {
        System.out.println("Indexing of " + bufferedInputs.size() + " documents started.\n");
    }

    private static void handleQueryModeInput(Scanner scanner) {
        while (true) {
            prompt();
            String input = scanner.nextLine();
            if (QUERY_MODE_COMMAND.equalsIgnoreCase(input)) {
                sameModeMessage();
                continue;
            }
            if (INDEX_MODE_COMMAND.equalsIgnoreCase(input)) {
                flipModes();
                switchingModesMessage();
                break;
            }
            List<String> results = SEARCH_ENGINE.query(input);
            if (results.isEmpty()) {
                System.out.println("No results found for query: " + input);
            } else {
                System.out.println("Result: [" + results
                        .stream()
                        .map(id -> "Document " + id)
                        .collect(Collectors.joining(", ")) + "]");
            }
        }
    }

    private static void sameModeMessage() {
        System.out.println("You already are inside " + currentMode.name() + " mode.");
    }

    private static void welcome() {
        System.out.println(WELCOME_MESSAGE);
    }

    private static void flipModes() {
        currentMode = (currentMode == WORKING_MODE.INDEX ? WORKING_MODE.QUERY : WORKING_MODE.INDEX);
    }

    private static void switchingModesMessage() {
        System.out.println("Switching to " + currentMode + " Mode.");
    }

    private static void indexingFinished() {
        System.out.println("Indexing of your documents finished.");
    }

    private static void prompt() {
        System.out.print(currentMode == WORKING_MODE.INDEX ? INDEX_MODE_PROMPT : QUERY_MODE_PROMPT);
    }

    private static void cannotSwitchModesWhenAddingDocuments() {
        System.out.println("You can't switch modes while adding documents.");
    }

    private static void addAnotherOrIndexAll() {
        System.out.print("Insert next or press enter to start indexing ==> ");
    }

    private enum WORKING_MODE {
        INDEX,
        QUERY
    }
}
