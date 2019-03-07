package com.hossamelsharkawy.req

import kotlinx.coroutines.*

/**
 * Created by Hossam Elsharkawy
0201099197556
on 9/25/2018.  time :04:04
 */

fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job = launchOnUI { asyncAwait { block.invoke(this) } }

suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T = async(block).await()

suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> =
    GlobalScope.async(Dispatchers.Main) { block() }


fun launchOnUITryCatch(
    tryBlock: suspend CoroutineScope.() -> Unit,
    catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
    handleCancellationExceptionManually: Boolean
) {
    launchOnUI { tryCatch(tryBlock, catchBlock, handleCancellationExceptionManually) }
}

fun launchOnUI(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.Main) { block() }


fun <T> async(io: T, ui: (T) -> Unit) = GlobalScope.launch(Dispatchers.IO) {
    launchOnUI {
        ui.invoke(io)
    }
}

fun <T> load(io: T, result: Result<T>) = GlobalScope.launch(Dispatchers.IO) {
    launchOnUI {
        if (io != null) {
            result.onRes?.invoke(io)
        } else {
            result.onError?.invoke(null)
        }
    }
}


fun launchOnUI(parent: Job, block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Main + parent) { block() }


fun launch(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch { block() }


suspend fun CoroutineScope.tryCatch(
    tryBlock: suspend CoroutineScope.() -> Unit,
    catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
    handleCancellationExceptionManually: Boolean = false
) {
    try {
        tryBlock()
    } catch (e: Throwable) {
        if (e !is CancellationException || handleCancellationExceptionManually) {
            catchBlock(e)
        } else {
            throw e
        }
    }
}