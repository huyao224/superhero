package com.next.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//定时任务功能开启
@EnableScheduling
//扫描mappers路径
@MapperScan(basePackages = "com.next.mapper")
//组件扫描
@ComponentScan(basePackages = {"com.next","org.n3r.idworker"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
