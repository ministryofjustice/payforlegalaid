package uk.gov.laa.gpfd.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

/** This configuration is necessary to ensure that the authentication details are passed to any service methods marked as @Async
 *  - i.e. running in a separate thread.
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
  @Override
  public Executor getAsyncExecutor() {
    int poolSize = 3;
    int queueCapacity = 10;
    //Use a threadpool with bounded queue capacity to limit memory use and where the calling thread gets an exception if the queue is full.
    ExecutorService executor = new ThreadPoolExecutor(
        poolSize,
        poolSize,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(queueCapacity),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()
    );
    return new DelegatingSecurityContextExecutor(executor);
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, params) ->
        log.error("Uncaught async error in method: {}", method.getName(), throwable);
  }
}