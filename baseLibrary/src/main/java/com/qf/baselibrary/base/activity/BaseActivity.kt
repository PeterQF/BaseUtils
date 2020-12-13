package com.qf.baselibrary.base.activity

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.qf.baselibrary.base.application.BaseApplication
import com.qf.baselibrary.utils.BarUtils
import java.lang.reflect.ParameterizedType


/**
 * 作者：PeterWu
 * 时间：2020/12/5
 * 描述：BaseActivity
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB by lazy {
        // 使用反射得到viewBinding的class
        // 返回当前类的父类type，也就是BaseActivity
        val type = javaClass.genericSuperclass as ParameterizedType
        // 获得泛型中的实际类型，可能会存在多个泛型，[0]也就是VB的type
        val aClass = type.actualTypeArguments[0] as Class<*>
        // 反射"inflate"
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        // 方法调用
        method.invoke(null, layoutInflater) as VB

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initStatusBar()
        initViewModel()
        init(savedInstanceState)
        observe()
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
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    abstract fun init(savedInstanceState: Bundle?)
}