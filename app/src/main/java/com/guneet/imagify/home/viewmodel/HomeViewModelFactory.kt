package com.guneet.imagify.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.guneet.imagify.data.repositories.ImageRepository

class HomeViewModelFactory(
    private val imageRepository: ImageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (HomeViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass.getConstructor(ImageRepository::class.java)
                .newInstance(imageRepository)
        } else {
            throw IllegalStateException("ViewModel must extend HomeViewModel")
        }
    }
}