package com.qf.baselibrary.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qf.baselibrary.base.http.ApiException

/**
 * 作者：PeterWu
 * 时间：2020/12/6
 * 描述：BaseViewModel
 */
open class BaseViewModel : ViewModel() {

    /**
     * 错误信息liveData
     */
    val errorLiveData = MutableLiveData<ApiException>()
}