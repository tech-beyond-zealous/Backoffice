package com.gosmart.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackOfficeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackOfficeApplication.class, args);
    }
}
