package com.guneet.imagify.base.extensions

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
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

fun View.showNoInternetView(
    onRetry: () -> Unit = {},
    title: String? = null,
    description: String? = null
) {
    setVisibility(true)
    findViewById<AppCompatButton>(R.id.btn_retry)?.let { button ->
        button.setOnClickListener {
            setVisibility(false)
            onRetry()
        }
    }

    findViewById<TextView>(R.id.tv_offline)
        ?.text = title ?: context?.getString(R.string.offline)

    findViewById<TextView>(R.id.tv_no_internet_desc)?.let {
        setVisibility(true)
        it.text = description ?: context?.getString(R.string.no_internet_connection_desc)
    }
}

