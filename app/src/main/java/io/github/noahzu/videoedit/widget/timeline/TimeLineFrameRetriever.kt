package com.haoqi.teacher.videoedit.widget.timeline

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.collection.LruCache
import io.github.noahzu.videoedit.VideoEditApplication
import io.github.noahzu.videoedit.ext.toFile

class TimeLineFrameRetriever(videoPath: String) {
    private val bitmapCache = LruCache<Long, Bitmap>(15)
    private val mediaMetadataRetriever = MediaMetadataRetriever()

    init {
        mediaMetadataRetriever.setDataSource(VideoEditApplication.getAppContext(), Uri.fromFile(videoPath.toFile()))

    }
}