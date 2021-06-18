package com.guneet.imagify.base.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView

class ImageLoader(context: Context) {
    private val fileCache by lazy { FileCache(context) }

    fun loadLastCachedImage(imageView: ImageView) {
        val mainHandler = Handler(Looper.getMainLooper())

        try {
            fileCache.getLastCachedBitmap()?.let {
                mainHandler.post {
                    imageView.setImageBitmap(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadImage(
        imageView: ImageView,
        resource: ByteArray
    ) {
        val mainHandler = Handler(Looper.getMainLooper())

        try {
            val bitmap = BitmapFactory.decodeByteArray(resource, 0, resource.size)
            mainHandler.post {
                imageView.setImageBitmap(bitmap)
            }
            fileCache.addBitmapToCache(bitmap.hashCode().toString(), bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}