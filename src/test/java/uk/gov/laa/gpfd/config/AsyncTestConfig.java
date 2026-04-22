package uk.gov.laa.gpfd.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@TestConfiguration
@EnableAsync
public class AsyncTestConfig {

    @Bean
    public Executor executor() {
        // Force tests to execute async immediately so can test its called
        return new SyncTaskExecutor();
    }

}
