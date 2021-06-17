package com.guneet.imagify.data.extensions

import com.guneet.imagify.data.entities.base.ResourceState
import com.guneet.imagify.data.enums.AppErrorCodes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

suspend fun <F, T> Call<F>.mapToEntity(mapper: (F) -> T): ResourceState<T> {
    return try {
        val response = this.toDeferredAsync().await()
        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                ResourceState.Failure(java.lang.Exception("Something went wrong"))
            } else {
                ResourceState.Success(mapper(body), response.code())
            }
        } else {
            ResourceState.Failure(java.lang.Exception("Something went wrong"))
        }
    } catch (exception: Exception) {
        when (exception) {
            is UnknownHostException,
            is SocketException,
            is SocketTimeoutException,
            is TimeoutException,
            is ConnectException -> {
                ResourceState.Failure<T>(
                    exception = exception,
                    statusCode = AppErrorCodes.NO_INTERNET_CONNECTION.statusCode
                )
            }
            else -> {
                ResourceState.Failure(
                    exception = exception,
                    statusCode = AppErrorCodes.FAILURE.statusCode)
            }
        }
    }
}

fun <T> Call<T>.toDeferredAsync(): Deferred<Response<T>> {
    val deferred = CompletableDeferred<Response<T>>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            cancel()
        }
    }
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (deferred.isActive) {
                deferred.complete(response)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (deferred.isActive) {
                deferred.completeExceptionally(t)
            }
        }
    })

    return deferred
}
