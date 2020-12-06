package com.qf.baselibrary.base.http

import androidx.lifecycle.MutableLiveData
import com.qf.baselibrary.ext.toast
import kotlinx.coroutines.*
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 作者：PeterWu
 * 时间：2020/12/6
 * 描述：BaseRepository
 */

typealias Block<T> = suspend () -> T
typealias Error = suspend (e: ApiException) -> Unit

open class BaseRepository(
    private val coroutineScope: CoroutineScope,
    private val errorLiveData: MutableLiveData<ApiException>
) {


    /**
     * 创建并执行协程
     */
    protected fun<T> launch(block: Block<T>, success: suspend (T) -> Unit, error: Error? = null): Job {
        return coroutineScope.launch {
            runCatching {
                block.invoke()
            }
                .onSuccess {
                    success.invoke(it)
                }
                .onFailure {
                    getApiException(it).apply {
                        error?.invoke(this)
                        errorMessage?.let { msg -> toast(msg) }
                        errorLiveData.postValue(this)
                    }
                }
        }
    }

    /**
     * 执行并行协程
     * 用于同时发起多个请求
     */
    protected fun <T> async(block: Block<T>): Deferred<T> {
        return coroutineScope.async { block.invoke() }
    }

    /**
     * 捕获异常信息
     */
    private fun getApiException(e: Throwable): ApiException {
        return when (e) {
            is UnknownHostException -> {
                ApiException(-100, "网络异常")
            }
            is JSONException -> {
                ApiException(-100, "数据异常")
            }
            is SocketTimeoutException -> {
                ApiException(-100, "连接超时")
            }
            is ConnectException -> {
                ApiException(-100, "连接错误")
            }
            is ApiException -> {
                /*可以根据不同的服务器状态来判断，由于这里服务器没有定义错误码，
                所以如果状态/错误码 != 自定义的状态码，都认为是服务器出错
               when(e.status) {
                   is xxx -> {}
                   is xxx ->
               }*/
                ApiException(e.status, "服务器出错")
            }
            /**
             * 如果协程还在运行，个别机型退出当前界面时，viewModel会通过抛出CancellationException，
             * 强行结束协程，与java中InterruptException类似，所以不必理会,只需将toast隐藏即可
             */
            is CancellationException -> {
                ApiException(-100, "")
            }
            else -> {
                ApiException(-100, "未知错误")
            }
        }
    }
}
