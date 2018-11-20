package com.around.volcanoinn.springboot.controller;

import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.entity.Campsite;
import com.around.volcanoinn.springboot.service.CampsiteService;
import com.around.volcanoinn.springboot.util.CustomErrorType;
import com.around.volcanoinn.springboot.util.Utilities;


@RestController
@RequestMapping("/campsite")
public class CampsiteController {

    private static final Logger logger = LoggerFactory.getLogger(CampsiteController.class);

    @Autowired
    CampsiteService service;

    @RequestMapping(path = "/{cmpsId}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<?> getCampsiteAvailabilityDateRange(
        @PathVariable(value = "cmpsId") Long cmpsId,
        @RequestParam(value = "fromDate", required = false) Long fromDate,
        @RequestParam(value = "toDate", required = false) Long toDate,
        UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        Optional<Campsite> optionalCampsite = service.getCampsiteAvailability(cmpsId, Optional.ofNullable(Utilities.getDateFromUnixTime(fromDate)), Optional.ofNullable(Utilities.getDateFromUnixTime(toDate)));
        headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(cmpsId).toUri());
        CustomErrorType error = getCustomErrorType(cmpsId);
        ResponseEntity customErrorTypeResponseEntity = new ResponseEntity<>(error, headers, HttpStatus.NO_CONTENT);
        optionalCampsite.ifPresent(campsite -> headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(campsite.getId()).toUri()));
        return service.existsCampsite(cmpsId).map(campsiteExists ->
            optionalCampsite.map(campsite ->
                new ResponseEntity<>(campsite, headers, HttpStatus.OK)).get()).orElse(customErrorTypeResponseEntity);

    }

    @RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
    ResponseEntity<List<Campsite>> getAllCampsitesAvailability(
        @RequestParam(value = "fromDate", required = false) Long fromDate,
        @RequestParam(value = "toDate", required = false) Long toDate, UriComponentsBuilder ucBuilder) {

        List<Campsite> campsites = service.getCampsitesAvailability(Utilities.getDateFromUnixTime(fromDate), Utilities.getDateFromUnixTime(toDate));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/campsite").build().toUri());
        return new ResponseEntity<>(campsites, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/{cmpsId}/booking", method = RequestMethod.POST)
    public ResponseEntity<?> bookingCampsite(
        @RequestBody Booking booking,
        @PathVariable(value = "cmpsId") Long cmpsId, UriComponentsBuilder ucBuilder) {

        logger.debug("Attempting to booking campsite => " + cmpsId + " with booking => " + booking);

        HttpHeaders headers = new HttpHeaders();

       return service.existsCampsite(cmpsId).map(campsite -> {
        try {
            service.booking(booking, cmpsId);
            logger.debug("Created booking with ID => " + booking.getId());
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(booking.getId()).toUri());
            return new ResponseEntity<>(booking, headers, HttpStatus.CREATED);
        } catch (RuntimeException r) {
            return logError(headers, cmpsId, "Unable to booking campsite, cause => ".concat(r.getMessage()));
        }
        }).orElse(logError(headers,cmpsId, "Campsite with ID => " + cmpsId + " does not exist"));
    }

    private ResponseEntity<?> logError(HttpHeaders headers, Long cmpsId, String errorMessage) {
        logger.error(errorMessage);
        CustomErrorType error = getCustomErrorType(cmpsId);
        return new ResponseEntity<>(error, headers, HttpStatus.NO_CONTENT);
    }

    private CustomErrorType getCustomErrorType(@PathVariable("cmpsId") Long cmpsId) {
        return new CustomErrorType("Campsite with ID => " + cmpsId + " does not exist");
    }
}