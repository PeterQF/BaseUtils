package com.qf.baselibrary.base.fragment

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qf.baselibrary.base.application.BaseApplication

/**
 * 作者：PeterWu
 * 时间：2020/12/6
 * 描述：BaseFragment
 */
abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var mActivity: AppCompatActivity
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    protected open fun <T : ViewModel?> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    protected fun <T : ViewModel?> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(mActivity)
        }
        return mActivityProvider!![modelClass]
    }

    protected fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider = ViewModelProvider(
                mActivity.applicationContext as BaseApplication,
                getApplicationFactory(mActivity)
            )
        }
        return mApplicationProvider!![modelClass]
    }

    private fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    private fun checkActivity(fragment: Fragment) {
        val activity = fragment.activity ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }
}