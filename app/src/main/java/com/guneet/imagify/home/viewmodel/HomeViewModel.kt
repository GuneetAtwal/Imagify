package com.guneet.imagify.home.viewmodel

import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guneet.imagify.data.entities.base.ResourceState
import com.guneet.imagify.data.repositories.ImageRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    private val _imageLiveData = MutableLiveData<ResourceState<ByteArray>>()
    val imageLiveData = _imageLiveData as LiveData<ResourceState<ByteArray>>

    var isNetworkAvailable = false

    var memoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun getImage() {
        viewModelScope.launch {
            _imageLiveData.postValue(imageRepository.getImage())
        }
    }

    private fun doCaching() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8

        val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }
}