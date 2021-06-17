package com.guneet.imagify.base.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class InternetCheckUtil {
    fun isConnectedToInternet(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        return true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        return true
                    }
                }
            }
        }
        return false
    }
}