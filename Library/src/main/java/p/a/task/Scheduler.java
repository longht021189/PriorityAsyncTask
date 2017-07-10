package p.a.task;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public abstract class Scheduler implements Contract.Runnable {

    /** Task List */
    private final ArrayDeque<Runnable> mTasks;

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
        Runnable task;
        if ((task = mTasks.poll()) != null) {
            task.run();
        }
    }

    /**
     * Return Priority for Scheduler
     * @return Integer, must constant, because it's calculated 1 time
     *         When it's execute by {@link Executor}
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
    public int size() {
        return mTasks.size();
    }
}
