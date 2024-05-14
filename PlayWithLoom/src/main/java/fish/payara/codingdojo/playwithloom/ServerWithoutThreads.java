/*
 * The MIT License
 *
 * Copyright 2023 aubi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fish.payara.codingdojo.playwithloom;

import static java.time.Duration.ofMillis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
public class ServerWithoutThreads {

//    private static org.postgresql.Driver initializeDriver = new org.postgresql.Driver(); // initialize driver
    private static final boolean SHOULD_LOG = false;

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port, 1_000);
        System.out.println("Server is listening on port " + port);

        while (true) {
            // Accept incoming client connections
            Socket clientSocket = serverSocket.accept();

            // Handle the client request synchronously
            handleRequest(clientSocket);
        }
    }

    private static AtomicLong counterStarted = new AtomicLong(0);
    private static AtomicLong counterFinished = new AtomicLong(0);
    private static AtomicLong maxDiff = new AtomicLong(0);

    public static void handleRequest(Socket clientSocket) {
        counterStarted.incrementAndGet();
        try {
            // Read the client's request
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = in.readLine(); // Read the first line of the request

            /*if (requestLine != null)*/ {
                // Parse the request to extract method and URL
//            String[] requestParts = requestLine.split(" ");
//            String method = requestParts[0];
//            String url = requestParts[1];

                // Process the request and generate a response (simplified for demonstration)
//                Thread.sleep(100); // save data to database
                String response = "HTTP/1.0 200 OK\r\n\r\n"
                        //                        + taskWithDB();
                        + taskWithSleep();

                // Send the response to the client
                OutputStream out = clientSocket.getOutputStream();
                out.write(response.getBytes());

                // Close the streams and the client socket
                out.flush();
                in.close();
                clientSocket.close();
            }
        } catch (SocketException e) {
            System.out.println("ERROR SocketException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        counterFinished.incrementAndGet();
        if (SHOULD_LOG) {
            long diff = counterStarted.longValue() - counterFinished.longValue();
            maxDiff.updateAndGet(d -> Math.max(d, diff));
            System.out.printf("%-30s Counter: %5d -> %5d (%5d running, %5d max)%n",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    counterStarted.longValue(),
                    counterFinished.longValue(),
                    diff,
                    maxDiff.longValue());
        }
    }

    public static ServerSocket getSocket() throws IOException {
        final int port = Integer.parseInt(System.getProperty("port", "8080"));
        final ServerSocket serverSocket = new ServerSocket(port, 100_000);
        System.out.println("Server with PID " + ProcessHandle.current().pid() + " is listening on port " + port);
        return serverSocket;
    }

    private static String taskWithDB() {
        StringBuilder users = null;
        if (counterStarted.get() % 2 > 0) {
            while (users == null) {
                // retry connect to database again and again
                try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bonbonis", "bonbonis", "bonbonis")) {
                    Statement st = con.createStatement();
                    users = new StringBuilder();
                    try (ResultSet rs = st.executeQuery("SELECT last_name from person")) {
                        while (rs.next()) {
                            users.append(rs.getString(1)).append("\n");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Well, exception connecting to database, trying again: " + e.getMessage());
                    Thread.yield();
                }
            }
        } else {
            // some part of request just quickly return
            users = new StringBuilder("Users calculated");
        }
        //                Thread.sleep(100); // load data from database
        BigInteger bi = BigInteger.ONE;
        for (int i = 1; i < 100; i++) {
            bi = bi.multiply(BigInteger.valueOf(i));
        }
        return users.toString() + "\n"
                + "100! = " + bi;
    }

    private static String taskWithSleep() {
        long sleep = Long.valueOf(System.getProperty("sleep", "100"));
        int stackDepth = Integer.valueOf(System.getProperty("depth", "100"));
        if (sleep >= 50) {
            runInDeeperStack(() -> sleepWithVariation(sleep - 50, sleep + 50), stackDepth);
        } else {
            runInDeeperStack(() -> sleepWithVariation(0, 0), stackDepth);
        }
        return "OK";
    }

    private static void sleepWithVariation(long minSleepMillis, long maxSleepMillis) throws RuntimeException {
        //            logger.log(INFO, "Thread is virtual: " + Thread.currentThread().isVirtual());
        try {
            Thread.yield();
            Thread.sleep(ofMillis(random(minSleepMillis, maxSleepMillis)));
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerWithoutThreads.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private static long random(long min, long max) {
        return new Random().nextLong(min, max + 1);
    }

    private static void runInDeeperStack(Runnable runnable, int depth) {
        if (depth > 0) {
            runInDeeperStack(runnable, depth - 1);
        } else {
            runnable.run();
        }
    }
}
/*
from other computer
117 reqs/s

2023-10-05T08:42:45.096182448  Counter:  2239 ->  2239 (    0 running,     0 max)
2023-10-05T08:42:45.104491282  Counter:  2240 ->  2240 (    0 running,     0 max)
2023-10-05T08:42:45.112151923  Counter:  2241 ->  2241 (    0 running,     0 max)
2023-10-05T08:42:45.120474027  Counter:  2242 ->  2242 (    0 running,     0 max)
2023-10-05T08:42:45.129867282  Counter:  2243 ->  2243 (    0 running,     0 max)
2023-10-05T08:42:45.138650615  Counter:  2244 ->  2244 (    0 running,     0 max)
2023-10-05T08:42:45.146290583  Counter:  2245 ->  2245 (    0 running,     0 max)
2023-10-05T08:42:45.154306993  Counter:  2246 ->  2246 (    0 running,     0 max)

ab -c 160 -n 100000 http://localhost:8080/
This is ApacheBench, Version 2.3 <$Revision: 1903618 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 10000 requests
Completed 20000 requests
Completed 30000 requests
Completed 40000 requests
Completed 50000 requests
Completed 60000 requests
Completed 70000 requests
Completed 80000 requests
Completed 90000 requests
Completed 100000 requests
Finished 100000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8080

Document Path:          /
Document Length:        35 bytes

Concurrency Level:      160
Time taken for tests:   9.269 seconds
Complete requests:      100000
Failed requests:        0
Total transferred:      5400000 bytes
HTML transferred:       3500000 bytes
Requests per second:    10789.19 [#/sec] (mean)
Time per request:       14.830 [ms] (mean)
Time per request:       0.093 [ms] (mean, across all concurrent requests)
Transfer rate:          568.96 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0   10  82.8      3    1037
Processing:     1    5  10.8      4     867
Waiting:        1    4  10.8      3     866
Total:          4   14  86.8      8    1890

Percentage of the requests served within a certain time (ms)
  50%      8
  66%      8
  75%      8
  80%      8
  90%      8
  95%      9
  98%      9
  99%      9
 100%   1890 (longest request)
 */
