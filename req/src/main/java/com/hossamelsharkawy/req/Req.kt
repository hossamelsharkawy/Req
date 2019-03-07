package com.hossamelsharkawy.req


import kotlinx.coroutines.Deferred


/**
 * Created by Hossam Elsharkawy
on 6/14/2018.
 */

/**************************************/
fun <T> Deferred<T>.req(listener: RequestListener<T>?) =
    launchOnUI(parent = Server.ApiJob) { doReq(this@req, listener) }

suspend fun <T> doReq(d: Deferred<T>, lis: RequestListener<T>?) {
    try {
        lis?.onResponse(d.await())
    } catch (e: Exception) {
        onErrorMsg(lis , getErrorMsg(e))
    }
}

fun <T> onErrorMsg(l: RequestListener<T>?, errorMsg: String) {
    l?.onHttpError(errorMsg)
}

/*************************************/
fun <T> Deferred<T>.req(
    onResponse: ((T) -> Unit)
    , onError: ((String) -> Unit)? = null
    , onEmpty: ((String) -> Unit)? = null
) = launchOnUI(parent = Server.ApiJob) {
    doReq(this@req, onResponse, onError, onEmpty)
}

fun <T> Deferred<T>.req(onRes: ((T) -> Unit)?) {
    launchOnUI(parent = Server.ApiJob) {
        doReq(this@req, onRes)
    }
}

suspend fun <T> doReq(
    d: Deferred<T>
    , onRes: ((T) -> Unit)?
    , onError: ((String) -> Unit)? = null
    , onEmpty: ((String) -> Unit)? = null
) {
    try {
        // response
        with(d.await()) {
            when (this) {
                is ResData<*> -> onResData(this, onRes, onEmpty, onError)
                else -> {
                    onRes?.invoke(this)
                }
            }
        }
    } catch (e: Exception) {
        onError?.invoke(getErrorMsg(e))
    }
}


fun <T> onResData(
    response: ResData<*>
    , onRes: ((T) -> Unit)?
    , onError: ((String) -> Unit)? = null
    , onEmpty: ((String) -> Unit)? = null
) {
    if (response.data is ArrayList<*>) {
        if (!response.data.isNullOrEmpty()) {
            onRes?.invoke(response as T)
        } else {
            if (onEmpty != null) onEmpty.invoke("No Data") else onError?.invoke("No Data")
        }
    } else if (response.data == null) {
        if (onEmpty != null) onEmpty.invoke("No Data") else onError?.invoke("No Data")
    } else {
        onRes?.invoke(response as T)
    }
}

/*
{"api_status":0,"api_http":422,"api_message":{"title":"error","message":"The Field Phone  must started with 0"}}*/
private fun getErrorMsg(t: retrofit2.HttpException) = if (!CheckNetwork.isConnected()) {
    Server.noInternetConnectionMsg
} else {
    when (t.code()) {
        404 -> t.message()
        422 -> getApiMessage(t)
        504 -> Server.serverErrorMsg
        401 -> Server.invalid_credentialsMsg
        400 -> Server.invalid_credentialsMsg
        else -> Server.serverErrorMsg
    }
}
private fun getErrorMsg(e: Exception) : String {
  return  when (e) {
        is retrofit2.HttpException -> getErrorMsg(e)
        is java.net.SocketTimeoutException -> Server.socketTimeoutMsg
        is javax.net.ssl.SSLHandshakeException -> Server.connectionErrorMsg
        is com.google.gson.JsonSyntaxException -> "jsonError" + "\n${e.message}"
        is java.net.UnknownHostException -> e.message ?: "Unknown Host!"
        is java.net.ProtocolException -> Server.protocolExceptionMsg
        is javax.net.ssl.SSLException -> Server.connectionAbortMsg
        is java.net.SocketException -> Server.connectionAbortMsg
        is java.util.concurrent.CancellationException ->  Server.cancellationExceptionMsg

        else -> throw Exception(e)
        //lis?.onHttpError(e.localizedMessage)
    }
}
fun getApiMessage(e: retrofit2.HttpException): String {

    val errorResponse = e.response().errorBody()?.string()

    if (errorResponse != null) {
        val json = com.google.gson.JsonPrimitive(errorResponse).asJsonObject

        val msg = json.get("api_message")?.asJsonObject?.get("message")?.asString

        if (msg != null) return msg
    }

    if (e.message != null) return e.message!!

    return "Api Error"

}
