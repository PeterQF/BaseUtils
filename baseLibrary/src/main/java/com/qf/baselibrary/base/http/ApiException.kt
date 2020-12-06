package com.qf.baselibrary.base.http

class ApiException(var status: Int? = null, var errorMessage: String? = null) : Throwable()