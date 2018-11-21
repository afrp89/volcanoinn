package com.around.volcanoinn.springboot.controller;

import java.util.List;
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
import com.around.volcanoinn.springboot.entity.Guest;
import com.around.volcanoinn.springboot.service.GuestService;
import com.around.volcanoinn.springboot.util.CustomErrorType;


@RestController
@RequestMapping("/guest")
public class GuestController {

    private static final Logger logger = LoggerFactory.getLogger(GuestController.class);

    @Autowired
    GuestService service;

    /* RETRIEVE ALL GUESTS */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllGuest(UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        List<Guest> guests = service.getAllGuests();
        headers.setLocation(ucBuilder.path("/guest").buildAndExpand().toUri());
        return getResponseEntity(headers, guests);
    }

    private ResponseEntity<?> getResponseEntity(HttpHeaders headers, List<Guest> guests) {
        return new ResponseEntity<>(guests, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{guestId}", method = RequestMethod.GET)
    public ResponseEntity<?> getGuest(
        @PathVariable(value = "guestId") Long guestId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsGuest(guestId)) {
            String errorStr = "Guest not found";
            CustomErrorType error = getCustomErrorType(errorStr);
            headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
            return getResponseEntity(headers, error, HttpStatus.NO_CONTENT);
        } else {
            Guest guest = service.getGuest(guestId);
            headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
            return getGuestResponseEntity(headers, guest);
        }
    }

    private ResponseEntity<Guest> getGuestResponseEntity(HttpHeaders headers, Guest guest) {
        return new ResponseEntity<>(guest, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{guestId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGuest(
        @RequestBody Guest guest,
        @PathVariable(value = "guestId") Long guestId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsGuest(guestId)) {
            CustomErrorType error = getCustomErrorType("Guest not found");
            headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
            return getResponseEntity(headers, error, HttpStatus.NO_CONTENT);
        } else {
            try {
                service.updateGuest(guest, guestId);
                headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
                return getGuestResponseEntity(headers, guest);
            } catch (RuntimeException r) {
                String errStr = "Unable to update guest, cause => " + r.getMessage();

                CustomErrorType error = getCustomErrorType(errStr);
                return getResponseEntity(headers, error, HttpStatus.NO_CONTENT);
            }
        }
    }

    @RequestMapping(value = "/{guestId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGuest(@PathVariable(value = "guestId") Long guestId, UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsGuest(guestId)) {
            CustomErrorType error = getCustomErrorType("Guest not found");
            headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
            return getResponseEntity(headers, error, HttpStatus.NO_CONTENT);
        } else {
            service.deleteGuest(guestId);
            CustomErrorType msg = getCustomErrorType("Guest deleted");
            headers.setLocation(ucBuilder.path("/guest/{guestId}").buildAndExpand(guestId).toUri());
            return getResponseEntity(headers, msg, HttpStatus.OK);
        }
    }

    private ResponseEntity<?> getResponseEntity(HttpHeaders headers, CustomErrorType msg, HttpStatus ok) {
        return new ResponseEntity<>(msg, headers, ok);
    }

    private CustomErrorType getCustomErrorType(String errStr) {
        logger.error(errStr);
        return new CustomErrorType(errStr);
    }
}