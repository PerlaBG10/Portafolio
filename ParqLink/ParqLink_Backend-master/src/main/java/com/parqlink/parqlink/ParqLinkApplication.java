package com.parqlink.parqlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ParqLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParqLinkApplication.class, args);
    }

}
