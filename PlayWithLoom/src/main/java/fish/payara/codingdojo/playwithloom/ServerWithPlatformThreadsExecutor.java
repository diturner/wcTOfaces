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

import static fish.payara.codingdojo.playwithloom.ServerWithoutThreads.getSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
public class ServerWithPlatformThreadsExecutor {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = getSocket();
        ExecutorService pool = Executors.newFixedThreadPool(Integer.parseInt(System.getProperty("threads", "10000")));

        while (true) {
            // Accept incoming client connections
            Socket clientSocket = serverSocket.accept();

            // Handle the client request in a separate thread
            pool.submit(() -> {
                try {
                    ServerWithoutThreads.handleRequest(clientSocket);
                } catch (Exception ex) {
                    Logger.getLogger(ServerWithLoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}


/*




























from other computer
664 reqs/s

2023-10-05T08:51:13.762530119  Counter: 47913 -> 47419 (  494 running,   499 max)
2023-10-05T08:51:13.76309213   Counter: 47913 -> 47421 (  493 running,   499 max)
2023-10-05T08:51:13.763095552  Counter: 47913 -> 47421 (  492 running,   499 max)
2023-10-05T08:51:13.76310966   Counter: 47913 -> 47422 (  491 running,   499 max)
2023-10-05T08:51:13.767832315  Counter: 47915 -> 47423 (  492 running,   499 max)
2023-10-05T08:51:13.769047759  Counter: 47917 -> 47424 (  493 running,   499 max)
2023-10-05T08:51:13.770793158  Counter: 47921 -> 47425 (  496 running,   499 max)
2023-10-05T08:51:13.771114357  Counter: 47921 -> 47426 (  495 running,   499 max)
2023-10-05T08:51:13.771487658  Counter: 47921 -> 47427 (  494 running,   499 max)
2023-10-05T08:51:13.773925601  Counter: 47924 -> 47428 (  496 running,   499 max)

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
Time taken for tests:   10.865 seconds
Complete requests:      100000
Failed requests:        0
Total transferred:      5400000 bytes
HTML transferred:       3500000 bytes
Requests per second:    9203.95 [#/sec] (mean)
Time per request:       17.384 [ms] (mean)
Time per request:       0.109 [ms] (mean, across all concurrent requests)
Transfer rate:          485.36 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    9  35.5      8    1033
Processing:     1    8   2.9      8     217
Waiting:        0    5   2.5      5     216
Total:          5   17  35.9     17    1250

Percentage of the requests served within a certain time (ms)
  50%     17
  66%     17
  75%     17
  80%     18
  90%     19
  95%     20
  98%     22
  99%     24
 100%   1250 (longest request)
 */
