/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ee.omnifish.glassfish.vt;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Ondro Mihalyi
 */
public class Benchmark {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final int numberOfRequests = 20000;
        final AtomicLong requestsToSend = new AtomicLong(50000);
        final AtomicLong responsesReceived = new AtomicLong(0);
        final long requestsToSendConstant = requestsToSend.get();
        Queue<CompletableFuture<?>> futures = new LinkedBlockingDeque<>();
        LocalDateTime start = now();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("http://localhost:9090/"))
                .timeout(Duration.ofSeconds(50))
                .GET()
                .build();
        final HttpClient client = HttpClient.newHttpClient();
        for (int i = 0; i < numberOfRequests; i++) {
            sendRequest(client, request, futures, requestsToSend, responsesReceived, requestsToSendConstant);
        }
        try {
            CompletableFuture<?> future = futures.poll();
            while (future != null) {
                future.join();
                future = futures.poll();
            }
        } finally {
            LocalDateTime end = now();

            System.out.println("Number of requests: " + responsesReceived.get());
            final Duration timeTaken = Duration.between(start, end);
            System.out.println("Time taken in seconds: " + timeTaken.toMillis() / 1000.0);
            System.out.println("Requests per second: " + ((double) responsesReceived.get()) / timeTaken.toMillis() * 1000.0);
        }

    }

    private static void sendRequest(final HttpClient client, HttpRequest request, Queue<CompletableFuture<?>> futures, AtomicLong requestsToSend, AtomicLong responsesReceived, long requestsToSendConstant) {
        if (requestsToSend.decrementAndGet() >= 0) {
            var future = client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString());
            futures.add(
                    future
                            .thenRun(() -> {
                                final long responses = responsesReceived.incrementAndGet();
                                if (responses % (requestsToSendConstant / 10) == 0) {
                                    System.out.println("Received " + responses + " responses so far...");
                                }
                            })
                            .thenRun(() -> {
                                sendRequest(client, request, futures, requestsToSend, responsesReceived, requestsToSendConstant);
                            })
            );
        }
    }
}
