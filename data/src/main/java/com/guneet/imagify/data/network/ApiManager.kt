package com.guneet.imagify.data.network

import com.guneet.imagify.data.api.ImageApi
import retrofit2.Retrofit

class ApiManager constructor(private val retrofit: Retrofit) {
    val iamgeApi by lazy { retrofit.updateBaseUrl("https://picsum.photos").createApi<ImageApi>() }
}

inline fun <reified T> Retrofit.createApi(): T = this.create(T::class.java)
fun Retrofit.updateBaseUrl(baseUrl: String): Retrofit = this.newBuilder().baseUrl(baseUrl).build()
