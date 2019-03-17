package com.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 11:41
 */
@SpringBootApplication
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }
}
