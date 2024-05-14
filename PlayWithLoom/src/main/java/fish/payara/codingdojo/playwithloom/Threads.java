package fish.payara.codingdojo.playwithloom;

/**
 *
 * @author Ondro Mihalyi
 */
public class Threads {

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {

                }
            }, "MyThread-" + i).start();
        }
        System.out.println("Active threads: " + Thread.activeCount());
    }
}
