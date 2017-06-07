package vn.android.myapplication.ui.screen.main

import vn.android.myapplication.Application
import vn.android.myapplication.R
import vn.android.myapplication.ui.screen.base.BaseActivity
import javax.inject.Inject

/**
 * Created by Huynh Thanh Long
 * Date: 05/31/17.
 */
class MainActivity : BaseActivity(), MainPresentation {

    /**
     * Layout Id
     * Which is used with #setContentView
     * @see setContentView
     */
    override val layoutId = R.layout.activity_main

    /**
     * Main Presenter Instance
     */
    @Inject lateinit var mPresenter: MainPresenter

    /**
     * Setup Dagger Injection for Current Activity
     */
    override fun setupInjection(application: Application) {

    }
}
