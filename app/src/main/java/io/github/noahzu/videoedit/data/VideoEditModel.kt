package com.haoqi.teacher.videoedit.data

import android.net.Uri
import io.github.noahzu.videoedit.VideoEditApplication
import io.github.noahzu.videoedit.utils.VideoSourceBean
import io.github.noahzu.videoedit.utils.VideoUtil
import java.io.File
import java.util.*

/**
 * 记录所有视频编辑数据
 */
class VideoEditModel() {
    lateinit var videoInfos: ArrayList<VideoEditBean>
    var outputVideoWidth = 0
    var outputVideoHeight = 0
    var outputVideoFile: File? = null
    var outputVideoCover: File? = null
    var imageMarker: File? = null

    var currentIndex = -1

    fun loadData(paths: List<String>) {
        if (paths.isEmpty()) {
            return
        }
        videoInfos = ArrayList()
        paths.forEach {
            VideoUtil.getVideoSourceBean(VideoEditApplication.getAppContext(), Uri.fromFile(File(it)))?.let {
                videoInfos.add(VideoEditBean.theVideoEditBean(it))
            }
        }
        if (videoInfos.isNotEmpty()) {
            outputVideoWidth = videoInfos.first().videoBean.width
            outputVideoHeight = videoInfos.first().videoBean.height
        }
    }


    fun currentVideoEditBean(): VideoEditBean {
        return videoInfos[currentIndex]
    }

    fun currentVideoSourceBean(): VideoSourceBean {
        return currentVideoEditBean().videoBean
    }

    fun count(): Int {
        return videoInfos.size
    }

    fun updateCurrentIndex(index: Int) {
        if (currentIndex >= 0) {
            currentVideoEditBean().isCurrent = false
        }
        currentIndex = index
        currentVideoEditBean().isCurrent = true
    }

    fun outRatio(): Float {
        return outputVideoWidth.toFloat() / outputVideoHeight
    }

    /**
     * 当前位置添加视频
     */
    fun addVideoInCurrentIndex(insertVideo: File) {
        VideoUtil.getVideoSourceBean(VideoEditApplication.getAppContext(), Uri.fromFile(insertVideo))?.let {
            currentVideoEditBean().run {
                isCurrent = false
                videoBean.extra = 0
            }
            videoInfos.add(currentIndex, VideoEditBean.theVideoEditBean(it))
            currentVideoEditBean().run {
                isCurrent = true
                videoBean.extra = 1
            }
        }
    }

    /**
     * 尾部添加视频
     */
    fun appendVideo(insertVideo: File) {
        VideoUtil.getVideoSourceBean(VideoEditApplication.getAppContext(), Uri.fromFile(insertVideo))?.let {
            currentVideoEditBean().run {
                isCurrent = false
                videoBean.extra = 0
            }
            videoInfos.add(VideoEditBean.theVideoEditBean(it))
            currentIndex = videoInfos.lastIndex
            currentVideoEditBean().run {
                isCurrent = true
                videoBean.extra = 1
            }
        }
    }

    fun addVideoSourceInCurrentIndex(videoEditBean: VideoEditBean) {
        videoInfos.add(currentIndex, videoEditBean)
    }

    /**
     * 更新当前选中视频
     */
    fun updateCurrentVideo(newVideoSource: VideoSourceBean) {
        currentVideoEditBean().videoBean = newVideoSource
    }

    /**
     * 删除当前选中视频
     */
    fun deleteCurrentVideo() {
        if (count() < 2) {
            return
        }
        videoInfos.removeAt(currentIndex)
        if (currentIndex > 0) {
            currentIndex -= 1
        }
    }

    /**
     * 更新数据是否有水印
     */
    fun updateVideoMarker(useMarker: Boolean, applyAll: Boolean) {
        if (applyAll) {
            videoInfos.forEach {
                it.haveMarker = useMarker
            }
        } else {
            currentVideoEditBean().haveMarker = useMarker
        }

    }

    /**
     * 做了数据增删改以后，校准currentIndex
     */
    fun correctCurrentIndex() {
        videoInfos.forEachIndexed { index, video ->
            if (video.isCurrent) {
                currentIndex = index
                return@forEachIndexed
            }
        }
    }
}