package com.c203.altteulbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableScheduling
@EnableJpaAuditing
@EnableWebSocketMessageBroker
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AltteulBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AltteulBeApplication.class, args);
	}
}
