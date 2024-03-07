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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
public class ServerWithPlatformThreads {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port, 10_000);
        System.out.println("Server is listening on port " + port);

        while (true) {
            // Accept incoming client connections
            Socket clientSocket = serverSocket.accept();

            // Handle the client request in a separate thread
            Thread thread = new Thread(() -> {
                try {
                    ServerWithoutThreads.handleRequest(clientSocket);
                } catch (Exception ex) {
                    Logger.getLogger(ServerWithLoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            thread.start();
        }
    }
}

/*
from other computer
698 reqs/s

2023-10-05T08:38:26.342365792  Counter: 54117 -> 54101 (   16 running,   141 max)
2023-10-05T08:38:26.34447156   Counter: 54119 -> 54102 (   17 running,   141 max)
2023-10-05T08:38:26.345294773  Counter: 54119 -> 54103 (   16 running,   141 max)
2023-10-05T08:38:26.348529516  Counter: 54121 -> 54104 (   17 running,   141 max)
2023-10-05T08:38:26.349424106  Counter: 54122 -> 54105 (   17 running,   141 max)
2023-10-05T08:38:26.351514858  Counter: 54122 -> 54106 (   16 running,   141 max)
2023-10-05T08:38:26.352053191  Counter: 54123 -> 54107 (   16 running,   141 max)
2023-10-05T08:38:26.352387728  Counter: 54124 -> 54108 (   16 running,   141 max)


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
