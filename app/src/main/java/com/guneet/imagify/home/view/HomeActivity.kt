package com.guneet.imagify.home.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.guneet.imagify.base.extensions.loadImage
import com.guneet.imagify.base.extensions.observe
import com.guneet.imagify.base.extensions.setVisibility
import com.guneet.imagify.data.entities.base.ResourceState
import com.guneet.imagify.data.repositories.ImageRepository
import com.guneet.imagify.databinding.ActivityHomeBinding
import com.guneet.imagify.home.viewmodel.HomeViewModel
import com.guneet.imagify.home.viewmodel.HomeViewModelFactory
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private val imageRepository: ImageRepository by inject()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = HomeViewModelFactory(imageRepository = imageRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        observeData()
        setClickListeners()
    }

    private fun observeData() {
        observe(viewModel.imageLiveData) {
            showLoading(false)
            when (it) {
                is ResourceState.Success -> {
                    binding.ivPlaceholder.loadImage(it.body)
                }

                is ResourceState.Failure -> {
                    it.exception.printStackTrace()
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.btnChange.setOnClickListener {
            showLoading(true)
            viewModel.getImage()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBarMain.setVisibility(show)
        binding.btnChange.isEnabled = show.not()
    }
}