package com.guneet.imagify.base.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.guneet.imagify.data.preferences.ImagePrefs
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class FileCache(private val context: Context) {

    private val prefs by lazy { ImagePrefs(context) }
    private val cacheDir: File by lazy { getDiskCacheDir(context, DISK_CACHE_SUBDIR) }
    private var diskLruCache: DiskLruCache? = null
    private val diskCacheLock = ReentrantLock()
    private val diskCacheLockCondition: Condition = diskCacheLock.newCondition()
    private var diskCacheStarting = true

    init {
        thread {
            diskCacheLock.withLock {
                diskLruCache = DiskLruCache.open(cacheDir,1, 1, DISK_CACHE_SIZE)
                diskCacheStarting = false
                diskCacheLockCondition.signalAll()
            }
        }.run()
    }

    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        prefs.setImageName(key)

        synchronized(diskCacheLock) {
            diskLruCache?.apply {
                if (this[key] == null) {
                    put(key, bitmap)
                }
            }
        }
    }

    fun getLastCachedBitmap() = prefs.getImageName()?.let {
        getBitmapFromDiskCache(it)
    }

    private fun getBitmapFromDiskCache(key: String): Bitmap? =
        diskCacheLock.withLock {
            while (diskCacheStarting) {
                try {
                    diskCacheLockCondition.await()
                } catch (e: InterruptedException) {
                }

            }
            return get(key)
        }


    private fun put(key: String, bitmap: Bitmap) {
        try {
            val editor = diskLruCache?.edit(key) ?: return
            val out = BufferedOutputStream(editor.newOutputStream(0))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            editor.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun get(key: String): Bitmap? {
        return try {
            val snapshot = diskLruCache?.get(key)

            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val out = BufferedInputStream(snapshot?.getInputStream(0))

            BitmapFactory.decodeStream(out, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !Environment.isExternalStorageRemovable()
            ) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }

        return File(cachePath + File.separator + uniqueName)
    }

    companion object {
        private const val DISK_CACHE_SIZE = 1024 * 1024 * 10L // 10MB
        private const val DISK_CACHE_SUBDIR = "thumbnails"
    }
}