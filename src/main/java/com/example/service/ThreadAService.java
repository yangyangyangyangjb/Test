package com.example.service;

/**
 * @title: ThreadA
 * @description: TODO
 * @date 2019/6/6
 */
public class ThreadAService implements Runnable{

    private String threadName;

    private Thread thread;

    // 构造方法中就启用线程
    ThreadAService(String threadName) {
        this.threadName = threadName;
        thread = new Thread(this, threadName);
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("线程" + Thread.currentThread().getName() + "业务逻辑开始！");
        for(int i = 6; i > 0 ; i--) {
            // 线程睡眠1秒
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " : " + i);
            }  catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + "线程执行异常！");
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " 线程结束！");
    }

    public String getThreadName() {
        return threadName;
    }

    public Thread getThread() {
        return thread;
    }
}