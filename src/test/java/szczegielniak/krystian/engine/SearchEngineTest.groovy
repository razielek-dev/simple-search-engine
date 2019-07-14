package szczegielniak.krystian.engine

import spock.lang.Specification

class SearchEngineTest extends Specification {

    def searchEngine = new SearchEngine()

    def firstDocument = 'ThE BRoWN;;,Fox   \tJuMpED::::;ovEr;  thE BroWN dOG'
    def secondDocument = 'THE LazY \'\'\'"BRown DOG saT in ThE CorNer'
    def thirdDocument = 'THE reD :::;;"foX \tBIT thE lazy DOg'
    def fourthDocument = 'BroWn:the\t hair,brown;the:eYEs,.BROwN;;the:snickeRS'

    def "should_return_proper_results_for_queries"() {
        given:
        def furtherDocuments = [secondDocument, thirdDocument]
        def queries = ['thE', 'tHE', 'ThE', 'bRoWN', 'JUmPEd', 'sAT', 'KrAKEn', 'HaiR', 'BrOwn', 'eyes']
        def initialResults = [['1'], ['1'], ['1'], ['1'], ['1'], [], [], [], ['1'], []]
        def furtherResults = [['1', '2', '3'], ['1', '2', '3'], ['1', '2', '3'], ['1', '2'], ['1'], ['2'], [], [], ['1', '2'], []]
        def resultsAfterIndexingNewDocument = [['1', '2', '3', '4'], ['1', '2', '3', '4'], ['1', '2', '3', '4'], ['4', '1', '2'], ['1'], ['2'], [], ['4'], ['4', '1', '2'], ['4']]

        expect:
        searchEngine.index([firstDocument])
        queries.eachWithIndex { query, index ->
            assert searchEngine.query(query) == initialResults[index]
        }

        searchEngine.index(furtherDocuments)
        queries.eachWithIndex { query, index ->
            assert searchEngine.query(query) == furtherResults[index]
        }

        searchEngine.index([fourthDocument])
        queries.eachWithIndex { query, index ->
            assert searchEngine.query(query) == resultsAfterIndexingNewDocument[index]
        }
    }
}
