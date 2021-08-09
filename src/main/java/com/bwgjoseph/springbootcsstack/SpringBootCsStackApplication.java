package com.bwgjoseph.springbootcsstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Using this, we can don't use @Mapper annotation if we
// rely purely on XML configuration
// @MapperScan(basePackages = "com.bwgjoseph.springbootcsstack")
public class SpringBootCsStackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCsStackApplication.class, args);
	}

}
