package com.qf.baseutil

import android.content.Context
import android.content.pm.ApplicationInfo

/**
 * 作者：PeterWu
 * 时间：2020/10/20
 * 描述：BaseUtilHelper
 */
class BaseUtils private constructor(){

    private var mContext: Context? = null

    companion object {
        val instance: BaseUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BaseUtils()
        }
    }

    fun init(context: Context) {
        this.mContext = context
    }

    fun getContext(): Context {
        if (mContext != null) {
            return mContext!!.applicationContext
        } else {
            throw Exception("Please call method 'init()' first to get context")
        }
    }

    fun isDebug(context: Context): Boolean {
        return context.applicationInfo != null && (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
    }
}