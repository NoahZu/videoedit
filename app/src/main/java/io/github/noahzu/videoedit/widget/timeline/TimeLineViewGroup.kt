package com.haoqi.teacher.videoedit.widget.timeline

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import io.github.noahzu.videoedit.utils.SizeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 固定长度的TimeLineView
 */
class TimeLineViewGroup : LinearLayout {

    private val perImageViewWidth = SizeUtils.dp2px(50f)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        orientation = HORIZONTAL
    }

    fun loadVideoTimeLine(videoFile: File) {
        removeAllViews()
        val count = Math.ceil(width.toDouble() / perImageViewWidth).toInt()
        val commonLayoutParams = LayoutParams(perImageViewWidth, height)

        GlobalScope.launch(Dispatchers.IO) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, Uri.fromFile(videoFile))
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
            val distance = duration / count
            for (i in 0 until count) {
                val time = Math.min(i * distance, duration)
                val bitmap = mediaMetadataRetriever.getFrameAtTime(time)

                withContext(Dispatchers.Main) {
                    val imageView = ImageView(context)
                    imageView.setImageBitmap(bitmap)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    addView(imageView, commonLayoutParams)
                }
            }
            mediaMetadataRetriever.release()
        }
    }

}