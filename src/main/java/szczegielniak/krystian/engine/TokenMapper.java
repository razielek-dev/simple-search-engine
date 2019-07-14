package szczegielniak.krystian.engine;

import java.util.Optional;
import java.util.function.Function;

/**
 * Transforms a token according to a rule and returns the result.
 * If the results turns out to be an empty String or null, returns Optional.empty().
 */
interface TokenMapper extends Function<String, Optional<String>> {
}
