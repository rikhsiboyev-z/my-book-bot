package org.example.mybooksbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MyBooksBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyBooksBotApplication.class, args);
	}

}
