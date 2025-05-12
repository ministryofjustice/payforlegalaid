package uk.gov.laa.gpfd.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.unconfigurableExecutorService;
import static java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * This configuration is necessary to ensure that the authentication details are passed to any service methods marked as @Async
 *  - i.e. running in a separate thread.
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

  @Value("${async.executor.pool.size:3}")
  private int poolSize;

  @Value("${async.executor.queue.capacity:10}")
  private int queueCapacity;

  @Value("${async.executor.keep-alive.seconds:30}")
  private long keepAliveSeconds;

  @Value("${async.executor.propagate.security.context:true}")
  private boolean propagateSecurityContext;

  @Value("${async.executor.shutdown.timeout.seconds:10}")
  private int shutdownTimeoutSeconds;

  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private ThreadPoolExecutor executor;

  @PostConstruct
  public synchronized void initialize() {
    if (!initialized.get()) {
//    Use a threadpool with bounded queue capacity to limit memory use and where the calling thread gets an exception if the queue is full.
      executor = new ThreadPoolExecutor(
              poolSize,
              poolSize,
              keepAliveSeconds, TimeUnit.SECONDS,
              new ArrayBlockingQueue<>(queueCapacity),
              new CustomizableThreadFactory("async-task-"),
              new AbortPolicyWithLogging()
      );
      initialized.set(true);
      log.info("Initialized async executor with coreSize={}, maxSize={}, queueCapacity={}",
              poolSize, poolSize, queueCapacity);
    }
  }

  /**
   * Configures a bounded thread pool with:
   * - Custom thread naming for better debugging
   * - AbortPolicy to fail fast when overloaded
   * - Security context propagation (if required)
   * - Bounded queue to prevent OOM errors
   */
  @Override
  public Executor getAsyncExecutor() {
    if (!initialized.get()) {
      throw new IllegalStateException("Executor not initialized");
    }
    return propagateSecurityContext ? new DelegatingSecurityContextExecutor(executor) : executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, params) ->
            log.error("Uncaught async error in method: {}", method.getName(), throwable);
  }

  /**
   * Custom rejection policy that logs before aborting
   */
  private static final class AbortPolicyWithLogging extends AbortPolicy {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      log.warn("Task rejected - pool exhausted (active: {}, queue: {}, max: {})",
              executor.getActiveCount(),
              executor.getQueue().size(),
              executor.getMaximumPoolSize());
      super.rejectedExecution(r, executor);
    }
  }

  /**
   * Ensure graceful shutdown
   */
  @PreDestroy
  public void destroy() {
    if (executor != null) {
      log.info("Initiating async executor shutdown");
      executor.shutdown();
      try {
        if (!executor.awaitTermination(shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
          log.warn("Forcing executor shutdown with {} tasks remaining", executor.getQueue().size());
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        log.warn("Interrupted during executor shutdown");
        executor.shutdownNow();
        currentThread().interrupt();
      }
    }
  }

  /**
   * Exposes read-only view of executor for monitoring
   */
  @Bean
  public ExecutorService monitoredExecutor() {
    return unconfigurableExecutorService(executor);
  }

}