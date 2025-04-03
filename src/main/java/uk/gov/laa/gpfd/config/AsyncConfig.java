package uk.gov.laa.gpfd.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

/** This configuration is necessary to ensure that the authentication details are passed to any service methods marked as @Async
 *  - i.e. running in a separate thread.
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {
  @Override
  public Executor getAsyncExecutor() {
    return new DelegatingSecurityContextExecutor(Executors.newFixedThreadPool(3));
  }
}