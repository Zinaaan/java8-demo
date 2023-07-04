package completableFuture;

import common.Result;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lzn
 * @date 2023/05/22 16:32
 * @description All usage of CompletableFuture in java8
 * <p>
 * runAsync       : just start a asynchronous execution                --- no input but output
 * supplyAsync    : start a asynchronous execution and return a result --- no input and output
 * <p>
 * thenAccept     : receive and consume the processing of the previous task   --- no output but input
 * thenRun        : don't receive any results and executed when the previous task completed  --- no input and output
 * <p>
 * thenApply      : for serial execution of multiple asynchronous tasks  --- with input and output
 * <p>
 * thenAcceptBoth : get two results of previous tasks when they complete execution --- no output
 * thenCombine    : get two results of previous tasks when they complete execution --- with output
 * <p>
 * whenComplete   : executed when the entire tasks completed or any of them throw exceptions
 * exceptionally  : only executed when any tasks throw exceptions
 */
public class CompletableFutureDemo {

    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        multiTaskSerialExecution();
//        twoTaskConcurrentlyExecution();
//        multipleTaskConcurrentlyExecution();
    }

    /**
     * Multiple asynchronous task serial executed
     * <p>
     * Notes:
     * 1. All the suffix of asynchronous method is "Async", but the synchronous does not have this suffix
     * 2. When we specify the thread pool for CompletableFuture (eg. ExecutorService), the synchronous chain requests will use this thread pool,
     * otherwise, use the default public thread pool called commonPool in ForkJoinPool.
     * 3. If we using asynchronous method in the synchronous chain request, for example, use thenApplyAsync instead of thenApply, the CompletableFuture
     * would use the default public thread pool called commonPool in ForkJoinPool whether you specify a thread pool or not
     * 4. We should always consider to specify the thread pool manually as the capacity of commonPool in ForkJoinPool is: (the number of core CPU - 1) and
     * the thread number maybe become the bottleneck
     */
    public static void multiTaskSerialExecution() throws InterruptedException {

        // Specify the thread pool for the CompletableFuture
        CompletableFuture.supplyAsync(() -> {
            doSomething();
            System.out.println(Thread.currentThread().getName() + " start working");
            return executionSucceed();
        }, executorService).thenApply(result -> {
            System.out.println(Thread.currentThread().getName() + " The result of the previous execution is: " + result);
            return executionSucceed();
        }).thenApply(result -> {
            System.out.println(Thread.currentThread().getName() + " The result of the previous execution is: " + result);
            return executionSucceed();
        }).whenComplete((result, throwable) -> {
            System.out.println("The entire task execution is completed, result: " + result);
            System.out.println("Exceptions: " + throwable);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        Thread.sleep(2000);

        // If we don't specify the thread pool manually for CompletableFuture, it will use the public thread pool of ForkJoinPool
        CompletableFuture.supplyAsync(() -> {
            doSomething();
            System.out.println(Thread.currentThread().getName() + " start working");
            return executionSucceed();
        }).thenCompose(result -> {
            System.out.println(Thread.currentThread().getName() + " The result of the previous execution is: " + result);
            return CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " need to return a completableFuture object");
                return executionSucceed();
            });
        }).thenApply(result -> {
            System.out.println(Thread.currentThread().getName() + " The result of the previous execution is: " + result);
            return executionSucceed();
        }).whenComplete((result, throwable) -> {
            System.out.println("The entire task execution is completed, result: " + result);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        Thread.sleep(2000);
    }

    /**
     * Two asynchronous task concurrently executed
     * <p>
     * thenAcceptBoth -- return void, thenCombine -- return object
     */
    public static void twoTaskConcurrentlyExecution() throws InterruptedException {

        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " cf1......");
            return 1;
        });

        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " cf2......");
            return 2;
        });

        CompletableFuture<Void> voidCompletableFuture = cf1.thenAcceptBoth(cf2, (resultA, resultB) -> System.out.println(resultA + resultB));

        CompletableFuture<Integer> integerCompletableFuture = cf1.thenCombine(cf2, Integer::sum);

        Thread.sleep(2000);
    }

    /**
     * Multiple asynchronous task concurrently executed
     * <p>
     * allOf, anyOf
     */
    public static void multipleTaskConcurrentlyExecution() throws InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            doSomething();
            System.out.println(Thread.currentThread().getName() + " exe cf1....");
            return 1;
        });

        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            doSomething();
            System.out.println(Thread.currentThread().getName() + " exe cf2....");
            return 2;
        });

        // Get the result directly when one of the task completed
        CompletableFuture.anyOf(cf1, cf2).whenComplete(((result, throwable) -> System.out.println("One of the task completed, result: " + result)));

        // Get the result only if all of these task completed, but couldn't get each result
        CompletableFuture.allOf(cf1, cf2).whenComplete(((result, throwable) -> System.out.println("All tasks completed, result: " + result)));

        // If we would like to get each result of the asynchronous tasks
        List<CompletableFuture<Integer>> completableFutures = Arrays.asList(cf1, cf2);
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .whenComplete((result, exception) -> {
                    Stream<Integer> integerStream = completableFutures.stream().map(CompletableFuture::join);
                    List<Integer> collect = integerStream.collect(Collectors.toList());
                    collect.forEach(System.out::println);
                });

        Thread.sleep(2000);
    }

    public static Result executionSucceed() {
        return new Result(0, "success");
    }

    public static Result executionFailed() {
        return new Result(500, "failed");
    }

    public static void doSomething() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
