package com.qf.mymodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qf.baseutils.BaseUtils
import com.qf.baseutils.log.LogUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseUtils.instance.init(this)
        LogUtils.info("main activity init")
    }
}