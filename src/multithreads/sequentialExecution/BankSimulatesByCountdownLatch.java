package multithreads.sequentialExecution;

import java.util.concurrent.CountDownLatch;

/**
 * @author lzn
 * @date 2023/07/08 21:40
 * @description
 */
public class BankSimulatesByCountdownLatch {

    private int balance;

    public BankSimulatesByCountdownLatch() {
        this.balance = 0;
    }

    public void deposit(int number) {
        balance += number;
    }

    public void withdraw(int number) {
        if (balance == 0) {
            throw new IllegalArgumentException("The current balance is 0");
        }

        balance -= number;
    }

    public static void main(String[] args) {
        BankSimulatesByCountdownLatch bankSimulatesByCountdownLatch = new BankSimulatesByCountdownLatch();
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        Thread depositThread = new Thread(() -> {
            System.out.println("depositThread executed");
            bankSimulatesByCountdownLatch.deposit(500);
            countDownLatch2.countDown();
        });
        Thread withDrawThread = new Thread(() -> {
            try {
                countDownLatch2.await();
                System.out.println("withDrawThread executed");
                bankSimulatesByCountdownLatch.withdraw(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        depositThread.start();
        withDrawThread.start();

        System.out.println("The current balance: " + bankSimulatesByCountdownLatch.balance);
    }
}