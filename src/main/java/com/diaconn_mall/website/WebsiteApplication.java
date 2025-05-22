package com.diaconn_mall.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.diaconn_mall.website")
public class WebsiteApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebsiteApplication.class, args);
	}

	@Bean
	CommandLineRunner checkBeans(ApplicationContext ctx) {
		return args -> {
			System.out.println(">>> 등록된 빈 목록 (controller 포함 여부 확인):");
			for (String beanName : ctx.getBeanDefinitionNames()) {
				if (beanName.contains("controller")) {
					System.out.println(beanName);
				}
			}
		};
	}
}



