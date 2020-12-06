package com.qf.baselibrary.ext

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.qf.baselibrary.utils.Utils

fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(content))return
    Toast.makeText(this, content, duration).apply { show() }
}

fun toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(content))return
    Utils.getApp().toast(content, duration)
}