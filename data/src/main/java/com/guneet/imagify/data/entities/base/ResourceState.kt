package com.guneet.imagify.data.entities.base

sealed class ResourceState<T> {
    data class Success<T>(val body: T, val code: Int = 200) : ResourceState<T>()

    data class Failure<T>(
            val exception: Throwable,
            val statusCode: String = "",
            val body: T? = null
    ) : ResourceState<T>()
}