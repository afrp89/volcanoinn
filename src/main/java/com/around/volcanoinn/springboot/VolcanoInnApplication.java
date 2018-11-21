package com.around.volcanoinn.springboot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.IntStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.entity.Campsite;
import com.around.volcanoinn.springboot.entity.Guest;
import com.around.volcanoinn.springboot.repository.BookingRepository;
import com.around.volcanoinn.springboot.repository.CampsiteRepository;
import com.around.volcanoinn.springboot.repository.GuestRepository;

@SpringBootApplication(scanBasePackages = {"com.around.volcanoinn.springboot"})
@Configuration
public class VolcanoInnApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "true");
        SpringApplication.run(VolcanoInnApplication.class, args);
    }

    @Bean
    public CommandLineRunner poc(CampsiteRepository campsiteRepository, GuestRepository guestRepository,
                                 BookingRepository bookingRepository) {
        return (args) -> {
            // Fake data
            IntStream.range(0, 0).mapToObj(Integer::toString).forEach(str -> {
                Campsite campsite = new Campsite();
                campsite.setName("Kilawea#".concat(str));
                campsiteRepository.save(campsite);
                Guest guest = new Guest();
                guest.setName("Guest #".concat(str));
                guest.setEmail("guest".concat(str.concat("@Mail.com")));
                guestRepository.save(guest);
                Booking booking = new Booking();
                booking.setCampsite(campsite);
                booking.setGuest(guest);
                Date toDay = new Date();
                Instant afterTomorrow = toDay.toInstant().plus(2, ChronoUnit.DAYS);
                booking.setArrivalDate(toDay.toInstant().toEpochMilli());
                booking.setDepartureDate(afterTomorrow.toEpochMilli());
                bookingRepository.save(booking);
            });
        };
    }
}