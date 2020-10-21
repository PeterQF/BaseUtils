package com.qf.baseutils.log

import android.util.Log
import com.qf.baseutils.BaseUtils


/**
 * 作者：PeterWu
 * 时间：2020/6/27
 * 描述：LogUtils
 */
object LogUtils {

    private val DEBUG_MODE by lazy {
        BaseUtils.instance.isDebug(BaseUtils.instance.getContext())
    }

    fun error(msg: String) {
        if (DEBUG_MODE){
            Log.e(BaseUtils.instance.getContext().packageName, msg)
        }
    }

    fun info(msg: String) {
        if (DEBUG_MODE){
            Log.i(BaseUtils.instance.getContext().packageName, msg)
        }
    }

    fun debug(msg: String) {
        if (DEBUG_MODE) {
            Log.d(BaseUtils.instance.getContext().packageName, msg)
        }
    }

    fun verbose(msg: String) {
        if (DEBUG_MODE){
            Log.v(BaseUtils.instance.getContext().packageName, msg)
        }
    }
}