package multithreads;

/**
 * @author lzn
 * @date 2023/07/03 20:38
 * @description Output a and b 100 times alternately via two threads and make sure the a will output firstly, for example: a, b, a, b, a, b.................
 *
 * Tips: After invoking the notify(), the current thread will not release the lock and wake up other thread immediately unless the synchronized block is completed
 */
public class AlternateExecutionByLock {

    public static void main(String[] args) {
        Object lock = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                for (int i = 0; i < 50; i++) {
                    System.out.println("a");
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                for (int i = 0; i < 50; i++) {
                    System.out.println("b");
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
