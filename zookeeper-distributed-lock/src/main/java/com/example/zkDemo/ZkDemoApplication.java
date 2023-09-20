package com.example.zkDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
//@ComponentScans()
public class ZkDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZkDemoApplication.class, args);
	}

}
