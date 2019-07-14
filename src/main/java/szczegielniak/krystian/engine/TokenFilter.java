package szczegielniak.krystian.engine;

import java.util.function.Predicate;

/**
 * Checks if a given token should be part of the final result.
 */
interface TokenFilter extends Predicate<String> {
}
