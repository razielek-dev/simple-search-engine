package szczegielniak.krystian.engine;

// TODO: make this a singleton which reads stopwords.txt lazily once when created
class StopWordFilter implements TokenFilter {

    private static final StopWordFilter INSTANCE = new StopWordFilter();

    private StopWordFilter() {
        // simple singleton
    }

    static StopWordFilter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean test(String token) {
        return true;
    }
}
