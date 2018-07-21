package com.jhinesconsulting.kafkatodoapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaTodoApiApplication implements CommandLineRunner {
	@Autowired
    TodoEventRecordConsumer todoEventRecordConsumer;

	public static void main(String[] args) {
		SpringApplication.run(KafkaTodoApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		todoEventRecordConsumer.poll();
	}
}
