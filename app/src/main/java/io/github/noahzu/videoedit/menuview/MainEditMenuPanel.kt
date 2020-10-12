package io.github.noahzu.videoedit.menuview

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haoqi.teacher.videoedit.Constant
import com.haoqi.teacher.videoedit.adapter.VideoListAdapter
import com.haoqi.teacher.videoedit.menuview.EditMenuPanel
import com.haoqi.teacher.videoedit.utils.StringFormatUtls
import com.yanzhenjie.album.Action
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ViewConfigFactory
import io.github.noahzu.videoedit.base.ToastUtils
import io.github.noahzu.videoedit.ext.beVisibleIf
import io.github.noahzu.videoedit.ext.toFile
import kotlinx.android.synthetic.main.view_edit_video_main_menu.view.*
import java.io.File
import java.util.*


open class MainEditMenuPanel : EditMenuPanel {

    private lateinit var videoListAdapter: VideoListAdapter

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun layoutId(): Int = R.layout.view_edit_video_main_menu

    override fun initialize() {
        initVideoList()
        initListener()
    }

    private fun initListener() {
        videoPlayControlManager.addOnProgressChange { progress, time ->
            videoSeekBar.progress = (progress * 100).toInt()
            currentTimeText.text = StringFormatUtls.formatMillSecondsToString(time)
        }
        cutBtn.setOnClickListener {
            if (editingVideoModel.currentVideoSourceBean().getVideoDuration() <= Constant.PART_MIN_DURATION) {
                ToastUtils.showToast("太短了，不能再剪切了哦~")
                return@setOnClickListener
            }
            videoMenuControlManager.openCutPanel()
        }
        deleteBtn.setOnClickListener {
            if (editingVideoModel.count() < 2) {
                ToastUtils.showToast("最后一个视频不能删除哦~")
                return@setOnClickListener
            }
            deleteCurrentVideo()
        }
        markerBtn.setOnClickListener {
            videoMenuControlManager.openMarkerPanel()
        }
        sortBtn.setOnClickListener {
            videoMenuControlManager.openSortPanel()
        }

        addVideoLeftBtn.setOnClickListener {
            insertVideo()
        }

        addTitleBtn.setOnClickListener {
            insertTitle()
        }

        splitBtn.setOnClickListener {
            videoMenuControlManager.openSplitPanel()
        }

        videoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var isTracking = false
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isTracking) {
                    val seek = currentEditVideoSource().getVideoDuration() * (progress.toFloat() / 100)
                    videoPlayControlManager.seekVideo(seek.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
                videoPlayControlManager.pauseVideo()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isTracking = false
                videoPlayControlManager.resumeVideo()
            }

        })
    }

    override fun onCancelEditAction() = Unit

    override fun onSaveEditAction() = Unit

    override fun panelTitle(): Int = -1

    /**
     * 删除当前视频
     */
    private fun deleteCurrentVideo() {
        editingVideoModel.deleteCurrentVideo()
        editingVideoModel.currentVideoSourceBean().extra = VideoListAdapter.OPTION_SELECT
        videoListAdapter.selectedPosition = editingVideoModel.currentIndex
        videoListAdapter.notifyDataSetChanged()
        videoListAdapter.onSelectVideoFunc.invoke(editingVideoModel.currentIndex, VideoListAdapter.OPTION_SELECT)
    }

    /**
     * 更新当前视频 比如剪裁
     */
    fun updateCurrentVideo() {
        editingVideoModel.currentVideoSourceBean().extra = VideoListAdapter.OPTION_SELECT
        videoListAdapter.notifyItemChanged(editingVideoModel.currentIndex)
        reInvokeSelectFunc()
    }

    /**
     * 做过移动，重新刷新列表
     */
    fun refreshVideoList() {
        videoListAdapter.notifyDataSetChanged()
        reInvokeSelectFunc()
    }

    private fun reInvokeSelectFunc() = videoListAdapter.reInvokeSelectFunc()

    /**
     * 初始化视频列表
     */
    private fun initVideoList() {
        videoListAdapter = VideoListAdapter(editingVideoModel.videoInfos, editingVideoModel, context).apply {
            onSelectVideoFunc = { index, option -> onItemSelect(index, option) }
        }
        videoListView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        videoListView.adapter = videoListAdapter
        post { videoListAdapter.onSelectVideoFunc.invoke(0, VideoListAdapter.OPTION_SELECT) }
    }

    /**
     * 当视频选中或者视频的+选中
     */
    private fun onItemSelect(index: Int, option: Int) {
        durationTimeText.text =
            StringFormatUtls.formatMillSecondsToString(videoListAdapter.getMyItemData(index).getVideoDuration())
        videoPlayControlManager.play(index, option)
        arrayOf(videoSeekBar, videoPlayBtn).forEach {
            it.isEnabled = option == VideoListAdapter.OPTION_SELECT
            it.isClickable = option == VideoListAdapter.OPTION_SELECT
        }
        selectMenu(option)
    }

    private fun selectMenu(option: Int) {
        selectVideoMenuLayout.beVisibleIf(option == VideoListAdapter.OPTION_SELECT)
        addVideoMenuLayout.beVisibleIf(option != VideoListAdapter.OPTION_SELECT)
    }

    fun insertVideo() {
        Album.video(context)
            .singleChoice()
            .columnCount(4)
            .camera(false)
            .widget(ViewConfigFactory.createAlbumWidgetVideo())
            .onResult(object : Action<ArrayList<AlbumFile>> {
                override fun onAction(result: ArrayList<AlbumFile>) {
                    processVideo(result)
                }

            }).start()
    }

    /**
     * 视频检查与处理
     */
    private fun processVideo(result: ArrayList<AlbumFile>) {
        if (result.isEmpty()) {
            ToastUtils.showToast("请选择一个视频")
            return
        }
        val videoFile = result[0].path.toFile()
        addVideo(videoFile)
    }

    fun addVideo(videoFile: File) {
        // insert video in last position
        if (currentEditVideoSource().extra == VideoListAdapter.OPTION_INSERT_RIGHT) {
            editingVideoModel.appendVideo(videoFile)
        } else {
            editingVideoModel.addVideoInCurrentIndex(videoFile)
        }
        videoListAdapter.notifyDataSetChanged()
        videoListAdapter.reInvokeSelectFunc()
        videoListView.smoothScrollToPosition(videoListAdapter.selectedPosition)
    }

    fun insertTitle() {
        videoMenuControlManager.openTitleInputPanel()
    }
}