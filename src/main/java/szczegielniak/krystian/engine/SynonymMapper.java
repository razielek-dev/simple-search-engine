package szczegielniak.krystian.engine;

import java.util.Optional;

// TODO: make this a singleton which reads synonyms.txt lazily once when created
class SynonymMapper implements TokenMapper {

    private static final SynonymMapper SYNONYM_MAPPER = new SynonymMapper();

    private SynonymMapper() {
        // simple singleton
    }

    static SynonymMapper getInstance() {
        return SYNONYM_MAPPER;
    }

    @Override
    public Optional<String> apply(String token) {
        return Optional.of(token);
    }
}
