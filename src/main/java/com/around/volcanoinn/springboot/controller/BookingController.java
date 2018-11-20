package com.around.volcanoinn.springboot.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.service.CampsiteService;
import com.around.volcanoinn.springboot.util.CustomErrorType;


@RestController
@RequestMapping("/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    CampsiteService service;

    /* RETRIEVE ALL BOOKINGS */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllBooking(UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        List<Booking> bookings = service.getAllBookings();
        headers.setLocation(ucBuilder.path("/booking").buildAndExpand().toUri());
        return new ResponseEntity<List<Booking>>(bookings, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{bookingId}", method = RequestMethod.GET)
    public ResponseEntity<?> getBooking(
        @PathVariable(value="bookingId") UUID bookingId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsBooking(bookingId)) {
            String errorStr = "Booking not found";
            CustomErrorType error = new CustomErrorType(errorStr);
            logger.error(errorStr);
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        } else {
            Booking booking = service.getBooking(bookingId);
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
            return new ResponseEntity<Booking>(booking, headers, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{bookingId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBooking(
        @RequestBody Booking booking,
        @PathVariable(value="bookingId") UUID bookingId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsBooking(bookingId)) {
            CustomErrorType error = new CustomErrorType("Booking not found");
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        } else {
            try {
                service.updateBooking(booking, bookingId);
                headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
                return new ResponseEntity<Booking>(booking, headers, HttpStatus.OK);
            } catch (RuntimeException r) {
                String errStr = "Unable to update booking, cause => " + r.getMessage();
                logger.error(errStr);
                CustomErrorType error =  new CustomErrorType(errStr);
                return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
            }
        }
    }

    @RequestMapping(value = "/{bookingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBooking(@PathVariable(value="bookingId") UUID bookingId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsBooking(bookingId)) {
            CustomErrorType error = new CustomErrorType("Booking not found");
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        }	else {
            service.deleteBooking(bookingId);
            CustomErrorType msg = new CustomErrorType("Booking deleted");
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(bookingId).toUri());
            return new ResponseEntity<CustomErrorType>(msg, headers, HttpStatus.OK);
        }
    }
}