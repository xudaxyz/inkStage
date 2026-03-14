package com.inkstage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
@EnableCaching
@MapperScan("com.inkstage.mapper")
public class InkStageApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(InkStageApplication.class, args);
        } catch (Exception e) {
            log.warn("Start Service Error: {}", e.getMessage());
        }
    }

}
