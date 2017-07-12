package p.a.task;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/10/17.
 */

public class SchedulerTest {

    @Test
    public void testRun() throws Exception {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; ++i) {
            tasks.add(new MyTask());
        }

        MyScheduler scheduler = new MyScheduler(tasks);
        Assert.assertEquals(1000, scheduler.size());

        while (scheduler.size() > 0) {
            scheduler.run();
        }

        Assert.assertEquals(0, scheduler.size());
    }

    @Test
    public void testInterrupt() throws Exception {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 1000; ++i) {
            tasks.add(new MyTask());
        }

        MyScheduler scheduler = new MyScheduler(tasks);
        Assert.assertEquals(1000, scheduler.size());

        int k = 0;
        try {
            while (scheduler.size() > 0) {
                scheduler.run();
                k++;
            }
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InterruptedException);
        }

        Assert.assertTrue(k < 1000);
        Assert.assertEquals(0, scheduler.size());
    }

    private static class MyTask implements Runnable {
        private static int mCount = 0;
        private static int mRun = 0;
        private final int mIndex = mCount++;
        private final long mTime;

        MyTask() {
            mTime = 0;
        }
        MyTask(long time) {
            mTime = time;
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
        public void run() throws InterruptedException {
            Assert.assertEquals(mIndex, mRun++);

            if (mTime != 0) {
                Thread.sleep(mTime);
            }
        }
    }

    private static class MyScheduler extends Scheduler {

        /**
         * Create Scheduler with Task List
         *
         * @param tasks Task List
         */
        MyScheduler(List<Runnable> tasks) {
            super(tasks);
        }

        /**
         * Runs before {@link #run()}.
         */
        @Override
        public void onPreExecute() {

        }

        /**
         * Runs after {@link #run()}
         */
        @Override
        public void onPostExecute() {

        }

        /**
         * Return Priority for Scheduler
         *
         * @return Integer, must constant, because it's calculated 1 time
         * When it's execute by {@link Manager}
         */
        @Override
        public int getPriority() {
            return 0;
        }
    }
}
