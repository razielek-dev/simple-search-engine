package szczegielniak.krystian.engine;

import java.util.Collections;
import java.util.List;

public class SearchEngine {
    /*
        For the sake of simplicity I will be using equivalent InputParser instances
        for both parsing the queries and indexing the documents.

        Different rules can be applied by simply providing different constructor parameters
        for InputParser objects used respectively for query parsing and document indexing.
     */
    private static final Tokenizer TOKENIZER = WhitespacePunctuationTokenizer.getInstance();
    private static final List<TokenFilter> FILTERS = Collections.emptyList();
    private static final List<TokenMapper> MAPPERS = Collections.singletonList(LowercaseMapper.getInstance());

    private final InputParser queryParser = new InputParser(TOKENIZER, FILTERS, MAPPERS);
    private final InputParser documentParser = new InputParser(TOKENIZER, FILTERS, MAPPERS);

    private static final Index index = Index.getInstance();

    public void index(List<String> documents) {
        for (String document : documents) {
            index(document);
        }
    }

    private void index(String document) {
        System.out.println("Indexing document: { " + document + " }.");
        List<String> tokens = documentParser.parse(document);
        System.out.println("After parsing: { " + String.join(" ", tokens) + " }.");
        index.index(tokens);
        System.out.println("Document indexed with ID: " + index.getSize() + ".\n");
    }

    public List<String> query(String term) {
        List<String> parsedInput = queryParser.parse(term);
        return index.retrieve(parsedInput);
    }
}
