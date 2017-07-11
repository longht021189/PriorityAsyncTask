package p.a.task;

import java.util.ArrayDeque;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

final class DefaultScheduler implements Contract.Runnable {

    /** Task List */
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     */
    public boolean add(Runnable scheduler) {
        return mTasks.offer(scheduler);
    }

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
    @Override
    public final int size() {
        return mTasks.size();
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
        Runnable task;
        if ((task = mTasks.poll()) != null) {
            task.run();
        }
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
