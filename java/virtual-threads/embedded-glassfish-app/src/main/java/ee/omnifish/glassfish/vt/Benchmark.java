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
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Ondro Mihalyi
 */
public class Benchmark {

    static class Params {

        int parallelRequests = 10000;
        int allRequests = 50000;
        String url = "http://localhost:8080/";
        String argName;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final Params params = parseArgs(args);
        final int numberOfInitialRequests = params.parallelRequests;
        final AtomicLong requestsToSend = new AtomicLong(params.allRequests);
        final AtomicLong responsesReceived = new AtomicLong(0);
        final long totalRequestsToSend = requestsToSend.get();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(params.url))
                .timeout(Duration.ofSeconds(50))
                .GET()
                .build();
        final HttpClient client = HttpClient.newHttpClient();
        LocalDateTime start = now();
        Queue<CompletableFuture<?>> futures = sendAllRequests(numberOfInitialRequests, client, request, requestsToSend, responsesReceived, totalRequestsToSend);
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

    private static Queue<CompletableFuture<?>> sendAllRequests(final int numberOfInitialRequests, final HttpClient client, HttpRequest request, final AtomicLong requestsToSend, final AtomicLong responsesReceived, final long totalRequestsToSend) {
        Queue<CompletableFuture<?>> futures = new LinkedBlockingDeque<>();
        final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < numberOfInitialRequests; i++) {
            executor.submit(() -> sendRequest(client, request, futures, requestsToSend, responsesReceived, totalRequestsToSend, executor));
        }
        return futures;
    }

    private static Params parseArgs(String[] args) {
        final Params p = Arrays.stream(args).reduce(new Params(), (params, arg) -> {
            if (params.argName == null) {
                params.argName = arg;
            } else {
                switch (params.argName) {
                    case "-n" ->
                        params.allRequests = Integer.valueOf(arg);
                    case "-c" ->
                        params.parallelRequests = Integer.valueOf(arg);
                }
                params.argName = null;
            }
            return params;
        }, (p1, p2) -> p1);
        if (p.argName != null) {
            p.url = p.argName;
            p.argName = null;
        }
        return p;
    }

    private static void sendRequest(final HttpClient client, HttpRequest request, Queue<CompletableFuture<?>> futures, AtomicLong requestsToSend, AtomicLong responsesReceived, long totalRequestsToSend, ExecutorService executor) {
        if (requestsToSend.decrementAndGet() >= 0) {
            var future = client
                    .sendAsync(request, HttpResponse.BodyHandlers.discarding());
            futures.add(
                    future
                            .thenRunAsync(() -> {
                                final long responses = responsesReceived.incrementAndGet();
                                if (responses % (totalRequestsToSend / 10) == 0) {
                                    System.out.println("Received " + responses + " responses so far...");
                                }
                            }, executor)
                            .thenRun(() -> {
                                sendRequest(client, request, futures, requestsToSend, responsesReceived, totalRequestsToSend, executor);
                            })
            );
        }
    }
}
