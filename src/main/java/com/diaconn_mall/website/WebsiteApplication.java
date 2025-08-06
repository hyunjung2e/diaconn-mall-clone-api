package com.diaconn_mall.website;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;


@SpringBootApplication
@ComponentScan(basePackages = "com.diaconn_mall.website")
public class WebsiteApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebsiteApplication.class, args);
	}

	// 타임존을 KST로 지정
	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	@Bean
	CommandLineRunner checkBeans(ApplicationContext ctx) {
		return args -> {
			System.out.println("@@@@@@@ Spring 서버를 시작합니다");
			for (String beanName : ctx.getBeanDefinitionNames()) {
				if (beanName.contains("controller")) {
					System.out.println(beanName);
				}
			}
		};
	}
}



