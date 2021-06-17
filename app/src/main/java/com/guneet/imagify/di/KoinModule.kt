package com.guneet.imagify.di

import com.guneet.imagify.data.network.ApiManager
import com.guneet.imagify.data.repositories.ImageRepository
import com.guneet.imagify.data.repositories.ImageRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.dsl.binds
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val repoModule = module {
    single { provideOkHttpClient() }
    single { provideMoshi() }
    single { provideRetrofitClient(get(), get()) }
    single { ApiManager(get()) }

    single { ImageRepositoryImpl(get()) } binds arrayOf(ImageRepository::class)
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()
}

fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

fun provideRetrofitClient(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://baseUrl")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()