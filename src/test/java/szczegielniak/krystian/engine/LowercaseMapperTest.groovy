package szczegielniak.krystian.engine

import spock.lang.Specification
import szczegielniak.krystian.engine.LowercaseMapper

import java.util.stream.Collectors
import java.util.stream.Stream

class LowercaseMapperTest extends Specification {

    def lowercaseMapper = LowercaseMapper.getInstance()

    def "should_properly_map_to_lowercase_and_comply_to_token_mapper_contract"() {
        expect:
        result == input.stream()
                .map(lowercaseMapper)
                .flatMap({ opt -> opt.isPresent() ? Stream.of(opt.get()) : Stream.empty() })
                .collect(Collectors.toList())

        where:
        input                                  || result
        ['cat', 'CaT', 'CAT', 'caT', null, ''] || ['cat', 'cat', 'cat', 'cat']
    }

}
