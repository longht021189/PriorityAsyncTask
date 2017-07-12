package p.a.task;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public abstract class Scheduler implements Contract.Runnable {

    /** Task List */
    private final ArrayDeque<Runnable> mTasks;
    /** Cancel Flag */
    private final AtomicBoolean mCancelled = new AtomicBoolean(false);
    private final AtomicReference<Thread> mThread = new AtomicReference<>();

    /**
     * Create Scheduler with Task Array
     * @param tasks Task Array
     */
    public Scheduler(Runnable... tasks) {
        mTasks = new ArrayDeque<>();
        Collections.addAll(mTasks, tasks);
    }

    /**
     * Create Scheduler with Task List
     * @param tasks Task List
     */
    public Scheduler(List<Runnable> tasks) {
        mTasks = new ArrayDeque<>(tasks);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public final void run() {
        synchronized (mCancelled) {
            if (mCancelled.get()) {
                return;
            }
        }

        try {
            synchronized (mThread) {
                mThread.set(Thread.currentThread());
            }

            Runnable task;
            if ((task = mTasks.poll()) != null) {
                task.run();
            }
        } finally {
            synchronized (mThread) {
                mThread.set(null);
            }
        }
    }

    /**
     * Return Priority for Scheduler
     * @return Integer, must constant, because it's calculated 1 time
     *         When it's execute by {@link Manager}
     */
    @Override
    public abstract int getPriority();

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
    @Override
    public final int size() {
        synchronized (mCancelled) {
            if (mCancelled.get()) {
                return 0;
            }
        }

        return mTasks.size();
    }

    /**
     * <p>Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when <tt>cancel</tt> is called,
     * this task should never run. If the task has already started,
     * then the <tt>mayInterruptIfRunning</tt> parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.</p>
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *        task should be interrupted; otherwise, in-progress tasks are allowed
     *        to complete.
     */
    public final void cancel(boolean mayInterruptIfRunning) {
        synchronized (mCancelled) {
            mCancelled.set(true);
        }

        if (mayInterruptIfRunning) {
            synchronized (mThread) {
                if (mThread.get() != null) {
                    mThread.get().interrupt();
                }
            }
        }
    }
}
