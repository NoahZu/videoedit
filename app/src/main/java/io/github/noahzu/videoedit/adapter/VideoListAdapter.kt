package com.haoqi.teacher.videoedit.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haoqi.teacher.videoedit.data.VideoEditBean
import com.haoqi.teacher.videoedit.data.VideoEditModel
import com.haoqi.teacher.videoedit.utils.StringFormatUtls
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ext.beVisibleIf
import io.github.noahzu.videoedit.ext.toFile
import io.github.noahzu.videoedit.utils.VideoSourceBean
import kotlinx.android.synthetic.main.item_video_list.view.*

class VideoListAdapter(val data: List<VideoEditBean>, val editingVideoModel: VideoEditModel, val context: Context) :
    RecyclerView.Adapter<VideoViewHolder>() {

    companion object {
        const val OPTION_UNSELECT = 0
        const val OPTION_SELECT = 1
        const val OPTION_INSERT_LEFT = 2
        const val OPTION_INSERT_RIGHT = 3

    }

    lateinit var onSelectVideoFunc: (index: Int, option: Int) -> Unit
    var selectedPosition = 0

    init {
        getMyItemData(selectedPosition).extra = OPTION_SELECT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.rootView.run {
            addVideoLeftBtn.setOnClickListener {
                selectItemInner(position, OPTION_INSERT_LEFT)
            }
            addVideoRightBtn.setOnClickListener {
                selectItemInner(position, OPTION_INSERT_RIGHT)
            }
            videoCover.setOnClickListener {
                selectItemInner(position, OPTION_SELECT)
            }
        }

        holder.bindData(getMyItemData(position), position, position == itemCount - 1)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun changeSelectStatus(position: Int, option: Int) {
        if (position == selectedPosition) {
            getMyItemData(position).extra = option
            notifyItemChanged(selectedPosition)
            return
        }

        getMyItemData(selectedPosition).extra = OPTION_UNSELECT
        getMyItemData(position).extra = option

        notifyItemChanged(selectedPosition)
        notifyItemChanged(position)

        selectedPosition = position
    }

    private fun selectItemInner(position: Int, option: Int) {
        if (position == selectedPosition && option == getMyItemData(selectedPosition).extra) {
            return
        }

        changeSelectStatus(position, option)
        onSelectVideoFunc.invoke(position, option)
    }

    fun reInvokeSelectFunc() {
        selectedPosition = editingVideoModel.currentIndex
        onSelectVideoFunc.invoke(selectedPosition, (getMyItemData(selectedPosition).extra as Int?) ?: 0)
    }


    fun getMyItemData(position: Int): VideoSourceBean {
        return data.get(position).videoBean
    }
}

class VideoViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {

    fun bindData(videoBean: VideoSourceBean, position: Int, isLastItem: Boolean) {
        rootView.videoDuration.text = StringFormatUtls.formatMillSecondsToString(videoBean.getVideoDuration())
        rootView.addVideoRightBtn.beVisibleIf(isLastItem)
        ((videoBean.extra as? Int) ?: 0).let {
            when (it) {
                VideoListAdapter.OPTION_SELECT -> {
                    rootView.videoCover.isSelected = true
                    rootView.addVideoLeftBtn.isSelected = false
                    rootView.addVideoRightBtn.isSelected = false
                }
                VideoListAdapter.OPTION_INSERT_LEFT -> {
                    rootView.videoCover.isSelected = false
                    rootView.addVideoLeftBtn.isSelected = true
                    rootView.addVideoRightBtn.isSelected = false
                }
                VideoListAdapter.OPTION_INSERT_RIGHT -> {
                    rootView.videoCover.isSelected = false
                    rootView.addVideoLeftBtn.isSelected = false
                    rootView.addVideoRightBtn.isSelected = true
                }
                else -> {
                    rootView.videoCover.isSelected = false
                    rootView.addVideoLeftBtn.isSelected = false
                    rootView.addVideoRightBtn.isSelected = false
                }
            }
        }
        rootView.videoIndexText.text = (position + 1).toString()
        Glide.with(rootView.context).load(Uri.fromFile(videoBean.coverImage.toFile())).into(rootView.videoCover)
    }

}