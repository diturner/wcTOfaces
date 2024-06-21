package fish.payara.codingdojo.playwithloom;

import static java.time.Duration.ofMinutes;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Ondro Mihalyi
 */
public class Threads {

    private static final AtomicInteger activeThreads = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        boolean virtual = args.length > 0 && args[0].equals("-v");
        final String pid = "" + ProcessHandle.current().pid();
        int i = 0;
        while (true) {
            i++;
            if (i % (virtual ? 100_000 : 1_000) == 0) {
                System.out.println("New thread " + i + ", pid = " + pid);
            }
            final Thread.Builder threadBuilder = virtual ? Thread.ofVirtual() : Thread.ofPlatform();
            threadBuilder.name("MyThread-" + i).start(() -> {
                try {
                    activeThreads.incrementAndGet();
                    Thread.sleep(ofMinutes(10));
                    activeThreads.decrementAndGet();
                } catch (Exception e) {

                }
            });
        }
//        Thread.sleep(Duration.of(100, MILLIS));
//        System.out.println("Active threads: " + activeThreads.get());
    }
}
