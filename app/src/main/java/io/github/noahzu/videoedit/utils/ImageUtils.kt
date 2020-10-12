package io.github.noahzu.videoedit.utils

import android.graphics.Bitmap

/**
 * @author xab
 * @create 2019-09-25 15:51
 * @desc
 */
object ImageUtils {

    fun scale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int,
        recycle: Boolean = false
    ): Bitmap {
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }


}