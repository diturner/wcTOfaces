/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.codingdojo.playwithloom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ondro Mihalyi
 */
public class ThreadLocalTest {
    public static void main(String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Task());
        executor.submit(new Task());
        executor.close();
    }

    static class Task implements Runnable {
        ThreadLocal<Integer> tInt = ThreadLocal.withInitial(() -> 1);

        @Override
        public void run() {
            System.out.println("Value " + tInt.get());
            tInt.set(tInt.get() + 1);
        }

    }
}
