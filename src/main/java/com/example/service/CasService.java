package com.example.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * CAS 应用的类 ：AtomicInteger
 * get()：获取当前的值。
 * set(int newValue)：设置新的值。
 * getAndSet(int newValue)：设置新的值，并返回旧值。
 * incrementAndGet()：自增并返回自增后的值。
 * decrementAndGet()：自减并返回自减后的值。
 * getAndIncrement()：返回当前值，并自增。
 * getAndDecrement()：返回当前值，并自减。
 * compareAndSet(int expect, int update)：如果当前值等于期望值expect，则将其更新为update。该方法返回一个布尔值，表示操作是否成功
 */
@Slf4j
public class CasService {
    private final int NUM_THREADS = 5;
    private AtomicInteger counter = new AtomicInteger();
    private static AtomicStampedReference<String> atomicRef = new AtomicStampedReference<>("A", 0);
    @Test
    public void CasTest() throws InterruptedException{
        // 创建并启动多个线程
        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = new Thread(new CounterRunnable());
            thread.start();
        }
    }

    class CounterRunnable implements Runnable {
        @Override
        public void run() {
            // 对共享计数器进行增加操作
            int newValue = counter.incrementAndGet();

            // 打印当前线程名称和增加后的值
            System.out.println("Thread " + Thread.currentThread().getName() + ": Counter value = " + newValue);
        }
    }

    @Test
    public void CasToAbaTest(){
        // 线程1对共享变量进行更新操作，将其从A变为B再变回A
        Thread thread1 = new Thread(() -> {
            int stamp = atomicRef.getStamp();
            String value = atomicRef.getReference();

            // 更新共享变量为B
            Boolean isToB = atomicRef.compareAndSet(value, "B", stamp, stamp + 1);
            System.out.println("更新共享变量为B:{}"+isToB);
            stamp++;
            value = atomicRef.getReference();

            // 更新共享变量为A
            Boolean isToA = atomicRef.compareAndSet(value, "A", stamp, stamp + 1);
            System.out.println("更新共享变量为A:{}"+isToA);
        });

        // 线程2对共享变量进行判断和更新操作
        Thread thread2 = new Thread(() -> {
            int stamp = atomicRef.getStamp();
            String value = atomicRef.getReference();

            // 如果共享变量的值为A，则将其更新为C
            if (value.equals("A")) {
                Boolean isToC = atomicRef.compareAndSet(value, "C", stamp, stamp + 1);
                System.out.println("如果共享变量的值为A，则将其更新为C:{}"+isToC);
            }
        });

        thread1.start();
        thread2.start();
    }
}
