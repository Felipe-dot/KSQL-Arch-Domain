package com.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.infrastructure")
public class InfraStructureApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfraStructureApplication.class);
    }
}
