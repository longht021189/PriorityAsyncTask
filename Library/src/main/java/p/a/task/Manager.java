package p.a.task;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

public class Manager implements Contract.Executor, Runnable {

    /** Scheduler List */
    private final SchedulerList mSchedulerList;
    /** Default Scheduler */
    private final DefaultScheduler mDefaultScheduler;
    /** Scheduler Status List */
    private final Map<Contract.Runnable, Status> mSchedulerStatus;

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
    private Manager() {
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
    public Manager(int poolSize, @NonNull Handler main, @NonNull Executor background) {
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
    public Manager(int poolSize, @NonNull Executor main, @NonNull Executor background) {
        this();

        mPoolSize = poolSize;
        mMainHelper = new Contract.ExecutorHelper(main);
        mBackgroundHelper = new Contract.ExecutorHelper(background);
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
     * @param command               Which is cancelled
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete.
     */
    @Override synchronized
    public void cancel(@NonNull Runnable command, boolean mayInterruptIfRunning) {
        if (command instanceof Scheduler) {
            Scheduler scheduler = (Scheduler) command;
            scheduler.cancel(mayInterruptIfRunning);
        } else {
            // TODO ....
        }
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
    @Override synchronized
    public void execute(@NonNull Runnable command) {
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

    /**
     * Called when {@link Contract.Runnable#onPreExecute()} is finish
     */
    @Override synchronized
    public void onPreExecuteCallback(@NonNull Contract.Runnable command) {
        if (command != mDefaultScheduler) {
            mSchedulerStatus.put(command, Status.Await);
        }
        checkAndRunScheduler();
    }

    /**
     * Called when {@link Contract.Runnable#onPostExecute()} is finish
     */
    @Override synchronized
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
    synchronized
    private void checkAndRunScheduler() {
        // Check Pool is full
        if (mPoolCount == mPoolSize) return;

        // Get Pool Count to Start
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
            mPoolCount++;
            mBackgroundHelper.post(this);
        }
    }

    /**
     * Get Top Scheduler Which has Await Status
     * And Change its status to Running
     * @return Runnable instance
     */
    synchronized
    private Contract.Runnable getSchedulerForRun(Contract.Runnable prevScheduler) {
        Contract.Runnable runnable = null;
        int size = mSchedulerList.size();

        for (int i = 0; i < size; ++i) {
            Contract.Runnable r = mSchedulerList.get(i);

            if (mSchedulerStatus.get(r) == Status.Await && r.size() > 0) {
                runnable = r;
                break;
            }
        }

        if (runnable != null) {
            mSchedulerStatus.put(runnable, Status.Running);
        }

        if (prevScheduler != null && runnable != prevScheduler
                && mSchedulerStatus.get(prevScheduler) == Status.Running) {
            mSchedulerStatus.put(prevScheduler, Status.Await);
        }

        return runnable;
    }

    /**
     * Post Post Execute
     * @param runnable PostExecute
     */
    synchronized
    private void postPostExecute(Contract.Runnable runnable) {
        if (runnable != mDefaultScheduler) {
            mSchedulerStatus.put(runnable, Status.Doing_onPostExecute);
            mMainHelper.post(new Contract
                    .PostPostExecute(runnable, this));
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
        Contract.Runnable runnable = null;

        do {
            runnable = getSchedulerForRun(runnable);

            if (runnable != null) {
                runnable.run();

                if (runnable.size() == 0) {
                    postPostExecute(runnable);
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
