package szczegielniak.krystian.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class Index {

    private static final Index INSTANCE = new Index();

    /*
        This map stores information if the index for a given term was updated recently.
        It will help to avoid unnecessary sorting operations whenever a user queries for an unchanged term.
     */
    private static final Map<String, Boolean> WAS_TERM_UPDATED_RECENTLY_MAP = new HashMap<>();

    /*
        This is the main storage for the indexed data.
     */
    private static final Map<String, List<DocumentIndexEntryForTerm>> INVERTED_INDEX = new HashMap<>();

    private long documentCount = 0L;

    private Index() {
        // simple singleton
    }

    static Index getInstance() {
        return INSTANCE;
    }

    long getSize() {
        return documentCount;
    }

    void index(List<String> tokens) {
        /*
            I start with incrementing the count.
            This value will be used as documentId and ids will start from 1.
         */
        documentCount++;

        // Map terms to their counts in the tokenized and filtered input
        Map<String, Long> termsFrequenciesInNewDocument = collectAndCountTermsFromTokens(tokens);

        /*
            Mark each term as updated recently, so new weight calculations
            and sorting will be performed the next time those any of those terms is queried.
         */
        markTermsAsRecentlyUpdated(termsFrequenciesInNewDocument.keySet());

        /*
            For each term add information about it's frequency in a Document to the Index
            under the key which is the given term.
         */
        addTermsWithTheirFrequenciesToIndex(termsFrequenciesInNewDocument, documentCount);
    }

    private Map<String, Long> collectAndCountTermsFromTokens(List<String> tokens) {
        return tokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private void markTermsAsRecentlyUpdated(Set<String> terms) {
        terms.forEach(term -> WAS_TERM_UPDATED_RECENTLY_MAP.put(term, true));
    }

    private void addTermsWithTheirFrequenciesToIndex(Map<String, Long> termsFrequenciesInNewDocument, Long documentId) {
        for (Map.Entry<String, Long> termFrequencyInNewDocument : termsFrequenciesInNewDocument.entrySet()) {
           /*
                Initially I set all new weights to 0L as they will be calculated only
                after a user queries for a term which was recently updated.
            */
            DocumentIndexEntryForTerm documentIndexEntryForTerm =
                    new DocumentIndexEntryForTerm(documentId, termFrequencyInNewDocument.getValue(), 0L);

            String term = termFrequencyInNewDocument.getKey();
            // Create a new list in the Index if the term is new
            if (!INVERTED_INDEX.containsKey(term)) {
                INVERTED_INDEX.put(term, new ArrayList<>());
            }
            addNewDocumentIndexEntryForTermToTheIndex(documentIndexEntryForTerm, term);
        }
    }

    private void addNewDocumentIndexEntryForTermToTheIndex(
            DocumentIndexEntryForTerm documentIndexEntryForTerm, String term) {
        List<DocumentIndexEntryForTerm> documentIndexEntryForTerms = INVERTED_INDEX.get(term);
        documentIndexEntryForTerms.add(documentIndexEntryForTerm);
    }

    List<String> retrieve(List<String> parsedInput) {
        // if all tokens were filtered out during parsing, return an empty list
        if (parsedInput == null || parsedInput.isEmpty()) {
            return Collections.emptyList();
        }
        // TODO: at this point I only support single term queries
        String queriedTerm = parsedInput.get(0);

        // The term may not be present in the Index
        if (!INVERTED_INDEX.containsKey(queriedTerm)) {
            return Collections.emptyList();
        }

        if (WAS_TERM_UPDATED_RECENTLY_MAP.get(queriedTerm)) {
            /*
                The Index entry (list) for a given term changed recently because the Index was updated.
                New weights have to be calculated for each element of the list,
                the list has to be sorted according to calculated weights and
                only then used to construct the result for the client.
             */
            return retrieveResultsForTermForWhichTheIndexWasRecentlyUpdated(queriedTerm);
        } else {
            /*
                Documents for the term are sorted properly because the Index was not recently updated.
                We can simply use a List for a given term to construct the final result.
             */
            return retrieveResultsForTermForWhichTheIndexWasNotModified(queriedTerm);
        }
    }

    private List<String> retrieveResultsForTermForWhichTheIndexWasRecentlyUpdated(String queriedTerm) {
        List<DocumentIndexEntryForTerm> weightedAndSortedEntries = INVERTED_INDEX.get(queriedTerm)
                .stream()
                .map(documentIndexEntryForTerm -> createEntryWithUpdatedWeight(queriedTerm, documentIndexEntryForTerm))
                // If weights are equal I am sorting according to the order in which documents were initially added.
                .sorted(Comparator.comparingDouble(DocumentIndexEntryForTerm::getWeight).reversed()
                        .thenComparing(DocumentIndexEntryForTerm::getId))
                .collect(Collectors.toList());

        // Update the Index with reweighted and resorted entries
        INVERTED_INDEX.put(queriedTerm, weightedAndSortedEntries);
        // Mark the term as not recently updated
        WAS_TERM_UPDATED_RECENTLY_MAP.put(queriedTerm, false);

        return weightedAndSortedEntries.stream()
                .map(DocumentIndexEntryForTerm::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    private DocumentIndexEntryForTerm createEntryWithUpdatedWeight(
            String queriedTerm, DocumentIndexEntryForTerm documentIndexEntryForTerm) {
        // Calculate new weight for an entry according to Tf-Idf rule
        double recalculatedTfIdfWeight = TfIdfCalculator.calculate(
                documentIndexEntryForTerm.termFrequencyInADocument,
                documentCount,
                (long) INVERTED_INDEX.get(queriedTerm).size());

       return new DocumentIndexEntryForTerm(documentIndexEntryForTerm.id,
                documentIndexEntryForTerm.termFrequencyInADocument,
                recalculatedTfIdfWeight);
    }

    private List<String> retrieveResultsForTermForWhichTheIndexWasNotModified(String queriedTerm) {
        return INVERTED_INDEX.get(queriedTerm)
                .stream()
                .map(documentIndexEntryForTerm -> documentIndexEntryForTerm.id)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    /*
        This inner POJO class holds information for each Document Entry in an Index for a given Term.
        This information is used to:
            - return the document id to the client
            - calculate weight using the TF-IDF rule
            - sort entries using calculated weight
     */
    private class DocumentIndexEntryForTerm {
        private long id;
        private long termFrequencyInADocument;
        private double weight;

        DocumentIndexEntryForTerm(long id, long termFrequencyInADocument, double weight) {
            this.id = id;
            this.termFrequencyInADocument = termFrequencyInADocument;
            this.weight = weight;
        }

        long getId() {
            return id;
        }

        double getWeight() {
            return weight;
        }
    }
}
