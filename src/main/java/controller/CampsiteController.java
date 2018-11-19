package controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import entity.Booking;
import entity.Campsite;
import service.CampsiteService;
import util.CustomErrorType;
import util.Utilities;


@RestController
@RequestMapping("/campsite")
public class CampsiteController {

    private static final Logger logger = LoggerFactory.getLogger(CampsiteController.class);

    @Autowired
    CampsiteService service;

    @RequestMapping(path = "/{cmpsId}" , method = RequestMethod.GET, produces= { "application/json" })
    public ResponseEntity<?> getCampsiteAvailabilityDateRange(
        @PathVariable(value="cmpsId") Long cmpsId,
        @RequestParam(value="fromDate", required = false) Long fromDate,
        @RequestParam(value="toDate", required = false) Long toDate,
        UriComponentsBuilder ucBuilder) {

        HttpHeaders headers = new HttpHeaders();
        if (!service.existsCampsite(cmpsId)) {
            headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(cmpsId).toUri());
            CustomErrorType error =  new CustomErrorType("Campsite with ID => " + cmpsId + " does not exist");
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        } else {
            Campsite campsite = service.getCampsiteAvailability(cmpsId, Utilities.getDateFromUnixTime(fromDate), Utilities.getDateFromUnixTime(toDate));
            headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(campsite.getId()).toUri());
            return new ResponseEntity<Campsite>(campsite, headers, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces= { "application/json" })
    ResponseEntity<List<Campsite>>getAllCampsitesAvailability(
        @RequestParam(value="fromDate", required = false) Long fromDate,
        @RequestParam(value="toDate", required = false) Long toDate, UriComponentsBuilder ucBuilder) {

        List<Campsite> campsites = service.getCampsitesAvailability(Utilities.getDateFromUnixTime(fromDate), Utilities.getDateFromUnixTime(toDate));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/campsite").build().toUri());
        return new ResponseEntity<List<Campsite>>(campsites, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/{cmpsId}/booking", method = RequestMethod.POST)
    public ResponseEntity<?> bookingCampsite(
        @RequestBody Booking booking,
        @PathVariable(value="cmpsId") Long cmpsId, UriComponentsBuilder ucBuilder) {

        logger.debug("Attempting to booking campsite => " + cmpsId + " with booking => " + booking);

        HttpHeaders headers = new HttpHeaders();

        if (!service.existsCampsite(cmpsId)) {
            logger.error("Campsite with ID => " + cmpsId + " does not exist");
            CustomErrorType error =  new CustomErrorType("Campsite with ID => " + cmpsId + " does not exist");
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        }
        try {
            service.booking(booking, cmpsId);
            logger.debug("Created booking with ID => " + booking.getId());
            headers.setLocation(ucBuilder.path("/booking/{bookingId}").buildAndExpand(booking.getId()).toUri());
            return new ResponseEntity<Booking>(booking, headers, HttpStatus.CREATED);
        } catch (RuntimeException r) {
            logger.error("Unable to booking campsite, cause => " + r.getMessage());
            CustomErrorType error =  new CustomErrorType("Unable to booking campsite, cause => " + r.getMessage());
            return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.NO_CONTENT);
        }
    }
}