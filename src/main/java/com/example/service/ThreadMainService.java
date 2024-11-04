package com.example.service;

import org.junit.Test;

/**
 * @title: ThreadMain
 * @description: TODO
 * @date 2019/6/6
 */
public class ThreadMainService {


//    @Test
    public void TestThreadJoin()throws InterruptedException{
        // 创建线程 B
        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动线程 Thread-a
                ThreadAService threadAService = new ThreadAService("Thread-a");
                try {
                    // 加入 join 方法
                    threadAService.getThread().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 线程B的业务逻辑
                System.out.println("开始线程 " + Thread.currentThread().getName() + "业务逻辑");
                for (int i = 6; i > 0 ; i--) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " : " + i);
                        // 睡眠0.5秒
                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + " 线程执行异常！");
                        e.printStackTrace();
                    }
                }

                System.out.println(Thread.currentThread().getName() + " 线程结束！");

            }
        }, "Thread-B");

        // 启动线程B
        threadB.start();
        // 加入 join 方法
        threadB.join();

        // 主线程业务逻辑开始
        System.out.println("主线程业务逻辑开始");
        for (int i = 6; i > 0 ; i--) {
            try {
                System.out.println(Thread.currentThread().getName() + " : " + i);
                // 睡眠0.3秒
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 线程执行异常！");
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() + " 线程结束！");
    }



//    @Test
    public void TestThread()throws InterruptedException{
        // 创建线程 B
        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                // 启动线程 Thread-a
                ThreadAService threadAService = new ThreadAService("Thread-a");
                // 线程B的业务逻辑
                System.out.println("开始线程 " + Thread.currentThread().getName() + "业务逻辑");
                for (int i = 6; i > 0 ; i--) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " : " + i);
                        // 睡眠0.5秒
                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + " 线程执行异常！");
                        e.printStackTrace();
                    }
                }

                System.out.println(Thread.currentThread().getName() + " 线程结束！");

            }
        }, "Thread-B");

        // 启动线程B
        threadB.start();

        // 主线程业务逻辑开始
        System.out.println("主线程业务逻辑开始");
        for (int i = 6; i > 0 ; i--) {
            try {
                System.out.println(Thread.currentThread().getName() + " : " + i);
                // 睡眠0.3秒
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 线程执行异常！");
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() + " 线程结束！");
    }
}

