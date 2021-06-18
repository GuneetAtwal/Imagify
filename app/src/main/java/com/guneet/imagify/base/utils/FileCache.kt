package com.guneet.imagify.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.guneet.imagify.data.preferences.ImagePrefs
import com.jakewharton.disklrucache.DiskLruCache
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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
        }.start()
    }

    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        prefs.setImageName(key)
        Log.d("666", "saving bitmap ${bitmap.hashCode()}")

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
            val out = ObjectOutputStream(editor.newOutputStream(0))
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

            val out = ObjectInputStream(snapshot?.getInputStream(0))

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

    fun clear() {
        diskLruCache?.delete()
    }

    companion object {
        private const val DISK_CACHE_SIZE = 1024 * 1024 * 10L // 10MB
        private const val DISK_CACHE_SUBDIR = "thumbnails"
    }
}