package com.haoqi.teacher.videoedit.menuview

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ext.beVisibleIf
import kotlinx.android.synthetic.main.view_edit_video_marker_menu.view.*

class MarkerEditMenuPanel : EditMenuPanel {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutId(): Int = R.layout.view_edit_video_marker_menu

    override fun initialize() {
        markerSwitchBtn.setOnCheckedChangeListener { view, isChecked ->
            showOrHideSelectLayout(isChecked)
        }
    }

    private fun applyAllVideo(isChecked: Boolean) {
        editingVideoModel.updateVideoMarker(isChecked, true)
        videoPlayControlManager.updateWaterMarker()
    }


    private fun applyCurrVideo(isChecked: Boolean) {
        editingVideoModel.updateVideoMarker(isChecked, false)
        videoPlayControlManager.updateWaterMarker()
    }

    override fun startEditAction() {
        super.startEditAction()
        markerTitleContent.text = "Noah"
        setRadioGroup()
    }

    private fun setRadioGroup() {
        markerSwitchBtn.setChecked(currentEditVideo().haveMarker)
        showOrHideSelectLayout(currentEditVideo().haveMarker)
    }

    private fun showOrHideSelectLayout(isShow: Boolean) {
        arrayOf(dividerLine, rangeHintText, hintApplyCurrentText, hintApplyAllText, selectApplyRadioGroup).forEach {
            it.beVisibleIf(
                isShow
            )
        }
        applyCurrentRadio.isChecked = true
        markerTitleContent.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (isShow) R.color.color_text_666666 else R.color.color_text_999999,
                null
            )
        )
    }

    override fun closeEditAction() {
        super.closeEditAction()
        videoMenuControlManager.onEndMarker()
    }

    override fun onCancelEditAction() {
        closeEditAction()
    }

    override fun onSaveEditAction() {
        saveCheckStatus()
        closeEditAction()
    }

    override fun panelTitle(): Int = R.string.water_marker

    private fun saveCheckStatus() {
        if (!markerSwitchBtn.isOpened) {
            applyCurrVideo(false)
            return
        }
        if (applyCurrentRadio.isChecked) {
            applyAllVideo(false)
            applyCurrVideo(true)
            return
        }
        if (applyAllRadio.isChecked) {
            applyAllVideo(true)
        }
    }
}