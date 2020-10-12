package com.haoqi.teacher.videoedit.menuview

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.contains
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.haoqi.teacher.videoedit.data.VideoEditModel
import com.haoqi.teacher.videoedit.manager.VideoEditControlManager
import com.haoqi.teacher.videoedit.manager.VideoMenuControlManager
import com.haoqi.teacher.videoedit.manager.VideoPlayControlManager
import com.haoqi.teacher.videoedit.widget.VideoEditTitleView
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ext.beGone
import io.github.noahzu.videoedit.ext.beVisible
import kotlinx.android.synthetic.main.view_video_edit_title.view.*

abstract class EditMenuPanel : RelativeLayout, VideoEditTitleView.ClickEventInterceptor {
    protected lateinit var editingVideoModel: VideoEditModel
    protected lateinit var editPage: Activity
    protected lateinit var editTitleBar: VideoEditTitleView
    protected lateinit var editMenuArea: RelativeLayout
    protected lateinit var videoPlayControlManager: VideoPlayControlManager
    protected lateinit var videoMenuControlManager: VideoMenuControlManager
    protected lateinit var videoEditControlManger: VideoEditControlManager

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(layoutId(), this)
    }

    /**
     * 初始化编辑上下文
     */
    fun initEditContext(
        videoModel: VideoEditModel,
        titleView: VideoEditTitleView,
        menuArea: RelativeLayout,
        playControlManager: VideoPlayControlManager,
        menuControlManager: VideoMenuControlManager,
        editControlManager: VideoEditControlManager
    ) {
        editingVideoModel = videoModel
        editPage = context as Activity
        editTitleBar = titleView
        editMenuArea = menuArea
        videoPlayControlManager = playControlManager
        videoMenuControlManager = menuControlManager
        videoEditControlManger = editControlManager
        initialize()
    }

    open fun startEditAction() {
        showWithAnim()
        setEditTitleBar()
    }

    private fun setEditTitleBar() {
        editTitleBar.setRightText(R.string.finish)
        editTitleBar.titleBackBtn.beGone()
        editTitleBar.titleLeftBtn.beVisible()
        editTitleBar.setTitle(panelTitle())
        editTitleBar.addClickEventInterceptor(this)
    }

    open fun closeEditAction() {
        hideWithAnim()
        resetEditTitleBar()
    }

    override fun onClickEventIntercept(viewId: Int): Boolean {
        when (viewId) {
            R.id.titleLeftBtn -> {
                onCancelEditAction()
            }
            R.id.titleRightBtn -> {
                onSaveEditAction()
            }
        }
        return true
    }

    private fun resetEditTitleBar() {
        editTitleBar.resetMainEditStatus()
        editTitleBar.removeClickEventInterceptor(this)
    }

    private fun showWithAnim() {
        if (!editMenuArea.contains(this)) {
            TransitionManager.beginDelayedTransition(editMenuArea, Slide(Gravity.BOTTOM).setDuration(200))
            editMenuArea.addView(this)
        }
    }

    private fun hideWithAnim() {
        if (editMenuArea.contains(this)) {
            TransitionManager.beginDelayedTransition(editMenuArea, Slide(Gravity.BOTTOM).setDuration(200))
            editMenuArea.removeView(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroyPanel()
    }

    fun currentEditVideoSource() = editingVideoModel.currentVideoSourceBean()

    fun currentEditVideo() = editingVideoModel.currentVideoEditBean()

    open fun onBackPress() {
        closeEditAction()
    }

    open fun destroyPanel() {

    }

    abstract fun layoutId(): Int

    abstract fun initialize()

    abstract fun panelTitle(): Int

    abstract fun onCancelEditAction()

    abstract fun onSaveEditAction()
}