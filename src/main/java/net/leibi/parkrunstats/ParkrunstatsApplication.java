package net.leibi.parkrunstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ParkrunstatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkrunstatsApplication.class, args);
    }

}
