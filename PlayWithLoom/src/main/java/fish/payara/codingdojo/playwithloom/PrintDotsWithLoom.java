/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package fish.payara.codingdojo.playwithloom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
public class PrintDotsWithLoom {

    public static void main(String[] args) {
        measure("Virtual threads", () -> runVirtualThreads());
        measure("Platform threads", () -> runPlatformThreads());
    }

    public static void runVirtualThreads() {
        System.out.printf("Hello %s!%n", Runtime.version());
        Thread t = Thread.startVirtualThread(() -> {
            System.out.println("Hello from thread!");
        });
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(PrintDotsWithLoom.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int i = 0; i < 1_000; i++) {
                System.out.print(".");
            }
        }
        System.out.println();
    }

    public static void runPlatformThreads() {
        System.out.printf("Hello %s!%n", Runtime.version());
        Thread t = new Thread(() -> {
            System.out.println("Hello from thread!");
        });
        try {
            t.start();
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(PrintDotsWithLoom.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                System.out.print(".");
            }
        }
        System.out.println();
    }

    public static void measure(String name, Runnable task) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        System.out.printf("%s ran %,d ns%n", name, end - start);
    }
}
