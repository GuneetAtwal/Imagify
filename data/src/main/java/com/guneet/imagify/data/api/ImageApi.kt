package com.guneet.imagify.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageApi {
    @GET("200/300")
    fun getImage(): Call<ResponseBody>
}