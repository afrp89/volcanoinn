package com.around.volcanoinn.springboot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.web.client.RestTemplate;
import com.around.volcanoinn.springboot.entity.Booking;

public class ConcurrentRequestGenerator {

    private int numPoolThreads;
    private int numParallelReqs;
    private Booking booking;
    private String serviceUri;

    public ConcurrentRequestGenerator() {
    }

    public ConcurrentRequestGenerator(int numPoolThreads, int numParallelReqs, Booking booking, String serviceUri) {
        this.numPoolThreads = numPoolThreads;
        this.numParallelReqs = numParallelReqs;
        this.booking = booking;
        this.serviceUri = serviceUri;
    }

    public void run() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(numPoolThreads);
        List<RequestThread> threads = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (int i = 0; i < numParallelReqs; i++) {
            RequestThread requestThread = new RequestThread(booking, serviceUri);
            es.execute(requestThread);
            threads.add(requestThread);
        }
        es.shutdown();
        es.awaitTermination(2, TimeUnit.MINUTES);

        // Delete created resources
        for (RequestThread rt : threads) {
            if (rt.getResourceUri() != null)
                restTemplate.delete(rt.getResourceUri());
        }
    }

}