package p.a.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public abstract class Scheduler implements Contract.Runnable {

    /** Task List */
    private final List<Runnable> mTasks;

    /**
     * Create Scheduler with Task Array
     * @param tasks Task Array
     */
    public Scheduler(Runnable... tasks) {
        mTasks = new ArrayList<>();
        Collections.addAll(mTasks, tasks);
    }

    /**
     * Create Scheduler with Task List
     * @param tasks Task List
     */
    public Scheduler(List<Runnable> tasks) {
        mTasks = new ArrayList<>(tasks);
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

    }

    /**
     * Return Priority for Scheduler
     * @return Integer, must constant, because it's calculated 1 time
     *         When it's execute by {@link Executor}
     */
    @Override
    public abstract int getPriority();
}
