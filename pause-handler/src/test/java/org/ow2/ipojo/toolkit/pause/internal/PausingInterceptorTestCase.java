package org.ow2.ipojo.toolkit.pause.internal;

import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 02/12/10
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class PausingInterceptorTestCase {
    @Test
    public void testLockingMechanismEngaged() throws Exception {
        final PausingInterceptor pause = new PausingInterceptor();
        final Holder holder = new Holder();
        pause.engage();

        Runnable run = new Runnable() {
            public void run() {
                pause.onEntry(null, null, null);
                holder.set();
            }
        };

        Thread thread = new Thread(run);

        assertFalse(holder.get());
        thread.start();

        Thread.sleep(50);
        assertFalse(holder.get());

        pause.disengage();

        Thread.sleep(50);
        assertTrue(holder.get());
    }

    @Test
    public void testLockingMechanismEngagedWithMultipleThreads() throws Exception {
        final PausingInterceptor pause = new PausingInterceptor();
        final Holder holder1 = new Holder();
        final Holder holder2 = new Holder();
        pause.engage();

        Runnable run1 = new Runnable() {
            public void run() {
                pause.onEntry(null, null, null);
                holder1.set();
            }
        };

        Thread thread1 = new Thread(run1);

        Runnable run2 = new Runnable() {
            public void run() {
                pause.onEntry(null, null, null);
                holder2.set();
            }
        };

        Thread thread2 = new Thread(run2);

        assertFalse(holder1.get());
        assertFalse(holder2.get());
        thread1.start();
        thread2.start();

        Thread.sleep(50);
        assertFalse(holder1.get());
        assertFalse(holder2.get());

        pause.disengage();

        Thread.sleep(50);
        assertTrue(holder1.get());
        assertTrue(holder2.get());
    }

    @Test
    public void testLockingMechanismDisengaged() throws Exception {
        final PausingInterceptor pause = new PausingInterceptor();
        final Holder holder = new Holder();

        Runnable run = new Runnable() {
            public void run() {
                pause.onEntry(null, null, null);
                holder.set();
            }
        };

        Thread thread = new Thread(run);

        assertFalse(holder.get());
        thread.start();

        Thread.sleep(50);
        assertTrue(holder.get());
    }

    private class Holder {
        private boolean done;

        public void set() {
            done = true;
        }

        public boolean get() {
            return done;
        }
    }
}
