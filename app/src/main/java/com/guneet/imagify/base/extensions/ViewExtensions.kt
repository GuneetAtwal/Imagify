package com.guneet.imagify.base.extensions

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.guneet.imagify.R
import kotlin.concurrent.thread

fun ImageView.loadImage(resource: ByteArray, @DrawableRes placeHolderResId: Int = R.color.black, cacheImage: Boolean = false) {
    setImageResource(placeHolderResId)
    thread {
        try {
            val bitmap = BitmapFactory.decodeByteArray(resource, 0, resource.size)
            setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

fun View.setVisibility(isVisible: Boolean): Unit =
    if (isVisible) visibility = View.VISIBLE else visibility = View.GONE
