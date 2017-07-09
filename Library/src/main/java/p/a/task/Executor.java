package p.a.task;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public class Executor implements Contract.Executor {

    /** Scheduler List */
    private final SchedulerList mSchedulerList;
    /** Default Scheduler */
    private final DefaultScheduler mDefaultScheduler;
    /** Attach Command into Main Thread */
    private Contract.Helper mMainHelper;
    /** Attach Command into Background Thread */
    private Contract.Helper mBackgroundHelper;

    /**
     * Default Constructor
     */
    private Executor() {
        mDefaultScheduler = new DefaultScheduler();

        mSchedulerList = new SchedulerList();
        mSchedulerList.add(mDefaultScheduler);
    }

    /**
     * Constructor
     * @param main       Main Thread
     * @param background Background Thread
     */
    public Executor(@NonNull Handler main,
                    @NonNull java.util.concurrent.Executor background) {
        this();

        mMainHelper = new Contract.HandlerHelper(main);
        mBackgroundHelper = new Contract.ExecutorHelper(background);
    }

    /**
     * Constructor
     * @param main       Main Thread
     * @param background Background Thread
     */
    public Executor(@NonNull java.util.concurrent.Executor main,
                    @NonNull java.util.concurrent.Executor background) {
        this();

        mMainHelper = new Contract.ExecutorHelper(main);
        mBackgroundHelper = new Contract.ExecutorHelper(background);
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
            if (mSchedulerList.add((Scheduler) command)) {
                // TODO Add more info
            }
        }
        else {
            mDefaultScheduler.add(command);
        }
    }
}
