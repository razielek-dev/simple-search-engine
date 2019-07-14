package szczegielniak.krystian.engine

import spock.lang.Specification
import szczegielniak.krystian.engine.WhitespacePunctuationTokenizer

class WhitespacePunctuationTokenizerTest extends Specification {

    def tokenizer = WhitespacePunctuationTokenizer.getInstance()

    def "should_split_on_whitespaces_and_punctuation"() {
        expect:
        tokenizer.tokenize(input) == result

        where:
        input                       || result
        'dog'                       || ['dog']
        ''                          || []
        '   \n\n   \t   \t\n      ' || []
        'dog \t DOG \n  Dog \n dOg' || ['dog', 'DOG', 'Dog', 'dOg']
        'dog                   cat' || ['dog', 'cat']
        'dog,:cat...,;"""hound'     || ['dog', 'cat', 'hound']
        ',:...,;"""\'\''            || []
    }
}
