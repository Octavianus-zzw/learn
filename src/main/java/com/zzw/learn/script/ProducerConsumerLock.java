package com.zzw.learn.script;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShareDataLock {
    public static AtomicInteger atomicInteger = new AtomicInteger();
    public static final int MAX_COUNT = 10;
    public static final List<Integer> pool = new ArrayList<>();

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

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
            lock.lock();
            try {
                while (pool.size() == MAX_COUNT) {
                    System.out.println(Thread.currentThread().getName() + ": pool is full, waiting...");
                    condition.await();
                }
                pool.add(atomicInteger.incrementAndGet());
                System.out.println(Thread.currentThread().getName() + ": produce number: " + atomicInteger.get() + "\t" + "current size: " + pool.size());
                condition.signalAll();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break OUT;
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                while (pool.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + ": pool is empty, waiting...");
                    condition.await();
                }
                int temp = pool.get(0);
                pool.remove(0);
                System.out.println(Thread.currentThread().getName() + ": consume number: " + temp + "\t" + "current size: " + pool.size());
                condition.signalAll();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                break OUT;
            } finally {
                lock.unlock();
            }
        }
    }

}

public class ProducerConsumerLock {
    public static void main(String[] args) {
        ShareDataLock shareData = new ShareDataLock();
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
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tA.interrupt();
        tB.interrupt();
        tC.interrupt();
        tD.interrupt();
    }
}
