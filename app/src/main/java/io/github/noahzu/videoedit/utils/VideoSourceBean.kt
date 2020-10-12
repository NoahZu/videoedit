package io.github.noahzu.videoedit.utils

/**
 * 支持假裁
 */
class VideoSourceBean(
    val path: String,
    val width: Int,
    val height: Int,
    private val duration: Long,
    val rotation: Int,
    var coverImage: String,
    var startTime: Long,
    var endTime: Long,
    var extra: Any? = null
) {

    fun createByTime(newStartTime: Long, newEndTime: Long): VideoSourceBean {
        return VideoSourceBean(path, width, height, duration, rotation, coverImage, newStartTime, newEndTime,extra)
    }

    fun getVideoDuration() = endTime - startTime

    fun getVideoRawDuration() = duration

    fun changeRange(startTime: Long,endTime: Long) {
        this.startTime = startTime
        this.endTime = endTime
    }
}