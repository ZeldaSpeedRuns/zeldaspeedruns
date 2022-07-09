package com.zeldaspeedruns.zeldaspeedruns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ZeldaSpeedRunsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZeldaSpeedRunsApplication.class, args);
	}
}
