package org.ow2.ipojo.toolkit.pause.internal;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.felix.ipojo.MethodInterceptor;

public class PausingInterceptor implements MethodInterceptor {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock read = lock.readLock();
    private Lock write = lock.writeLock();
    private boolean locked = false;

    public void onEntry(Object pojo, Method method, Object[] args) {
        read.lock();
    }

    public void onFinally(Object pojo, Method method) {
        read.unlock();
    }

    public void onExit(Object pojo, Method method, Object returnedObj) { }

    public void onError(Object pojo, Method method, Throwable throwable) { }

    public void engage() {
        if (!locked) {
            write.lock();
            locked = true;
        }
    }

    public void disengage() {
        if (locked) {
            write.unlock();
            locked = false;
        }
    }

}
