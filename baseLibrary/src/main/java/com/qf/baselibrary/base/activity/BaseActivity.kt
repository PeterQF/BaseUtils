package com.qf.baselibrary.base.activity

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qf.baselibrary.base.application.BaseApplication
import com.qf.baselibrary.utils.BarUtils


/**
 * 作者：PeterWu
 * 时间：2020/12/5
 * 描述：BaseActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initStatusBar()
        initViewModel()
        observe()
        init(savedInstanceState)
    }

    /**
     * 观察数据
     */
    open fun observe() {}

    open fun initViewModel() {}

    open fun initStatusBar() {
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
    }

    /**
     * 通过activity获取viewModel，跟随activity生命周期
     */
    protected fun <T : ViewModel?> getActivityViewModel(modelClass: Class<T>): T? {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider?.get(modelClass)
    }

    protected open fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider = ViewModelProvider(
                this.applicationContext as BaseApplication,
                getAppFactory(this)
            )
        }
        return mApplicationProvider!![modelClass]
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application: Application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.getApplication()
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    abstract fun init(savedInstanceState: Bundle?)

    abstract fun getLayoutId(): Int
}