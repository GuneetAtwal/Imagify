package com.guneet.imagify.home.viewmodel

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

    fun getImage() {
        viewModelScope.launch {
            _imageLiveData.postValue(imageRepository.getImage())
        }
    }
}