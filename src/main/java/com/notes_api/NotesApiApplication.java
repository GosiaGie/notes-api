package com.notes_api;

import com.notes_api.security.jtw.JwtProperties;
import com.notes_api.security.bucket4j.RateLimitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, RateLimitProperties.class})
public class NotesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotesApiApplication.class, args);
	}

}
