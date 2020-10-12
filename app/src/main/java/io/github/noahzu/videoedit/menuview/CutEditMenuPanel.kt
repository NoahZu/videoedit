package com.haoqi.teacher.videoedit.menuview

import android.content.Context
import android.util.AttributeSet
import com.haoqi.teacher.videoedit.Constant
import com.haoqi.teacher.videoedit.manager.VideoPlayControlManager
import com.haoqi.teacher.videoedit.widget.RangeSeekBar
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.base.ToastUtils
import kotlinx.android.synthetic.main.view_edit_video_cut_menu.view.*
import java.io.File
import java.util.*

class CutEditMenuPanel : EditMenuPanel {
    private var duration = 0L
    private val timeFormat by lazy { context.resources.getString(R.string.time_seconds) }
    private val cuttedTimeFormat by lazy { context.resources.getString(R.string.cutted_time) }
    private val progressFunc: (progress: Float, currentTime: Long) -> Unit by lazy {
        { progress: Float, currentTime: Long ->
            onVideoProgressChange(progress)
        }
    }


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutId(): Int = R.layout.view_edit_video_cut_menu

    override fun initialize() {
        initListener()
    }

    private fun initListener() {
        btnSaveSelect.setOnClickListener {
            saveSelect()
        }

        btnDeleteSelect.setOnClickListener {
            deleteSelect()
        }

        btnResetSelect.setOnClickListener {
            reset()
        }
    }


    private fun reset() {
        rangeSeekBar.reset()
        cuttedDurationText.text = String.format(Locale.CHINA, cuttedTimeFormat, duration / 1000)
        slideText.text = ""
    }

    override fun startEditAction() {
        super.startEditAction()
        post {
            videoTimeLineView.loadVideoTimeLine(File(editingVideoModel.currentVideoSourceBean().path))
        }
        setListener()
        setVideoData()
        videoPlayControlManager.pauseVideo()
        videoPlayControlManager.enableUserPlayVideo(false)
        videoPlayControlManager.addOnProgressChange(progressFunc)
    }


    override fun closeEditAction() {
        super.closeEditAction()
        resetListener()
        videoPlayControlManager.enableUserPlayVideo(true)
        videoPlayControlManager.removeOnProgressChange(progressFunc)
        reset()
    }

    override fun onCancelEditAction() {
        onBackPress()
    }

    override fun onSaveEditAction() {
        saveSelect()
    }

    override fun onBackPress() {
        super.onBackPress()
        videoMenuControlManager.onEndCut(true, false)
    }


    private fun setVideoData() {
        duration = currentEditVideoSource().getVideoDuration()
        durationText.text =
            String.format(Locale.CHINA, context.resources.getString(R.string.time_seconds), duration / 1000)
        cuttedDurationText.text =
            String.format(Locale.CHINA, cuttedTimeFormat, duration / 1000)
        rangeSeekBar.maxProgress = (1000f / duration)
    }

    private fun setListener() {
        rangeSeekBar.onRangeSeekBarChangeListener = object : RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(seekBar: RangeSeekBar, cuttedProgress: Float, slideProgress: Float) {
                cuttedDurationText.text =
                    String.format(Locale.CHINA, cuttedTimeFormat, (duration * cuttedProgress).toInt() / 1000)
                videoPlayControlManager.seekVideo((cuttedProgress * duration).toLong())
                slideText.text = String.format(Locale.CHINA, timeFormat, (duration * slideProgress).toInt() / 1000)
            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBar) {
                videoPlayControlManager.pauseVideo()
            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBar, leftProgress: Float, rightProgress: Float) {
                val playFilter = object : VideoPlayControlManager.PlayFilter {
                    override fun filt(): Array<Int> {
                        val leftPosition = duration * leftProgress
                        val rightPosition = duration * rightProgress
                        return arrayOf(leftPosition.toInt(), rightPosition.toInt())
                    }
                }
                videoPlayControlManager.run {
                    play(currentIndex, playOption, playFilter)
                }
            }
        }
    }

    private fun resetListener() {
        rangeSeekBar.onRangeSeekBarChangeListener = null
    }

    override fun panelTitle(): Int = R.string.edit_cut

    private fun saveSelect() {
        val videoSource = editingVideoModel.currentVideoSourceBean()
        val duration = videoSource.getVideoDuration()

        val startTime = videoSource.startTime + rangeSeekBar.getLeftPosition() * duration
        val endTime = videoSource.startTime + rangeSeekBar.getRightPosition() * duration
        if (endTime - startTime < Constant.PART_MIN_DURATION) {
            ToastUtils.showToast("太短了，不能再剪切了哦~")
            return
        }

        val newVideoSource = videoSource.createByTime(startTime.toLong(), endTime.toLong())

        editingVideoModel.updateCurrentVideo(newVideoSource)
        editingVideoModel.currentVideoEditBean().refreshCover(context)
        closeEditAction()
        videoMenuControlManager.onEndCut(false, false)
    }

    private fun deleteSelect() {
        val videoSource = editingVideoModel.currentVideoSourceBean()
        val leftPosition = rangeSeekBar.getLeftPosition() * duration
        val rightPosition = rangeSeekBar.getRightPosition() * duration

        val startTime = videoSource.startTime + leftPosition
        val endTime = videoSource.startTime + rightPosition

        if (leftPosition < Constant.PART_MIN_DURATION) {
            ToastUtils.showToast("左边太短了，无法剪切哦~")
            return
        }
        if (videoSource.endTime - rightPosition < Constant.PART_MIN_DURATION) {
            ToastUtils.showToast("右边太短了，无法剪切哦~")
            return
        }

        videoEditControlManger.startSplitCurrentVideo(startTime.toLong(), endTime.toLong())
        closeEditAction()
        videoMenuControlManager.onEndCut(false, true)
    }

    private fun onVideoProgressChange(progress: Float) {
        if (!videoPlayControlManager.isPlaying()) {
            return
        }
        rangeSeekBar.updateIndicator(progress)
    }

}