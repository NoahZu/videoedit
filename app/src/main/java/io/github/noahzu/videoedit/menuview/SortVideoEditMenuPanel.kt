package io.github.noahzu.videoedit.menuview

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.github.noahzu.videoedit.adapter.SortVideoListAdapter
import com.haoqi.teacher.videoedit.data.VideoEditBean
import com.haoqi.teacher.videoedit.menuview.EditMenuPanel
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.widget.DividerDecoration
import kotlinx.android.synthetic.main.view_edit_video_main_menu.view.*

class SortVideoEditMenuPanel : EditMenuPanel {

    private lateinit var videoListAdapter: SortVideoListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val tempArrayList = ArrayList<VideoEditBean>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutId(): Int = R.layout.view_edit_video_sort_menu

    override fun initialize() {
        createItemTouchHelper()
        initVideoList()
    }

    override fun startEditAction() {
        super.startEditAction()

        tempArrayList.run {
            clear()
            addAll(editingVideoModel.videoInfos)
        }

        videoListAdapter.notifyDataSetChanged()
        videoPlayControlManager.pauseVideo()
    }

    override fun closeEditAction() {
        super.closeEditAction()

        editingVideoModel.correctCurrentIndex()
        videoMenuControlManager.onEndSort()
        videoPlayControlManager.resumeVideo()
    }


    override fun onCancelEditAction() {
        closeEditAction()
    }

    override fun onSaveEditAction() {
        editingVideoModel.videoInfos.run {
            clear()
            addAll(tempArrayList)
        }
        closeEditAction()
    }

    override fun panelTitle(): Int = R.string.edit_move

    /**
     * 初始化视频列表
     */
    private fun initVideoList() {
        videoListAdapter = SortVideoListAdapter(tempArrayList, context);
        videoListView.layoutManager = GridLayoutManager(context, 4)
        videoListView.adapter = videoListAdapter
        videoListView.addItemDecoration(DividerDecoration(R.color.main_bg, 20f))
        itemTouchHelper.attachToRecyclerView(videoListView)

    }

    private fun createItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.UP or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                videoListAdapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState != 0) {
                    viewHolder?.itemView?.scaleX = 1.2f
                    viewHolder?.itemView?.scaleY = 1.2f
                    viewHolder?.itemView?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.scaleX = 1f
                viewHolder.itemView.scaleY = 1f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
        })
    }
}
