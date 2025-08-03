package com.sloyardms.trackerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TrackerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackerApiApplication.class, args);
    }

}
