package com.haoqi.teacher.videoedit.manager

import VideoHandle.EpDraw
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import com.haoqi.teacher.videoedit.data.VideoEditBean
import com.haoqi.teacher.videoedit.data.VideoEditModel
import com.haoqi.teacher.videoedit.videoprocess.EpEditorExt
import io.github.noahzu.videoedit.ext.toFile
import io.github.noahzu.videoedit.utils.FileUtils
import io.github.noahzu.videoedit.utils.VideoUtil
import java.io.File

class VideoEditControlManager(val videoModel: VideoEditModel) {
    companion object {
        const val MAX_VIDEO_SIZE = 1080
    }

    private val workTempDir = FileUtils.getVideoEditTempDir()
    private val workThread = HandlerThread("VideoEditControlManager")
    private var workHandler: Handler
    private val mainHandler = Handler()
    private val filterFormat by lazy { "pad=%d:%d:%d:%d:black" }

    init {
        workThread.start()
        workHandler = Handler(workThread.looper)
    }

    /**
     * @param startTime 剪切开始时间 单位毫秒
     * @param endTime 剪切结束时间 单位毫秒
     */
    fun startCutCurrentVideo(startTime: Int, endTime: Int, callback: VideoUtil.EditCallback) {
        val videoSourceBean = videoModel.currentVideoSourceBean()
        val videoFile = File(videoSourceBean.path)
        if (startTime >= endTime || startTime < 0 || endTime > videoSourceBean.getVideoDuration()) {
            callback.onFail("剪切时间错误")
            return
        }

        if (startTime == 0 && endTime.toLong() == videoSourceBean.getVideoDuration()) {
            callback.onSuccess(videoFile)
            return
        }

        val outputFile = File(workTempDir, "${videoFile.name}_clip_${startTime}_${endTime}.mp4")
        val epVideo = EpVideo(videoSourceBean.path)
        epVideo.clip(startTime.toFloat() / 1000, (endTime - startTime).toFloat() / 1000)
        val outputOption = EpEditor.OutputOption(outputFile.path)
        doExecInner(epVideo, callback, outputFile, outputOption)
    }


    /**
     * 开始图片生成视频
     * @param picFile 图片地址
     * @param duration 时长
     * @param callback 回调
     */
    fun startImage2Video(picFile: File?, duration: Int, callback: VideoUtil.EditCallback) {
        if (picFile == null) {
            callback.onFail("file is empty")
            return
        }

        val videoOutPath = File(workTempDir, "${picFile.name}_generate.mp4")
        EpEditorExt.onePic2Video(
            picFile.path,
            videoOutPath.path,
            duration,
            videoModel.outputVideoWidth,
            videoModel.outputVideoHeight,
            object : OnEditorListener {
                override fun onSuccess() {
                    callback.onSuccess(videoOutPath)
                }

                override fun onFailure() {
                    callback.onFail("")
                }

                override fun onProgress(progress: Float) {
                    callback.onProgress(progress)
                }
            })
    }

    /**
     * 视频转码
     */
    fun transCodeVideo(videoFile: File, size: Size, callback: VideoUtil.EditCallback) {
        val outputFile = File(workTempDir, "${videoFile.nameWithoutExtension}_transcode.mp4")
        val epVideo = EpVideo(videoFile.path)
        val outputOption = EpEditor.OutputOption(outputFile.path)
        val newWidth: Int
        val newHeight: Int

        if (size.width > size.height) {
            newWidth = MAX_VIDEO_SIZE
            newHeight = newWidth * size.height / size.width
        } else {
            newHeight = MAX_VIDEO_SIZE
            newWidth = newHeight * size.width / size.height
        }
        outputOption.setWidth(newWidth)
        outputOption.setHeight(newHeight)
        outputOption.frameRate = 16
        outputOption.bitRate = 3
        workHandler.post {
            EpEditor.exec(epVideo, outputOption, object : OnEditorListener {
                override fun onSuccess() {
                    mainHandler.post { callback.onSuccess(outputFile) }
                }

                override fun onFailure() {
                    mainHandler.post {
                        callback.onFail("")
                    }
                }

                override fun onProgress(progress: Float) {
                    mainHandler.post {
                        callback.onProgress(progress)
                    }
                }
            })
        }
    }


    /**
     * 开始合成
     */
    fun startCompose(callback: VideoUtil.EditCallback) {
        val epVideoList = videoModel.videoInfos.map { toEpVideo(it) }
        val outputFile = File(workTempDir, "haoqi_${randomFileName()}.mp4").path
        val outputOption = EpEditor.OutputOption(outputFile)
        outputOption.setWidth(videoModel.outputVideoWidth)
        outputOption.setHeight(videoModel.outputVideoHeight)
        outputOption.frameRate = 16//输出视频帧率,默认30
        outputOption.bitRate = 3//输出视频码率,默认10
        if (epVideoList.size > 1) {
            workHandler.post {
                mergeVideoInner(epVideoList, outputOption, outputFile, callback)
            }
        } else if (epVideoList.size == 1) {
            doExecInner(epVideoList[0], callback, outputFile.toFile(), outputOption)
        }
    }

    private fun mergeVideoInner(
        epVideoList: List<EpVideo>,
        outputOption: EpEditor.OutputOption,
        outputFile: String,
        callback: VideoUtil.EditCallback
    ) {
        EpEditor.merge(epVideoList, outputOption, object : OnEditorListener {
            override fun onSuccess() {
                Log.d("Compose", "success")
                mainHandler.post {
                    callback.onSuccess(outputFile.toFile())
                }
            }

            override fun onFailure() {
                mainHandler.post {
                    callback.onFail("")
                }
                Log.d("Compose", "fail")
            }

            override fun onProgress(progress: Float) {
                mainHandler.post {
                    callback.onProgress(progress)
                }
                Log.d("Compose", "progress:$progress")
            }

        })
    }

    private fun randomFileName(): String {
        return System.currentTimeMillis().toString()
    }

    fun startSplitCurrentVideo(splitTime: Long) {
        val currentVideoEditBean = videoModel.currentVideoEditBean()
        val currentVideoSource = videoModel.currentVideoSourceBean()
        val centerTime = currentVideoSource.startTime + splitTime

        val insertVideoSource =
            currentVideoSource.createByTime(currentVideoSource.startTime, centerTime)
                .apply { extra = null }
        val insertVideoEditBean = currentVideoEditBean.copyByVideoSource(insertVideoSource)

        currentVideoSource.changeRange(centerTime, currentVideoSource.endTime)
        videoModel.addVideoSourceInCurrentIndex(insertVideoEditBean)
        videoModel.currentIndex += 1
    }

    fun startSplitCurrentVideo(leftTime: Long, rightTime: Long) {
        val currentVideoEditBean = videoModel.currentVideoEditBean()
        val currentVideoSource = videoModel.currentVideoSourceBean()

        val insertVideoSource =
            currentVideoSource.createByTime(currentVideoSource.startTime, leftTime).apply { extra = null }
        val insertVideoEditBean = currentVideoEditBean.copyByVideoSource(insertVideoSource)

        currentVideoSource.changeRange(rightTime, currentVideoSource.endTime)
        videoModel.addVideoSourceInCurrentIndex(insertVideoEditBean)
        videoModel.currentIndex += 1
    }

    private fun toEpVideo(it: VideoEditBean): EpVideo {
        val epVideo = EpVideo(it.videoBean.path)
        if (it.videoBean.startTime != 0L || it.videoBean.endTime != it.videoBean.getVideoRawDuration()) {
            epVideo.clip((it.videoBean.startTime / 1000).toFloat(), (it.videoBean.endTime / 1000).toFloat())
        }
        if (it.ratio() != videoModel.outRatio()) {
            processVideoSize(epVideo, it)
        }
        if (it.haveMarker) {
            val epDraw = EpDraw(
                videoModel.imageMarker?.path,
                0,
                0,
                videoModel.outputVideoWidth.toFloat(),
                videoModel.outputVideoHeight.toFloat(),
                false
            )
            epVideo.addDraw(epDraw)
        }
        return epVideo
    }

    /**
     * 处理视频大小，对于跟输出视频大小不一样的做添加黑边处理
     */
    private fun processVideoSize(epVideo: EpVideo, it: VideoEditBean) {
        val newWidth: Int
        val newHeight: Int
        val boardStartX: Int
        val boardStartY: Int
        val videoSource = it.videoBean

        /**
         * 宽度不变，高度增加，上下加黑边
         */
        if (it.ratio() > videoModel.outRatio()) {
            newWidth = videoSource.width
            newHeight = (videoSource.width / videoModel.outRatio()).toInt()
            boardStartX = 0
            boardStartY = (newHeight - videoSource.height) / 2
        } else { //高度不变，左右加黑边
            newHeight = videoSource.height
            newWidth = (videoSource.height * videoModel.outRatio()).toInt()
            boardStartY = 0
            boardStartX = (newWidth - videoSource.width) / 2
        }
        epVideo.addFilter(String.format(filterFormat, newWidth, newHeight, boardStartX, boardStartY))
    }

    /**
     * 执行编辑操作
     */
    private fun doExecInner(
        epVideo: EpVideo,
        callback: VideoUtil.EditCallback,
        outFile: File,
        outputOption: EpEditor.OutputOption
    ) {
        workHandler.post {
            EpEditor.exec(epVideo, outputOption, object : OnEditorListener {
                override fun onSuccess() {
                    mainHandler.post { callback.onSuccess(outFile) }
                }

                override fun onFailure() {
                    mainHandler.post { callback.onFail("") }
                }

                override fun onProgress(progress: Float) {
                    mainHandler.post { callback.onProgress(progress) }
                }

            })
        }
    }

    fun retrieveCover(context: Context) {
        videoModel.outputVideoCover =
            VideoUtil.getVideoCover(context, Uri.fromFile(videoModel.outputVideoFile)).toFile()
    }


}