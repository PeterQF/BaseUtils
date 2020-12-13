package com.qf.baselibrary.base.fragment

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.qf.baselibrary.base.application.BaseApplication
import java.lang.reflect.ParameterizedType

/**
 * 作者：PeterWu
 * 时间：2020/12/6
 * 描述：BaseFragment
 */
abstract class BaseFragment<VB: ViewBinding> : Fragment() {

    private lateinit var mActivity: AppCompatActivity
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null
    private var lazyLoaded = false
    private var _binding: VB? = null
    //TODO 这个作用域只适用于onCreateView 和 onDestroyView之间
    // 不要在超过这两个生命周期之外的地方调用，否则空指针
    protected val binding get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        //TODO 必须要在Activity与Fragment绑定后，因为如果Fragment可能获取的是Activity中ViewModel
        // 必须在onCreateView之前初始化viewModel，因为onCreateView中需要通过ViewModel与DataBinding绑定(如果使用DataBinding的话)
        initViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        _binding = method.invoke(null,layoutInflater,container,false) as VB
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
        //observe一定要在初始化最后，因为observe会收到黏性事件，随后对ui做处理
        observe()
    }

    /**
     * 初始化入口
     */
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 初始化viewModel
     * 之所以没有设计为抽象，是因为部分简单activity可能不需要viewModel
     * observe同理
     */
    open fun initViewModel() {}

    /**
     * 懒加载数据
     */
    open fun lazyLoadData() {}

    /**
     * 注册观察者
     */
    open fun observe() {}

    override fun onResume() {
        super.onResume()
        if (lazyLoaded.not()) {
            lazyLoadData()
            lazyLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        fragment.activity ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }
}