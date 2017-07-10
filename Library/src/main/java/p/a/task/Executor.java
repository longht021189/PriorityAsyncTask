package p.a.task;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
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
    /** Scheduler Status List */
    private final Map<Contract.Runnable, Status> mSchedulerStatus;

    /** Pool Worker Size, It must correct with {@link #mBackgroundHelper} */
    private int mPoolSize;
    /** Pool Worker is Running */
    private int mPoolCount;
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

        mSchedulerStatus = new HashMap<>();
        mSchedulerStatus.put(mDefaultScheduler, Status.Await);

        mPoolCount = 0;
    }

    /**
     * Constructor
     * @param main       Main Thread
     * @param background Background Thread
     */
    public Executor(int poolSize, @NonNull Handler main,
                    @NonNull java.util.concurrent.Executor background) {
        this();

        mPoolSize = poolSize;
        mMainHelper = new Contract.HandlerHelper(main);
        mBackgroundHelper = new Contract.ExecutorHelper(background);
    }

    /**
     * Constructor
     * @param main       Main Thread
     * @param background Background Thread
     */
    public Executor(int poolSize, @NonNull java.util.concurrent.Executor main,
                    @NonNull java.util.concurrent.Executor background) {
        this();

        mPoolSize = poolSize;
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
            Scheduler scheduler = (Scheduler) command;

            if (mSchedulerList.add(scheduler)) {
                mSchedulerStatus.put(scheduler, Status.Doing_onPreExecute);
                mMainHelper.post(new Contract.PostPreExecute(scheduler, this));
            }
        }
        else {
            mDefaultScheduler.add(command);
            onPreExecuteCallback(mDefaultScheduler);
        }
    }

    /**
     * Called when {@link Contract.Runnable#onPreExecute()} is finish
     */
    @Override
    public void onPreExecuteCallback(@NonNull Contract.Runnable command) {
        mSchedulerStatus.put(command, Status.Await);
        checkAndRunScheduler();
    }

    /**
     * Called when {@link Contract.Runnable#onPostExecute()} is finish
     */
    @Override
    public void onPostExecuteCallback(@NonNull Contract.Runnable command) {
        if (command instanceof DefaultScheduler) {
            mSchedulerStatus.remove(command);
            mSchedulerList.remove(command);
        } else {
            mSchedulerStatus.put(command, Status.Await);
        }

        checkAndRunScheduler();
    }

    /**
     * Check Pool Worker is Available for Run Scheduler
     * If yes, that scheduler will run.
     *
     * Note: Await   <-> Running,
     *       Running  -> Doing_onPostExecute
     */
    private void checkAndRunScheduler() {
        while (mPoolCount < mPoolSize) {
            int size = mSchedulerList.size();

            for (int i = 0; i < size; ++i) {
                Contract.Runnable runnable = mSchedulerList.get(i);

                if (mSchedulerStatus.get(runnable) == Status.Await) {
                    if (!(runnable instanceof DefaultScheduler)
                            || ((DefaultScheduler)runnable).size() > 0) {
                        mSchedulerStatus.put(runnable, Status.Running);
                        mBackgroundHelper.post(new AttachSchedulerRunnable(runnable, this));
                        mPoolCount++;
                    }
                }
            }
        }
    }

    /**
     * Scheduler Status
     */
    private enum Status {
        Doing_onPreExecute,
        Doing_onPostExecute,
        Await,
        Running
    }

    private static class AttachSchedulerRunnable implements Runnable {

        private final Contract.Runnable mRunnable;
        private final Executor mExecutor;

        AttachSchedulerRunnable(Contract.Runnable runnable, Executor executor) {
            this.mRunnable = runnable;
            this.mExecutor = executor;
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
        public void run() {
            mRunnable.run();
        }
    }
}
