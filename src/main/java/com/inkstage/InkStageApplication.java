package com.inkstage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.inkstage.mapper")
public class InkStageApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkStageApplication.class, args);
    }

}
