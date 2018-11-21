package com.around.volcanoinn.springboot.controller;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.entity.Campsite;
import com.around.volcanoinn.springboot.entity.Guest;
import com.around.volcanoinn.springboot.util.ConcurrentRequestGenerator;


@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VolcanoInnTest {
    public static final String REST_SERVICE_URI = "http://localhost:8080";

    private static final Logger logger = LoggerFactory.getLogger(VolcanoInnTest.class);

    @Test
    @SuppressWarnings("unchecked")
    public void aaNoCampsiteIdShouldReturnAllCampsitesAvailability() throws Exception {
        // Check availability starting from tomorrow
        Long fromDate = Instant.from(LocalDate.now().plusDays(11).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        // And to three days ahead
        Long toDate = Instant.from(LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();
        List<LinkedHashMap<String, Object>> campsitesMap = restTemplate.getForObject(url, List.class);
        if (campsitesMap != null) {
            // Two campsites were created
            assertTrue(campsitesMap.size() == 2);
            // Available days for the two campsites should be 3 because there are no bookings
            List<String> avaliableDays1 = (List<String>) campsitesMap.get(0).get("availableDays");
            List<String> avaliableDays2 = (List<String>) campsitesMap.get(1).get("availableDays");
            assertTrue(avaliableDays1.size() == 3);
            assertTrue(avaliableDays2.size() == 3);
        } else {
            logger.info("No campsites exist");
        }
    }

    @Test
    public void abGetFirstCampsiteAvailabilityBetweenDates() throws Exception {

        // Check availability starting from tomorrow
        Long fromDate = Instant.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        // And to three days ahead
        Long toDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();

        Campsite campsite = restTemplate.getForObject(url, Campsite.class);
        // Campsite must exist
        assertTrue(campsite != null);
        // Three available days because there are no bookings
        assertTrue(campsite.getAvailableDays().size() == 3);
    }

    @Test
    public void acGetNotExistingCampsite() throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/12";
        Campsite campsite = restTemplate.getForObject(url, Campsite.class);
        // Campsite must not exist
        assertTrue(campsite == null);
    }

    @Test
    public void adMakeSuccessfulBooking() throws Exception {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        // Retrieve created booking
        booking = restTemplate.getForObject(bookingUri, Booking.class);
        // Check contents
        assertTrue(booking != null);
        assertTrue(booking.getGuest().getEmail().equals("guest.email@gmail.com"));
        assertTrue(booking.getGuest().getName().equals("Guest Name"));
        assertTrue(booking.getArrivalDate().equals(arrivalDate));
        assertTrue(booking.getDepartureDate().equals(departureDate));
        restTemplate.delete(bookingUri);
    }

    @Test
    public void aeUpdateBooking() throws Exception {

        // Create booking to be updated
        Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        // Retrieve created booking
        booking = restTemplate.getForObject(bookingUri, Booking.class);
        // Create new values to update the created booking
        Long updatedArrivalDate = Instant.from(LocalDate.now().plusDays(11).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long updatedDepartureDate = Instant.from(LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        String updatedEmail = "UPDATED.BOOKING@gmail.com";
        String updatedFullName = "Guest Name UPDATED";
        Guest updatedGuest = new Guest(updatedFullName, updatedEmail);
        booking.setGuest(updatedGuest);
        booking.setArrivalDate(updatedArrivalDate);
        booking.setDepartureDate(updatedDepartureDate);
        restTemplate.put(bookingUri, booking);
        // Retrieve updated booking
        booking = restTemplate.getForObject(bookingUri, Booking.class);
        // Check contents
        assertTrue(booking != null);
        assertTrue(booking.getGuest().getEmail().equals(updatedEmail));
        assertTrue(booking.getGuest().getName().equals(updatedFullName));
        assertTrue(booking.getArrivalDate().equals(updatedArrivalDate));
        assertTrue(booking.getDepartureDate().equals(updatedDepartureDate));
        restTemplate.delete(bookingUri);
    }

    @Test
    public void afMakeConcurrentBookings() throws Exception {
        // Constants
        final int NUM_POOL_THREADS = 10;
        final int NUM_PARALLEL_REQUESTS = 10;
        // The booking to be issued in parallel
        Long arrivalDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        ConcurrentRequestGenerator generator = new ConcurrentRequestGenerator(NUM_POOL_THREADS, NUM_PARALLEL_REQUESTS, booking, url);
        generator.run();
    }

    @Test
    public void agCancelBooking() throws Exception {

        // Create booking to be deleted
        Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        // Cancel booking
        restTemplate.delete(bookingUri);
        // Verify cancellation
        booking = restTemplate.getForObject(bookingUri, Booking.class);
        assertTrue(booking == null);
    }

    @Test
    public void ahTryBookingMoreThanThreeDays() throws Exception {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        assertTrue(bookingUri == null);
    }

    @Test
    public void aiTryBookingArrivalDateToday() throws Exception {

        Long arrivalDate = Instant.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        assertTrue(bookingUri == null);
    }

    @Test
    public void ajTryReseveArrivalDateMoreThanOneMonthAhead() throws Exception {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(34).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        assertTrue(bookingUri == null);
    }

    @Test
    public void akTryBookingDepartureDateBeforeArrivalDate() throws Exception {

        Long arrivalDate = Instant.from(LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        Long departureDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_SERVICE_URI + "/campsite/1/booking";
        Guest guest = new Guest("Guest Name", "guest.email@gmail.com");
        Booking booking = new Booking(arrivalDate, departureDate);
        booking.setGuest(guest);
        URI bookingUri = restTemplate.postForLocation(url, booking, Booking.class);
        assertTrue(bookingUri == null);
    }
}