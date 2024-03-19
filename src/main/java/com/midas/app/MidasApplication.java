package com.midas.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MidasApplication {

  public static void main(String[] args) {
    SpringApplication.run(MidasApplication.class, args);
  }
}
