package vn.android.myapplication.ui.screen.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import vn.android.myapplication.Application
import vn.android.myapplication.R

/**
 * Created by Huynh Thanh Long
 * Date: 05/31/17.
 */
abstract class BaseActivity: AppCompatActivity() {

    /**
     * Layout Id
     * Which is used with #setContentView
     * @see setContentView
     */
    abstract val layoutId: Int

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(android.net.Uri , String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     *
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInjection(application as Application)
        setContentView(R.layout.activity_main)
    }

    /**
     * Setup Dagger Injection for Current Activity
     */
    abstract fun setupInjection(application: Application)
}