package com.ryan_frederick.painting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PaintingApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaintingApplication.class, args);
	}
	@GetMapping("/")
	public String helloWorld() {
		return "<h1>Hello World!</h1>";
	}

}
