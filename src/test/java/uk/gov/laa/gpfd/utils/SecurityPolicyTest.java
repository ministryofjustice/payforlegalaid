package uk.gov.laa.gpfd.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityPolicyTest {

    @Test
    @SneakyThrows
    void shouldZipBombProtectionApplyValidPolicy() {
        var testRatio = 0.001;
        var mockInput = new ByteArrayInputStream("test".getBytes());

        var protectedStream = SecurityPolicy.zipBombProtection(testRatio).apply(mockInput);

        assertNotNull(protectedStream, "Should return a protected stream");
    }

    @Test
    @SneakyThrows
    void shouldComposeApplyPoliciesInOrder() {
        boolean[] firstApplied = {false};
        boolean[] secondApplied = {false};

        SecurityPolicy<InputStream> firstPolicy = input -> {
            firstApplied[0] = true;
            assertFalse(secondApplied[0], "First policy should run before second");
            return input;
        };

        SecurityPolicy<InputStream> secondPolicy = input -> {
            secondApplied[0] = true;
            assertTrue(firstApplied[0], "Second policy should run after first");
            return input;
        };

        firstPolicy.compose(secondPolicy)
                .apply(new ByteArrayInputStream("test".getBytes()));

        assertTrue(firstApplied[0] && secondApplied[0],
                "Both policies should be applied");
    }

}