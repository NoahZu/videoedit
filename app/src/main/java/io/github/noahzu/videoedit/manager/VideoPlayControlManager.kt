package com.haoqi.teacher.videoedit.manager

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.haoqi.teacher.videoedit.adapter.VideoListAdapter.Companion.OPTION_INSERT_LEFT
import com.haoqi.teacher.videoedit.adapter.VideoListAdapter.Companion.OPTION_INSERT_RIGHT
import com.haoqi.teacher.videoedit.data.VideoEditModel
import io.github.noahzu.videoedit.VideoEditApplication
import io.github.noahzu.videoedit.ext.*
import io.github.noahzu.videoedit.utils.FileUtils
import io.github.noahzu.videoedit.utils.VideoSourceBean
import kotlinx.android.synthetic.main.activity_video_edit.view.*
import kotlinx.android.synthetic.main.view_edit_video_main_menu.view.*

/**
 * 控制视频播放的逻辑
 */
class VideoPlayControlManager(uiContentView: View, val videoModel: VideoEditModel) {
    private val playerView: PlayerView = uiContentView.videoPlayerView
    private val markerText = uiContentView.markerText
    private val playButton = uiContentView.videoPlayBtn
    private val hintTextView = uiContentView.hintText

    private val exoPlayer = ExoPlayerFactory.newSimpleInstance(playerView.context)
    private val dataSourceFactory = DefaultDataSourceFactory(
        playerView.context,
        Util.getUserAgent(playerView.context, VideoEditApplication.instance.packageName)
    )
    private val progressFuncList = ArrayList<(progress: Float, currentTime: Long) -> Unit>()
    private var progressUpdateAction = this::updateProgress
    private val progressUpdateDistance = 500L
    private var playingVideoSource: VideoSourceBean? = null
    private var controlVideoEnable = true
    var playOption: Int = 0
    var currentIndex: Int = 0
    var playFilter: PlayFilter? = null

    init {
        playerView.player = exoPlayer
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        playButton.setOnClickListener {
            if (!controlVideoEnable) {
                return@setOnClickListener
            }
            if (isPlaying()) {
                pauseVideo()
            } else {
                resumeVideo()
            }
        }
        playerView.setOnClickListener {
            if (!controlVideoEnable) {
                return@setOnClickListener
            }
            if (isPlaying()) {
                pauseVideo()
            } else {
                resumeVideo()
            }
        }
        resizePlayerView()
    }

    private fun resizePlayerView() {
        if (videoModel.outputVideoWidth > videoModel.outputVideoHeight) {
            val height = playerView.width.toFloat() * videoModel.outputVideoHeight / videoModel.outputVideoWidth
            playerView.adjustHeight(height.toInt())
            markerText.adjustHeight(height.toInt())
        } else {
            val width = playerView.height * videoModel.outputVideoWidth / videoModel.outputVideoHeight
            playerView.adjustWidth(width)
            markerText.adjustWidth(width)
        }
    }

    fun play(index: Int, option: Int, filter: PlayFilter? = null) {
        if (index < 0 || index >= videoModel.count()) {
            return
        }

        videoModel.updateCurrentIndex(index)
        currentIndex = index
        if (videoModel.videoInfos[index].videoBean != playingVideoSource || filter != playFilter) {
            playFilter = filter
            playVideo(videoModel.currentVideoSourceBean())
        }
        updateWaterMarker()
        updateInsertHint(option)
    }

    private fun updateInsertHint(option: Int) {
        if (option == OPTION_INSERT_LEFT || option == OPTION_INSERT_RIGHT) {
            hintTextView.beVisible()
            pauseVideo()
        } else {
            hintTextView.beGone()
            resumeVideo()
        }
    }

    fun updateWaterMarker() {
        markerText.text = "Noah"
        markerText.beVisibleIf(videoModel.currentVideoEditBean().haveMarker)
        markerText.postDelayed({
            if (videoModel.currentVideoEditBean().haveMarker) {
                generateWaterMarkerIfNotExist()
            }
        }, 100)
    }

    fun addOnProgressChange(func: (progress: Float, currentTime: Long) -> Unit) {
        progressFuncList.add(func)
    }

    fun removeOnProgressChange(func: (progress: Float, currentTime: Long) -> Unit) {
        progressFuncList.remove(func)
    }

    fun onDestroy() {
        exoPlayer.release()
    }

    fun pauseVideo() {
        exoPlayer.playWhenReady = false
        playerView.removeCallbacks(progressUpdateAction)
        playButton.isSelected = false
    }

    fun resumeVideo() {
        exoPlayer.playWhenReady = true
        playerView.postDelayed(progressUpdateAction, progressUpdateDistance)
        playButton.isSelected = true
    }

    fun isPlaying(): Boolean = exoPlayer.playWhenReady

    fun enableUserPlayVideo(enable: Boolean) {
        controlVideoEnable = enable
    }

    /**
     * @param time 毫秒
     */
    fun seekVideo(time: Long) {
        exoPlayer.seekTo(time)
    }

    private fun playVideo(videoSource: VideoSourceBean) {
        exoPlayer.prepare(createDataSource(videoSource))
        resumeVideo()
    }

    private fun createDataSource(videoSource: VideoSourceBean): MediaSource {
        playingVideoSource = videoSource
        var mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(videoSource.path.toFile()))

        var start = videoSource.startTime
        var end = videoSource.endTime
        playFilter?.filt()?.let {
            end = start + it[1]
            start += it[0]
        }
        if (start != 0L || end != videoSource.getVideoRawDuration()) {
            mediaSource = ClippingMediaSource(mediaSource, start * 1000, end * 1000)
        }
        return mediaSource
    }

    private fun updateProgress() {
        val position = exoPlayer.currentPosition
        val progress = position.toFloat() / exoPlayer.duration
        progressFuncList.forEach { it.invoke(progress, position) }
        playerView.postDelayed(progressUpdateAction, progressUpdateDistance)
    }

    private fun generateWaterMarkerIfNotExist() {
        if (videoModel.imageMarker == null) {
            val bitmap =
                Bitmap.createBitmap(markerText.width, markerText.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            markerText.draw(canvas)
            videoModel.imageMarker = FileUtils.saveBitmapToLocal(bitmap)
        }
    }

    interface PlayFilter {
        fun filt(): Array<Int>
    }
}