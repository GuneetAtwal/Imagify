package com.guneet.imagify.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.guneet.imagify.base.extensions.observe
import com.guneet.imagify.base.extensions.setVisibility
import com.guneet.imagify.base.extensions.showNoInternetView
import com.guneet.imagify.base.utils.InternetCheckUtil
import com.guneet.imagify.data.entities.base.ResourceState
import com.guneet.imagify.data.enums.AppErrorCodes
import com.guneet.imagify.data.repositories.ImageRepository
import com.guneet.imagify.databinding.ActivityHomeBinding
import com.guneet.imagify.base.utils.ImageLoader
import com.guneet.imagify.home.viewmodel.HomeViewModel
import com.guneet.imagify.home.viewmodel.HomeViewModelFactory
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private val imageRepository: ImageRepository by inject()

    private val internetCheckUtil by lazy { InternetCheckUtil() }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageLoader = ImageLoader(this)

        val factory = HomeViewModelFactory(imageRepository = imageRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        checkNetworkState()
        observeData()
        setClickListeners()
    }

    private fun checkNetworkState() {
        viewModel.isNetworkAvailable = internetCheckUtil.isConnectedToInternet(this)
        if (viewModel.isNetworkAvailable.not()) {
            showNoInternetView(::checkNetworkState)
        } else {
            binding.btnChange.isEnabled = true
        }
    }

    private fun fetchImage() {
        showLoading(true)
        viewModel.getImage()
    }

    private fun observeData() {
        observe(viewModel.imageLiveData) {
            showLoading(false)
            when (it) {
                is ResourceState.Success -> {
                    imageLoader.loadImage(binding.ivPlaceholder, it.body)
                }

                is ResourceState.Failure -> {
                    if (it.statusCode == AppErrorCodes.NO_INTERNET_CONNECTION.statusCode) {
                        showNoInternetView(::fetchImage)
                    }
                    it.exception.printStackTrace()
                }
            }
        }
        imageLoader.loadLastCachedImage(binding.ivPlaceholder)
    }

    private fun setClickListeners() {
        binding.btnChange.setOnClickListener {
            fetchImage()
        }
    }

    private fun showNoInternetView(onRetry: () -> Unit) {
        binding.includeNoInternetLayout.root.showNoInternetView(onRetry)
        binding.btnChange.isEnabled = false
    }

    private fun showLoading(show: Boolean) {
        binding.progressBarMain.setVisibility(show)
        binding.btnChange.isEnabled = show.not()
    }
}