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

public class Executor implements Contract.Executor, Runnable {

    /** Scheduler List */
    private final SchedulerList mSchedulerList;
    /** Default Scheduler */
    private final DefaultScheduler mDefaultScheduler;
    /** Scheduler Status List */
    private final Map<Contract.Runnable, Status> mSchedulerStatus;
    /** Locker for Sync Thread */
    private final Object mLocker;

    /** Pool Worker Size, It must correct with {@link #mBackgroundHelper} */
    private int mPoolSize;
    /** Pool Worker is Running */
    private volatile int mPoolCount;
    /** Attach Command into Main Thread */
    private Contract.Helper mMainHelper;
    /** Attach Command into Background Thread */
    private Contract.Helper mBackgroundHelper;

    /**
     * Default Constructor
     */
    private Executor() {
        mLocker = new Object();
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
        synchronized (mLocker) {
            if (command instanceof Scheduler) {
                Scheduler scheduler = (Scheduler) command;

                if (mSchedulerList.add(scheduler)) {
                    mSchedulerStatus.put(scheduler, Status.Doing_onPreExecute);
                    mMainHelper.post(new Contract.PostPreExecute(scheduler, this));
                }
            } else {
                mDefaultScheduler.add(command);
                onPreExecuteCallback(mDefaultScheduler);
            }
        }
    }

    /**
     * Called when {@link Contract.Runnable#onPreExecute()} is finish
     */
    @Override
    public void onPreExecuteCallback(@NonNull Contract.Runnable command) {
        synchronized (mLocker) {
            mSchedulerStatus.put(command, Status.Await);
            checkAndRunScheduler();
        }
    }

    /**
     * Called when {@link Contract.Runnable#onPostExecute()} is finish
     */
    @Override
    public void onPostExecuteCallback(@NonNull Contract.Runnable command) {
        synchronized (mLocker) {
            if (command instanceof DefaultScheduler) {
                mSchedulerStatus.remove(command);
                mSchedulerList.remove(command);
            } else {
                mSchedulerStatus.put(command, Status.Await);
            }

            checkAndRunScheduler();
        }
    }

    /**
     * Check Pool Worker is Available for Run Scheduler
     * If yes, that scheduler will run.
     *
     * Note: Await   <-> Running,
     *       Running  -> Doing_onPostExecute
     */
    private void checkAndRunScheduler() {
        synchronized (mLocker) {
            int size = mSchedulerList.size();
            int awaitCount = 0;

            for (int i = 0; i < size; ++i) {
                Contract.Runnable runnable = mSchedulerList.get(i);

                if (mSchedulerStatus.get(runnable) == Status.Await && runnable.size() > 0) {
                    awaitCount++;
                }
            }

            int min = Math.min(awaitCount, mPoolSize);

            for (int i = mPoolCount; i < min; ++i) {
                mBackgroundHelper.post(this);
                mPoolCount++;
            }
        }
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
        Contract.Runnable runnable;

        do {
            runnable = null;

            synchronized (mLocker) {
                int size = mSchedulerList.size();

                for (int i = 0; i < size; ++i) {
                    Contract.Runnable r = mSchedulerList.get(i);

                    if (mSchedulerStatus.get(r) == Status.Await && r.size() > 0) {
                        runnable = r;
                        break;
                    }
                }

                mSchedulerStatus.put(runnable, Status.Running);
            }

            if (runnable != null) {
                runnable.run();

                if (runnable.size() == 0) {

                    runnable = null;
                }
            }
        }
        while (runnable != null);

        mPoolCount--;
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
}
