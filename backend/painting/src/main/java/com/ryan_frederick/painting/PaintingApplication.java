package com.ryan_frederick.painting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PaintingApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaintingApplication.class, args);
	}

}
