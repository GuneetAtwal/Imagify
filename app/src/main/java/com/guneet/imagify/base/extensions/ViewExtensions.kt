package com.guneet.imagify.base.extensions

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.guneet.imagify.R

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
