package p.a.task;

import android.os.Handler;

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
    }

    interface Executor extends java.util.concurrent.Executor {

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
}
