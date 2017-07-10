package p.a.task;

import android.os.Handler;
import android.support.annotation.NonNull;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/9/17.
 */

final class Contract {

    interface Runnable extends java.lang.Runnable {

        /**
         * Runs before {@link #run()}.
         */
        void onPreExecute();

        /**
         * Runs after {@link #run()}
         */
        void onPostExecute();

        /**
         * Return Priority for Scheduler
         *
         * @return Integer
         */
        int getPriority();

        /**
         * Returns the number of elements in this list.  If this list contains
         * more than <tt>Integer.MAX_VALUE</tt> elements, returns
         * <tt>Integer.MAX_VALUE</tt>.
         *
         * @return the number of elements in this list
         */
        int size();
    }

    interface Executor extends java.util.concurrent.Executor {

        /**
         * Called when {@link Contract.Runnable#onPreExecute()} is finish
         */
        void onPreExecuteCallback(@NonNull Runnable command);

        /**
         * Called when {@link Contract.Runnable#onPostExecute()} is finish
         */
        void onPostExecuteCallback(@NonNull Runnable command);
    }

    interface Helper {

        /**
         * Causes the Runnable r to be added to the message queue.
         * The runnable will be run on the thread to which this handler is
         * attached.
         *
         * @param command The Runnable that will be executed.
         */
        void post(java.lang.Runnable command);
    }

    static class HandlerHelper implements Helper {

        private final Handler mHandler;

        /**
         * Default Constructor
         * @param handler Handler
         */
        HandlerHelper(Handler handler) {
            mHandler = handler;
        }

        /**
         * Causes the Runnable r to be added to the message queue.
         * The runnable will be run on the thread to which this handler is
         * attached.
         *
         * @param command The Runnable that will be executed.
         */
        @Override
        public void post(java.lang.Runnable command) {
            mHandler.post(command);
        }
    }

    static class ExecutorHelper implements Helper {

        private final java.util.concurrent.Executor mExecutor;

        /**
         * Default Constructor
         * @param executor Executor
         */
        ExecutorHelper(java.util.concurrent.Executor executor) {
            mExecutor = executor;
        }

        /**
         * Causes the Runnable r to be added to the message queue.
         * The runnable will be run on the thread to which this handler is
         * attached.
         *
         * @param command The Runnable that will be executed.
         */
        @Override
        public void post(java.lang.Runnable command) {
            mExecutor.execute(command);
        }
    }

    static class PostPreExecute implements java.lang.Runnable {

        private final Runnable mRunnable;
        private final Executor mExecutor;

        PostPreExecute(Runnable runnable, Executor executor) {
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
            mRunnable.onPreExecute();
            mExecutor.onPreExecuteCallback(mRunnable);
        }
    }

    static class PostPostExecute implements java.lang.Runnable {

        private final Runnable mRunnable;
        private final Executor mExecutor;

        PostPostExecute(Runnable runnable, Executor executor) {
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
            mRunnable.onPostExecute();
            mExecutor.onPostExecuteCallback(mRunnable);
        }
    }
}
