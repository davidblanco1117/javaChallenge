package ar.com.francoblanco.challenge.application.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class OwnExecutor {

	@Bean(name = "executorTareas")
	public Executor asyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(1000);
	    executor.setMaxPoolSize(1200);
	    executor.setQueueCapacity(1300);
	    executor.setThreadNamePrefix("Tarea-");
	    executor.initialize();
	    return executor;
	}
}
