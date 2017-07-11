package p.a.task;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Huynh Thanh Long.
 * Date: 7/10/17.
 */

public class ManagerTest {

    private final int mPoolSize = 4;
    private final Executor mMainExecutor = Executors.newSingleThreadExecutor();
    private final Executor mBackgroundExecutor = Executors.newFixedThreadPool(mPoolSize);
    private final Manager mManager = new Manager(mPoolSize, mMainExecutor, mBackgroundExecutor);

    @Test
    public void testDefaultScheduler() {
        final AtomicInteger number = new AtomicInteger(0);

        for (int i = 0; i < 100; ++i) {
            final int k = i;
            mManager.execute(new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(k, number.getAndIncrement());
                }
            });
        }
    }

    @Test
    public void testMultiSchedulers() {

    }
}
