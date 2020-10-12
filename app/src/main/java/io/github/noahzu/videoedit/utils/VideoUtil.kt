package io.github.noahzu.videoedit.utils

import Jni.TrackUtils
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import java.io.File

object VideoUtil {

    private val TAG = VideoUtil::class.java.simpleName

    /**
     * 涉及获取Bitmap，Bitmap的修改，文件存储，最好放在非UI线程
     */
    fun getVideoSourceBean(context: Context?, videoPath: Uri?): VideoSourceBean? {
        if (videoPath == null || context == null) {
            return null
        }

        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath.toString(), HashMap<String, String>())

            val path = videoPath.path
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0
            val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()?:0
            val height =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()?:0
            val rotation =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt()?:0
            val coverPath = scaleSaveCover(mediaMetadataRetriever.getFrameAtTime(500 * 1000)!!)

            val videoSourceBean = if (needRotation(rotation)) {
                VideoSourceBean(path!!, height, width, duration, rotation, coverPath, 0, duration)
            } else {
                VideoSourceBean(path!!, width, height, duration, rotation, coverPath, 0, duration)
            }

            mediaMetadataRetriever.release()
            return videoSourceBean
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, e.toString())
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return null
    }

    private fun scaleSaveCover(bitmap: Bitmap): String {
        val width = SizeUtils.dp2px(50f)
        return ImageUtils.scale(bitmap, width, width, true)?.let {
            (FileUtils.saveBitmapToLocal(it)?.path ?: "")
        } ?: kotlin.run { "" }
    }

    fun getSimpleMetadata(context: Context?, videoPath: Uri?): SimpleMetadata? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(context, videoPath)
            val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()?:0
            val height =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()?:0
            val rotation =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt()?:0
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0
            val bitRate =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLong()?:0 / (8 * 1024)

            mediaMetadataRetriever.release()
            if (needRotation(rotation)) {
                return SimpleMetadata(height, width, duration, bitRate)
            }
            return SimpleMetadata(width, height, duration, bitRate)
        } catch (e: java.lang.Exception) {
            return null
        }
    }

    fun getVideoCover(context: Context?, videoPath: Uri?): String {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, videoPath)
        val file = FileUtils.saveBitmapToLocal(mediaMetadataRetriever.frameAtTime!!)
        mediaMetadataRetriever.release()
        return file?.path ?: ""
    }

    /**
     * 获取视频信息
     *
     * @param url
     * @return    视频时长（单位微秒）
     */
    fun getDuration(url: String?): Long {
        return try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(url?:"")
            var videoExt = TrackUtils.selectVideoTrack(mediaExtractor)
            if (videoExt == -1) {
                videoExt = TrackUtils.selectAudioTrack(mediaExtractor)
                if (videoExt == -1) {
                    return 0
                }
            }
            val mediaFormat = mediaExtractor.getTrackFormat(videoExt)
            val res =
                if (mediaFormat.containsKey(MediaFormat.KEY_DURATION)) mediaFormat.getLong(
                    MediaFormat.KEY_DURATION
                ) else 0 //时长
            mediaExtractor.release()
            res
        } catch (e: java.lang.Exception) {
            0
        }
    }

    /**
     *
     * @param rotation
     * @return true 90的奇数倍, false 90的偶数倍
     */
    fun needRotation(rotation: Int): Boolean {
        val num = Math.abs(rotation) / 90
        return num % 2 != 0
    }

    class SimpleMetadata(val width: Int, val height: Int, val duration: Long, val bitRate: Long)


    interface EditCallback {
        fun onProgress(progress: Float)
        fun onFail(error: String)
        fun onSuccess(file: File?)
    }
}
