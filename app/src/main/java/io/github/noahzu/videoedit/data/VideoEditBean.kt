package com.haoqi.teacher.videoedit.data

import android.content.Context
import android.net.Uri
import io.github.noahzu.videoedit.ext.toFile
import io.github.noahzu.videoedit.utils.VideoSourceBean
import io.github.noahzu.videoedit.utils.VideoUtil

class VideoEditBean(var videoBean: VideoSourceBean, var haveMarker: Boolean, var isCurrent: Boolean) {

    companion object {
        fun theVideoEditBean(videoBean: VideoSourceBean): VideoEditBean {
            return VideoEditBean(videoBean, false, false)
        }
    }

    fun copyByVideoSource(videoBean: VideoSourceBean): VideoEditBean {
        return VideoEditBean(videoBean, haveMarker, false)
    }

    fun ratio() = videoBean.width.toFloat() / videoBean.height.toFloat()


    fun refreshCover(context: Context) {
        videoBean.coverImage = VideoUtil.getVideoCover(context,Uri.fromFile(videoBean.path.toFile()))
    }
}