package p.a.task;

import android.support.annotation.NonNull;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public abstract class Executor implements Contract.Executor {

    /** Scheduler List */
    private final SchedulerList mSchedulerList;
    /** Default Scheduler */
    private final DefaultScheduler mDefaultScheduler;

    /**
     * Default Constructor
     */
    public Executor() {
        mDefaultScheduler = new DefaultScheduler();

        mSchedulerList = new SchedulerList();
        mSchedulerList.add(mDefaultScheduler);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution
     * @throws NullPointerException       if command is null
     */
    @Override
    public void execute(@NonNull Runnable command) {
        if (command instanceof Scheduler) {
            mSchedulerList.add((Scheduler) command);
        }
        else {
            mDefaultScheduler.add(command);
        }
    }
}
