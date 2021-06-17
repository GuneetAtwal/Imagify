package com.guneet.imagify.data.repositories

import com.guneet.imagify.data.entities.base.ResourceState
import com.guneet.imagify.data.extensions.mapToEntity
import com.guneet.imagify.data.network.ApiManager

interface ImageRepository {
    suspend fun getImage() : ResourceState<ByteArray>
}

class ImageRepositoryImpl(private val apiManager: ApiManager): ImageRepository {
    override suspend fun getImage(): ResourceState<ByteArray> {
        return apiManager.iamgeApi.getImage().mapToEntity {
            it.bytes()
        }
    }
}