package com.hossamelsharkawy.req


/**
 * Created by hossam on 28/02/17.
 */

interface RequestListener<T> {

    fun onResponse(data: T)

    fun onHttpError(msg: String)

    fun onNetError(msg: String)


}

