package szczegielniak.krystian.engine

import spock.lang.Specification
import szczegielniak.krystian.engine.InputParser
import szczegielniak.krystian.engine.TokenFilter
import szczegielniak.krystian.engine.TokenMapper
import szczegielniak.krystian.engine.WhitespacePunctuationTokenizer

class InputParserTest extends Specification {

    def tokenizer = WhitespacePunctuationTokenizer.getInstance()
    def filters = [new DoesNotStartWithATestTokenFilter(), new DoesNotEndWithATestTokenFilter()]
    def mappers = [new CutFirstLetterTestTokenMapper(), new AppendSuperTestTokenMapper()]

    def testParser = new InputParser(tokenizer, filters, mappers)

    def "should_parse_the_input_properly"() {
        expect:
        result == testParser.parse(input)

        where:
        input                            || result
        'abba:..;;\t\t\nabcd dcba b c'   || []
        'abba:..;;\t\t\nabcd dcba bc cd' || ['csuper', 'dsuper']
        'dog'                            || ['ogsuper']
    }


    private class DoesNotStartWithATestTokenFilter implements TokenFilter {
        @Override
        boolean test(String token) {
            return !token.startsWith('a')
        }
    }

    private class DoesNotEndWithATestTokenFilter implements TokenFilter {
        @Override
        boolean test(String token) {
            return !token.endsWith('a')
        }
    }

    private class AppendSuperTestTokenMapper implements TokenMapper {

        @Override
        Optional<String> apply(String token) {
            token = token ?: ""
            return Optional.of(token + 'super')
        }
    }

    private class CutFirstLetterTestTokenMapper implements TokenMapper {

        @Override
        Optional<String> apply(String token) {
            if (token == null || token.size() < 2) {
                return Optional.empty()
            }
            return Optional.of(token.substring(1))
        }
    }
}
