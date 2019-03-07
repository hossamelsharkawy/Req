package com.hossamelsharkawy.req

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.EOFException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.SocketTimeoutException

/**
 * Created by Hossam Elsharkawy
on 6/24/2018.  time :11:49
 */

class TryCallAdapter private constructor() : CallAdapter.Factory() {

    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = TryCallAdapter()
    }

    override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Deferred::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                    "Deferred return type must be parameterized as Deferred<Foo> or Deferred<out Foo>")
        }
        val responseType = getParameterUpperBound(0, returnType)

        val rawDeferredType = getRawType(responseType)

        return if (rawDeferredType == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                        "Response must be parameterized as Response<Foo> or Response<out Foo>")
            }

            ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {

            BodyCallAdapter<Any>(responseType)

        }
    }

    private class BodyCallAdapter<T>(
            private val responseType: Type


    ) : CallAdapter<T, Deferred<T>> {
        private var tryCount: Int = 0

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<T> {
            val deferred = CompletableDeferred<T>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            req(deferred, call)

            return deferred
        }

        private fun req(deferred: CompletableDeferred<T>, call: Call<T>) {

            //Log.d("BodyCallAdapter", hashCode().toString() +" tryCount : $tryCount")
            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (t is EOFException || t is SocketTimeoutException) {
                        retry(deferred, call.clone(), Exception("SocketTimeoutException"))
                    } else {
                        deferred.completeExceptionally(t)
                    }
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        deferred.complete(body)
                    } else {
                        deferred.completeExceptionally(HttpException(response))
                    }
                }
            })
        }

        private fun retry(deferred: CompletableDeferred<T>, call: Call<T>, t: Exception) {
            if (tryCount < 2) {
                tryCount++
                req(deferred, call.clone())
            } else {
                tryCount = 0
                deferred.completeExceptionally(t)
            }
        }
    }

    private class ResponseCallAdapter<T>(
            private val responseType: Type
    ) : CallAdapter<T, Deferred<Response<T>>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<Response<T>> {
            val deferred = CompletableDeferred<Response<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    deferred.complete(response)
                }
            })

            return deferred
        }
    }
}

