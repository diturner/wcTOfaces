package fish.payara.codingdojo.playwithloom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Ondro Mihalyi
 */
public class ThreadPools {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool(100);
        for (int i = 0; i < 100000; i++) {
            executor.submit(() -> {
                try {
                    System.out.println("Thread " + Thread.currentThread().getName() + " is going to sleep.");
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            });
        }
        executor.awaitTermination(1, TimeUnit.MINUTES);

    }
}
