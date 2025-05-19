package uk.gov.laa.gpfd.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.poi.openxml4j.util.ZipSecureFile.getMinInflateRatio;
import static org.apache.poi.openxml4j.util.ZipSecureFile.setMinInflateRatio;

/**
 * A functional interface that defines a transformation policy for {@link AutoCloseable} resources,
 * used to add security layer to input streams.
 *
 * @param <T> the type of resource to transform, must extend {@link AutoCloseable}
 * @see InputStream
 * @see AutoCloseable
 */
@FunctionalInterface
public interface SecurityPolicy<T extends AutoCloseable> {

    /**
     * Creates a ZIP bomb protection policy for input streams that limits the inflation ratio.
     * The policy will automatically restore the original ratio when the stream is closed.
     *
     * @param ratio the minimum allowed compression ratio (e.g., 0.001 for 0.1%)
     * @return a configured ZIP bomb protection policy
     * @throws IllegalArgumentException if ratio is not positive
     */
    static SecurityPolicy<InputStream> zipBombProtection(double ratio) {
        return (InputStream input) -> {
            var original = getMinInflateRatio();
            setMinInflateRatio(ratio);
            return new FilterInputStream(input) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        setMinInflateRatio(original);
                    }
                }
            };
        };
    }

    /**
     * Applies the security policy to the input resource, returning a transformed version.
     * The returned resource should properly handle cleanup when closed.
     *
     * @param input the input resource to transform
     * @return the transformed resource with policy applied
     * @throws IOException          if the policy cannot be applied or the input is invalid
     * @throws NullPointerException if the input is null
     */
    T apply(T input) throws IOException;

    /**
     * Composes this policy with another policy to create a combined transformation.
     * The resulting policy will apply this policy first, then the next policy.
     *
     * @param next the policy to apply after this one
     * @return a new composed policy
     * @throws NullPointerException if the next policy is null
     */
    default SecurityPolicy<T> compose(SecurityPolicy<T> next) {
        return (T input) -> next.apply(this.apply(input));
    }
}