package szczegielniak.krystian.engine;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class WhitespacePunctuationTokenizer implements Tokenizer {

    private static final String WHITESPACE_DELIMETER = "[\\p{Punct}\\s]+";

    private static final WhitespacePunctuationTokenizer INSTANCE = new WhitespacePunctuationTokenizer();

    private WhitespacePunctuationTokenizer() {
        // NO-OP: utility class
    }

    static WhitespacePunctuationTokenizer getInstance() {
        return INSTANCE;
    }

    @Override
    public List<String> tokenize(String input) {
        String[] split = input.split(WHITESPACE_DELIMETER);
        return Stream.of(split)
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }
}
