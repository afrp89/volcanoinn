package com.around.volcanoinn.springboot.util;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import com.around.volcanoinn.springboot.entity.Booking;


public class RequestThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestThread.class);
    private Booking booking;
    private String serviceUri;
    private URI resourceUri;

    public RequestThread() {
    }

    public RequestThread(Booking booking, String serviceUri) {
        this.booking = booking;
        this.serviceUri = serviceUri;
    }


    @Override
    public void run() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Booking> requestEntity = new HttpEntity<>(booking, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            URI bookingUri = restTemplate.postForLocation(serviceUri, requestEntity);
            if (bookingUri == null) {
                logger.error(Thread.currentThread().getName() + " Cannot booking campsite ");
            } else {
                resourceUri = bookingUri;
                logger.info(Thread.currentThread().getName() + " Successfully bookingd campsite! Uri => " + bookingUri);
            }
        } catch (Exception e) {
            logger.error("Problem reserving campsite");
        }
    }

    public URI getResourceUri() {
        return resourceUri;
    }
}