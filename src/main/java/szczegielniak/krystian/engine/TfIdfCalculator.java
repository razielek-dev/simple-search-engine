package szczegielniak.krystian.engine;

/*
    Calculates the TF-IDF value.
    idf = log(N / n) where:
        - N - number of documents
        - n - number of documents in which the term appears
    I am using the natural logarithm (base of 'e').
 */
final class TfIdfCalculator {
    static double calculate(long termFrequencyInADocument, long numberOfDocuments,
            long numberOfDocumentsInWhichTheTermAppears) {
        double idf = Math.log((double)numberOfDocuments / numberOfDocumentsInWhichTheTermAppears);
        return termFrequencyInADocument * idf;
    }
}
