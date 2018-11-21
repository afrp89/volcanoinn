package com.around.volcanoinn.springboot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.around.volcanoinn.springboot.entity.Booking;

@Component
public class ConcurrentRequestGenerator {

    private int numPoolThreads;
    private int numParallelReqs;
    private Booking booking;
    private String serviceUri;

    public ConcurrentRequestGenerator() {
    }

    public void run() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(numPoolThreads);
        List<RequestThread> threads = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        executeThreads(es, threads);
        destroy(es, threads, restTemplate);

    }

    private void executeThreads(ExecutorService es, List<RequestThread> threads) {
        for (int i = 0; i < numParallelReqs; i++) {
            RequestThread requestThread = new RequestThread(booking, serviceUri);
            es.execute(requestThread);
            threads.add(requestThread);
        }
    }

    private void destroy(ExecutorService es, List<RequestThread> threads, RestTemplate restTemplate)throws InterruptedException {
        es.shutdown();
        es.awaitTermination(2, TimeUnit.MINUTES);
        for (RequestThread rt : threads) {
            if (rt.getResourceUri() != null)
                restTemplate.delete(rt.getResourceUri());
        }
    }


    public int getNumPoolThreads() {
        return numPoolThreads;
    }

    public void setNumPoolThreads(int numPoolThreads) {
        this.numPoolThreads = numPoolThreads;
    }

    public int getNumParallelReqs() {
        return numParallelReqs;
    }

    public void setNumParallelReqs(int numParallelReqs) {
        this.numParallelReqs = numParallelReqs;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

}