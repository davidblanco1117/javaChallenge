package ar.com.francoblanco.challenge.application.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class Config {

	@Bean(name = "executorTareas")
	public Executor asyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(50);  // Hilos en reposo
	    executor.setMaxPoolSize(200);   // Máximo bajo carga
	    executor.setQueueCapacity(1000); // Cola grande para picos
	    executor.setThreadNamePrefix("Async-");
	    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	    executor.initialize();
	    return executor;
	}
}
