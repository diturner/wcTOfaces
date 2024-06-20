package ee.omnifish.glassfish.vt;


import static java.time.Duration.ofMillis;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/")
public class EmbeddedGfSimpleServlet extends HttpServlet {

    static AtomicInteger counter = new AtomicInteger(0);
    static AtomicInteger maxCounter = new AtomicInteger(0);

    private System.Logger logger = System.getLogger(this.getClass().getName());
    static AtomicReference<LocalDateTime> lastTimeRef = new AtomicReference<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//                logger.log(WARNING, () -> "Current thread: " + Thread.currentThread().getName() + (Thread.currentThread().isVirtual() ? "is" : "is not") + " a virtual thread.");

        var current = counter.incrementAndGet();
        final int maxCounterValue = maxCounter.get();
        if (current > maxCounterValue) {
            if (maxCounterValue == maxCounter.compareAndExchange(maxCounterValue, current))  {
//            if (shouldPrint(maxCounterValue)) {
//                logger.log(INFO, "Max Current threads: " + current);
            }
        }
//        logger.log(INFO, "Current threads: " + current);
        runInDeeperStack(() -> sleepWithVariation(50L, 150L), 100);

        response.setContentType("text/plain");
        final PrintWriter writer = response.getWriter();
        writer.println("Hello from Embedded GlassFish ! Running on " + (Thread.currentThread().isVirtual() ? "virtual" : " platform") + " threads.");
        current = counter.decrementAndGet();
//        if (shouldPrint(current)) {
//            logger.log(INFO, "Current threads: " + current);
//        }
//        System.out.println("Current threads: " + current);
    }

    private void sleepWithVariation(long minSleepMillis, long maxSleepMillis) throws RuntimeException {
        //            logger.log(INFO, "Thread is virtual: " + Thread.currentThread().isVirtual());
        try {
            Thread.yield();
            Thread.sleep(ofMillis(random(minSleepMillis, maxSleepMillis)));
        } catch (InterruptedException ex) {
            Logger.getLogger(EmbeddedGfSimpleServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    final Random random = new Random(0);

    private long random(long min, long max) {
        return random.nextLong(min, max + 1);
    }

    private boolean shouldPrint(int current) {
//        return true;
        final LocalDateTime now = LocalDateTime.now();
        if (lastTimeRef.compareAndSet(null, now)) {
            return true;
        }
        final LocalDateTime lastTime = lastTimeRef.get();
        if (now.minus(100, ChronoUnit.MILLIS).isAfter(lastTime)) {
            if (lastTimeRef.compareAndSet(lastTime, now)) {
                return true;
            }
        }
        return false;
    }

    private void runInDeeperStack(Runnable runnable, int depth) {
        if (depth > 0) {
            runInDeeperStack(runnable, depth-1);
        } else {
            runnable.run();
        }
    }

}
