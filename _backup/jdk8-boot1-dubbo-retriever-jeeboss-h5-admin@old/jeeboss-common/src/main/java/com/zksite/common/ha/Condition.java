package com.zksite.common.ha;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Condition {

    private Lock lock = new ReentrantLock();

    private final Logger logger = LoggerFactory.getLogger(Condition.class);

    private java.util.concurrent.locks.Condition condition = lock.newCondition();

    public void await() {
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        try {

            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Condition condition = new Condition();
        new Thread() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "---开始等待");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "---等待结束");
            }

        }.start();
        
        new Thread() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "---开始睡眠");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "---唤醒结束");
            }

        }.start();
        
        new Thread() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "---开始睡眠");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "---唤醒结束");
            }

        }.start();
    }
}
