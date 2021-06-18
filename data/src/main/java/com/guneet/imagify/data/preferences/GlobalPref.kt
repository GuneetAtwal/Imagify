package com.guneet.imagify.data.preferences

import android.content.Context

class ImagePrefs(private val context: Context) {
    companion object {
        private const val PREF_NAME = "image_loader_prefs"
        private const val CACHED_IMAGE_KEY = "cached_image_key"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setImageName(name: String) {
        pref.edit().putString(CACHED_IMAGE_KEY, name).apply()
    }

    fun getImageName(): String? {
        return pref.getString(CACHED_IMAGE_KEY, null)
    }
}