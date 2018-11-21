package com.around.volcanoinn.springboot.controller;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.entity.Campsite;
import com.around.volcanoinn.springboot.entity.Guest;
import com.around.volcanoinn.springboot.util.ConcurrentRequestGenerator;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VolcanoInnTest {

    // Constants
    @LocalServerPort
    protected static final int serverPort = 8080;
    private static final int NUM_POOL_THREADS = 10;
    private static final int NUM_PARALLEL_REQUESTS = 10;

    private static final String REST_SERVICE_URI = "http://localhost:".concat(Integer.toString(serverPort));

    @Test
    public void inExistingCampsite() {

        RestTemplate restTemplate = new RestTemplate();
        //Not existing campsite.
        String url = REST_SERVICE_URI + "/campsite/99";
        Campsite campsite = restTemplate.getForObject(url, Campsite.class);
        assertNull(campsite);
    }

    @Test
    public void concurrentBookings() throws Exception {

        // The booking to be issued in parallel
        Long arrivalDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Booking booking = getAssignedBooking(arrivalDate, departureDate);
        ConcurrentRequestGenerator generator = new ConcurrentRequestGenerator(NUM_POOL_THREADS, NUM_PARALLEL_REQUESTS, booking, url);
        generator.run();
    }

    @Test
    public void businessRulesMax3Days() {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Booking booking = getAssignedBooking(arrivalDate, departureDate);
        URI bookingUri = getBookingUri(restTemplate, url, booking);
        assertNull(bookingUri);
    }

    @Test
    public void businessRulesArrivalDateToday() {

        Long arrivalDate = Instant.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Booking booking = getAssignedBooking(arrivalDate, departureDate);
        URI bookingUri = getBookingUri(restTemplate, url, booking);
        assertNull(bookingUri);
    }

    @Test
    public void businessRulesArrivalDateMoreThanOneMonthAhead() {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(34).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Booking booking = getAssignedBooking(arrivalDate, departureDate);
        URI bookingUri = getBookingUri(restTemplate, url, booking);
        assertNull(bookingUri);
    }

    @Test
    public void businessRulesDepartureDateBeforeArrivalDate() {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Booking booking = getAssignedBooking(arrivalDate, departureDate);
        URI bookingUri = getBookingUri(restTemplate, url, booking);
        assertNull(bookingUri);
    }

    private URI getBookingUri(RestTemplate restTemplate, String url, Booking booking) {
        return restTemplate.postForLocation(url, booking, Booking.class);
    }

    private Booking getAssignedBooking(Long arrivalDate, Long departureDate) {
        Guest guest = getGuest();
        Booking booking = getBooking(arrivalDate, departureDate);
        booking.setGuest(guest);
        return booking;
    }

    private Booking getBooking(Long arrivalDate, Long departureDate) {
        return new Booking(arrivalDate, departureDate);
    }

    private Guest getGuest() {
        return new Guest("Guest Name", "guest.email@gmail.com");
    }

}