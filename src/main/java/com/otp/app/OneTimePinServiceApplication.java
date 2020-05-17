package com.otp.app;

import com.otp.props.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages={"com.otp"})
@EntityScan("com.otp.entity")
@EnableJpaRepositories("com.otp.dao")
public class OneTimePinServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneTimePinServiceApplication.class, args);
	}

}
