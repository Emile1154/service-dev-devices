package ru.emiljan.servicedevdevices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author EM1LJAN
 */
@SpringBootApplication
public class ServiceDevDevicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceDevDevicesApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
