package com.ryan_frederick.painting;

import com.ryan_frederick.painting.config.RsaKeyProperties;
import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class PaintingApplication {

    public static void main(String[] args) {
		SpringApplication.run(PaintingApplication.class, args);
	}
}
