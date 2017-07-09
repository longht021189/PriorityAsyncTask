package p.a.task;

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
}
