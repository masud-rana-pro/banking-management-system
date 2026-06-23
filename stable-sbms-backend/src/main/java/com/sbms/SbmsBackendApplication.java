package com.sbms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication(scanBasePackages = "com.sbms")
@ComponentScan(basePackages = "com.sbms")
@EntityScan(basePackages = {"com.sbms"})
@EnableJpaRepositories(basePackages = {"com.sbms"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class SbmsBackendApplication {
    @Bean(name = "verificationDeliveryExecutor")
    public Executor verificationDeliveryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("otp-mail-");
        executor.initialize();
        return executor;
    }

	public static void main(String[] args) {
		SpringApplication.run(SbmsBackendApplication.class, args);
	}

}

