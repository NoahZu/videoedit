package com.haoqi.teacher.videoedit.manager

import java.io.File


interface VideoMenuControlManager {

    enum class Action {
        ACTION_EMPTY, ACTION_INSERT_VIDEO, ACTION_INSERT_TITLE
    }

    //主动唤起xxpanel
    fun openCutPanel()

    fun openMarkerPanel()
    fun openSortPanel()
    fun openTitleInputPanel()
    fun openSplitPanel()

    //xxpanel的结束callback
    fun onEndCut(isCancel: Boolean, isAllRefresh: Boolean)

    fun onEndMarker()
    fun onEndSort()
    fun onEndTitleInput(isCancel: Boolean, file: File? = null)
    fun onEndSplitPanel(isCancel: Boolean, endAction: Action)

    fun exitPage()
    fun exitPageDirect()
}