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
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
public class ServerWithLoomLimited {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = getSocket();
        Semaphore sem = new Semaphore(Integer.parseInt(System.getProperty("threads", "10000")));

        while (true) {
            // Accept incoming client connections
            Socket clientSocket = serverSocket.accept();

            // Handle the client request in a separate thread
            Thread.startVirtualThread(() -> {
                try {
                    sem.acquire();
                    ServerWithoutThreads.handleRequest(clientSocket);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerWithLoomLimited.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    sem.release();
                }
            });
//            executor.execute(() -> ServerWithoutThreads.handleRequest(clientSocket));
        }
    }
}
/*
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
Time taken for tests:   10.018 seconds
Complete requests:      100000
Failed requests:        0
Total transferred:      5400000 bytes
HTML transferred:       3500000 bytes
Requests per second:    9982.49 [#/sec] (mean)
Time per request:       16.028 [ms] (mean)
Time per request:       0.100 [ms] (mean, across all concurrent requests)
Transfer rate:          526.42 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    8   2.5      8      19
Processing:     1    8   2.8      8      51
Waiting:        0    4   2.7      3      46
Total:         12   16   1.6     16      62

Percentage of the requests served within a certain time (ms)
  50%     16
  66%     16
  75%     16
  80%     16
  90%     17
  95%     18
  98%     19
  99%     21
 100%     62 (longest request)
*/
