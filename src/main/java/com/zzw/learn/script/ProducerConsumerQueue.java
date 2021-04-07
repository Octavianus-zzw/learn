package com.zzw.learn.script;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ShareDataQueue {
    public static AtomicInteger atomicInteger = new AtomicInteger();
    public static final int MAX_COUNT = 10;
    public static BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(MAX_COUNT);
    public volatile boolean flag = true;

    public void produce() {
        while (flag) {
            boolean retvalue = false;
            try {
                retvalue = blockingQueue.offer(atomicInteger.incrementAndGet(), 2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (retvalue == true) {
                System.out.println(Thread.currentThread().getName() + "\t 插入队列" + atomicInteger.get() + "成功，资源队列大小=" + blockingQueue.size());
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 插入队列" + atomicInteger.get() + "失败，资源队列大小=" + blockingQueue.size());
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "flag变为false，生产停止");
    }

    public void consume() {
        Integer result = null;
        while (true) {
            try {
                result = blockingQueue.poll(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (null == result) {
                System.out.println("超过两秒没有取到数据，消费者即将退出");
                return;
            }
            System.out.println(Thread.currentThread().getName() + "\t 消费" + result + "成功\t\t资源队列大小=" + blockingQueue.size());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        flag = false;
    }
}

public class ProducerConsumerQueue {
    public static void main(String[] args) {
        ShareDataQueue shareData = new ShareDataQueue();
        Thread tA = new Thread(() -> {
            shareData.produce();
        }, "Thread-A");
        Thread tB = new Thread(() -> {
            shareData.produce();
        }, "Thread-B");
        Thread tC = new Thread(() -> {
            shareData.produce();
        }, "Thread-C");
        Thread tD = new Thread(() -> {
            shareData.consume();
        }, "Thread-D");

        tA.start();
        tB.start();
        tC.start();
        tD.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shareData.stop();
    }
}
