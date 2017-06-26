package p.a.task;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

/**
 * Created by Huynh Thanh Long
 * Date: 06/26/17.
 */
public interface TaskRunner {

    /**
     * Override this method to return id string for checking purpose
     * if 2 tasks have same id, they will run synchronized
     * @return Id String
     */
    @NonNull
    String getId();

    /**
     * Override this method to perform a computation on a background thread.
     */
    @WorkerThread
    void doInBackground();
}
