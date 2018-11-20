package com.around.volcanoinn.springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.around.volcanoinn.springboot.entity.Campsite;
import com.around.volcanoinn.springboot.repository.CampsiteRepository;

@SpringBootApplication(scanBasePackages = {"com.around.volcanoinn.springboot"})
public class VolcanoInnApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "true");
        SpringApplication.run(VolcanoInnApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CampsiteRepository campsiteRepository) {
        return (args) -> {
            // Create the campsite
            Campsite campsite = new Campsite();
            campsite.setName("Kilawea");
            campsiteRepository.save(campsite);

            Campsite campsite2 = new Campsite();
            campsite2.setName("Fuji");
            campsiteRepository.save(campsite2);
        };
    }
}