package com.glmall.search.thread;

import java.util.concurrent.*;

public class TreadTest {

    public static void main(String[] args) throws Exception {
//        Thread01 thread01 = new Thread01();
//        thread01.start();
        // Executors.newScheduledThreadPool();

        // 线程池工作顺序
        // threadPoolTest();
        /*CompletableFuture<Void> runAsync1 = CompletableFuture.runAsync(() -> {
            System.out.println("Thread " + Thread.currentThread().getName() + " is running...");
        }, executor);


        // 方法完成后的感知
        CompletableFuture<Integer> supplyAsync1 = CompletableFuture
                .supplyAsync(() -> 5 / 0, executor)
                .whenComplete((result, e) -> {
                    System.out.println("Thread " + Thread.currentThread().getName() + " is running...");
                    System.out.println("执行结束，结果是：" + result + "，异常是：" + e);
                }).exceptionally(throwable -> {
                    // 可以感知异常，同时返回默认值
                    return 10;
                });

        System.out.println("supplyAsync1.result="+ supplyAsync1.get());

        // 方法完成后的处理
        CompletableFuture<Integer> supplyAsync2 = CompletableFuture
                .supplyAsync(() -> 6 / 0, executor)
                .handle((res,e)->{
                    System.out.println("Thread " + Thread.currentThread().getName() + " is running...");
                    System.out.println("执行结束，结果是：" + res + "，异常是：" + e);
                    // 可以感知结果和异常，同时返回默认值
                    return 10;
                });
        System.out.println("supplyAsync2.result="+ supplyAsync2.get());*/

        CompletableFuture<Void> runAsync3 = CompletableFuture.runAsync(() -> {
            System.out.println("Thread " + Thread.currentThread().getName() + " is running...");
        }, executor);

//         runAsync3.thenRun();
//         runAsync3.thenRunAsync();
//         runAsync3.thenAccept();
//         runAsync3.thenAcceptAsync();
//         runAsync3.thenApply();
//         runAsync3.thenApplyAsync();


        // CompletableFuture<Void> future = CompletableFuture.allOf(runAsync1, supplyAsync1);
        // Integer integer = supplyAsync1.get();

    }

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            10,
            5,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * 该方法验证了线程池工作流程：
     * 1、线程池一旦创建，则立即准备好 corePoolSize 数量的核心线程，准备接受任务
     * 2、当核心线程满了，新到来的任务会被直接放入阻塞队列中等待执行
     * 3、当阻塞队列满了以后，就直接开启新的线程，执行阻塞队列中的任务
     * 4、当执行的线程数达到 maximumPoolSize 后，新进来的线程将会使用拒绝策略
     * 拒绝入队；
     * 5、当阻塞队列中的所有任务都执行完成后，如果没有新的任务进来，线程池
     * 中的线程就会处于闲置状态，在达到 keepAliveTime 这个时间后，线程池
     * 会释放 （ maximumPoolSize - corePoolSize ）个闲置线程
     */
    private static void threadPoolTest() {
        while (true) {
            // 每隔3秒向线程池中保存一个线程任务
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            executor.execute(() -> {
                System.out.println("Thread " + Thread.currentThread().getName() + " is running...");
                try {
                    Thread.sleep(1000 * 60 * 30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread " + Thread.currentThread().getName() + " is stoped...");
            });

            System.out.println("当前线程池阻塞队列大小：" + executor.getQueue().size());
        }
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
        }
    }
}
