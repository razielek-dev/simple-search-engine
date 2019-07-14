package szczegielniak.krystian.engine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An InputParser transforms the input to a List of Strings (tokens), which are initially split using a provided
 * Tokenizer implementation, filtered using a List of provided TokenFilter implementations and finally transformed
 * using a List of provided TokenMapper implementations.
 */
class InputParser {

    private final Tokenizer tokenizer;
    private final List<TokenFilter> filters;
    private final List<TokenMapper> mappers;

    InputParser(Tokenizer tokenizer, List<TokenFilter> filters, List<TokenMapper> mappers) {
        this.tokenizer = tokenizer;
        this.filters = filters;
        this.mappers = mappers;
    }

    /**
     * Performs the following:
     * - splits the input to tokens,
     * - filters the tokens using provided filters,
     * - transforms each token using provided mappers.
     */
    List<String> parse(String input) {
        return applyMappers(applyFilters(tokenizer.tokenize(input)));
    }

    private List<String> applyFilters(List<String> tokens) {
        for (TokenFilter filter : filters) {
            tokens = tokens.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }
        return tokens;
    }

    private List<String> applyMappers(List<String> tokens) {
        for (TokenMapper mapper : mappers) {
            tokens = tokens.stream()
                    .map(mapper)
                    .flatMap(tokenOpt -> tokenOpt.map(Stream::of).orElseGet(Stream::empty))
                    .collect(Collectors.toList());
        }
        return tokens;
    }
}
