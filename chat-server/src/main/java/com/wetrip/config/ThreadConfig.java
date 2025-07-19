package com.wetrip.config;

import java.util.concurrent.Executors;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@ConditionalOnProperty(
    value = "spring.thread-executor",
    havingValue = "virtual"
)
public class ThreadConfig {

  @Bean("virtualThreadTaskExecutor")
  public TaskExecutor taskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
  }
}
