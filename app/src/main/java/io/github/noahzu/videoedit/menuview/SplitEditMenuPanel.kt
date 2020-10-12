package com.haoqi.teacher.videoedit.menuview

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.haoqi.teacher.videoedit.Constant
import com.haoqi.teacher.videoedit.manager.VideoMenuControlManager
import com.haoqi.teacher.videoedit.utils.StringFormatUtls
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.base.ToastUtils
import kotlinx.android.synthetic.main.view_edit_video_split_menu.view.*
import java.util.*

class SplitEditMenuPanel : EditMenuPanel {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun layoutId(): Int = R.layout.view_edit_video_split_menu

    override fun initialize() {

        videoTimeLineView.onSlideFunc = {
            slideTimeText.text = StringFormatUtls.formatMillSecondsToString(it)
            videoPlayControlManager.seekVideo(it)
            canSplit().let {
                splitCutBtn.isEnabled = it
                splitCutBtn.setTextColor(ResourcesCompat.getColor(resources,if (it) R.color.color_black else R.color.color_light_gray,null))
            }
        }

        splitCutBtn.setOnClickListener {
            if (splitVideo()) {
                closeEditAction()
                videoMenuControlManager.onEndSplitPanel(false, VideoMenuControlManager.Action.ACTION_EMPTY)
            } else {
                ToastUtils.showToast("片段太短，无法分割哦~")
            }

        }

        splitInsertVideoBtn.setOnClickListener {
            splitVideo()
            closeEditAction()
            videoMenuControlManager.onEndSplitPanel(false, VideoMenuControlManager.Action.ACTION_INSERT_VIDEO)
        }

        splitInsertTitleBtn.setOnClickListener {
            splitVideo()
            closeEditAction()
            videoMenuControlManager.onEndSplitPanel(false, VideoMenuControlManager.Action.ACTION_INSERT_TITLE)
        }

    }

    override fun startEditAction() {
        super.startEditAction()

        videoTimeLineView.loadFile(editingVideoModel.currentVideoSourceBean(), 1000)
        durationTimeText.text = String.format(
            Locale.CHINA,
            context.resources.getString(R.string.time_seconds),
            editingVideoModel.currentVideoSourceBean().getVideoDuration() / 1000
        )
        videoPlayControlManager.pauseVideo()
        videoPlayControlManager.enableUserPlayVideo(false)

    }

    override fun closeEditAction() {
        super.closeEditAction()
        videoPlayControlManager.resumeVideo()
        videoPlayControlManager.enableUserPlayVideo(true)
    }

    override fun onCancelEditAction() {
        closeEditAction()
        videoMenuControlManager.onEndSplitPanel(true, VideoMenuControlManager.Action.ACTION_EMPTY)
    }

    override fun onSaveEditAction() {
        if (splitVideo()) {
            closeEditAction()
            videoMenuControlManager.onEndSplitPanel(false, VideoMenuControlManager.Action.ACTION_EMPTY)
        } else {
            ToastUtils.showToast("片段太短，无法分割哦~")
        }
    }

    override fun panelTitle(): Int = R.string.edit_split

    /**
     * 分割视频
     */
    private fun splitVideo(): Boolean {
        if (canSplit()) {
            videoEditControlManger.startSplitCurrentVideo(videoTimeLineView.currentSelectTime)
            currentEditVideo().refreshCover(context)
            return true
        }
        return false
    }

    private fun canSplit(): Boolean {
        if (videoTimeLineView.currentSelectTime <= Constant.PART_MIN_DURATION) {
            return false
        }
        if (currentEditVideoSource().getVideoDuration() - videoTimeLineView.currentSelectTime <= Constant.PART_MIN_DURATION) {
            return false
        }
        return true
    }
}