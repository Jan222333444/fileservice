package de.dhbw.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@SpringBootApplication
public class FileserviceApplication {


    public static void main(String[] args) {
        SpringApplication.run(FileserviceApplication.class, args);
    }

}
