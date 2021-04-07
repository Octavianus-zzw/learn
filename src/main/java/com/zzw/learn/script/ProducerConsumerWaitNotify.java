package com.zzw.learn.script;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ShareDataWaitNotify {
    public static AtomicInteger atomicInteger = new AtomicInteger();
    public static final int MAX_COUNT = 10;
    public static final List<Integer> pool = new ArrayList<>();

    public void produce() {
        OUT:
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break;
            }
            synchronized (pool) {
                while (pool.size() == MAX_COUNT) {
                    System.out.println(Thread.currentThread().getName() + ": pool is full, waiting...");
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().getName() + " interrupted");
                        break OUT;
                    }
                }
                pool.add(atomicInteger.incrementAndGet());
                System.out.println(Thread.currentThread().getName() + ": produce number: " + atomicInteger.get() + "\t" + "current size: " + pool.size());
                pool.notifyAll();
            }
        }
    }

    public void consume() {
        OUT:
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break;
            }
            synchronized (pool) {
                while (pool.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + ": pool is empty, waiting...");
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().getName() + " interrupted");
                        break OUT;
                    }
                }
                int temp = pool.get(0);
                pool.remove(0);
                System.out.println(Thread.currentThread().getName() + ": consume number: " + temp + "\t" + "current size: " + pool.size());
                pool.notifyAll();
            }
        }
    }
}

public class ProducerConsumerWaitNotify {
    public static  void main(String[] args) {
        ShareDataWaitNotify shareData = new ShareDataWaitNotify();
        Thread tA = new Thread(() -> {
            shareData.produce();
        }, "Thread-A");
        Thread tB = new Thread(() -> {
            shareData.consume();
        }, "Thread-B");
        Thread tC = new Thread(() -> {
            shareData.consume();
        }, "Thread-C");
        Thread tD = new Thread(() -> {
            shareData.consume();
        }, "Thread-D");

        tA.start();
        tB.start();
        tC.start();
        tD.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tA.interrupt();
        tB.interrupt();
        tC.interrupt();
        tD.interrupt();
    }
}
