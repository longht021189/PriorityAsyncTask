package p.a.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

final class DefaultScheduler implements Contract.Runnable {

    /** Task List */
    private final List<Runnable> mTasks = new ArrayList<>();

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     */
    public boolean add(Runnable scheduler) {
        return mTasks.add(scheduler);
    }

    /**
     * Runs before {@link #run()}.
     * Empty method
     */
    @Override
    public void onPreExecute() {}

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
    public void run() {

    }

    /**
     * Runs after {@link #run()}.
     * Empty method
     */
    @Override
    public void onPostExecute() {}

    /**
     * Return Priority for Scheduler
     *
     * @return Integer
     */
    @Override
    public int getPriority() {
        return 0;
    }
}
