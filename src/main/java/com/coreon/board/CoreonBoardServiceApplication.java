package com.coreon.board;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.coreon.board.mapper")
public class CoreonBoardServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreonBoardServiceApplication.class, args);
    }
}
