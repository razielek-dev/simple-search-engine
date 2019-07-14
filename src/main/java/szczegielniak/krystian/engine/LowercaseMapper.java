package szczegielniak.krystian.engine;

import java.util.Optional;

class LowercaseMapper implements TokenMapper {

    private static final LowercaseMapper INSTANCE = new LowercaseMapper();

    private LowercaseMapper() {
        // simple singleton
    }

    static LowercaseMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<String> apply(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(token.toLowerCase());
    }
}
