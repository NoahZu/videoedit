package io.github.noahzu.videoedit.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haoqi.teacher.videoedit.data.VideoEditBean
import com.haoqi.teacher.videoedit.utils.StringFormatUtls
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ext.toFile
import io.github.noahzu.videoedit.utils.VideoSourceBean
import kotlinx.android.synthetic.main.item_video_list2.view.*
import java.util.*

class SortVideoListAdapter(val data: List<VideoEditBean>, val context: Context) : RecyclerView.Adapter<SortVideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortVideoViewHolder {
        return SortVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_list2, parent, false))
    }

    override fun onBindViewHolder(holder: SortVideoViewHolder, position: Int) {
        holder.bindData(getMyItemData(position), position)
    }

    override fun getItemCount(): Int {
        return data.size;
    }

    fun getMyItemData(position: Int): VideoSourceBean {
        return data.get(position).videoBean
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}

class SortVideoViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {

    fun bindData(videoBean: VideoSourceBean, position: Int) {
        rootView.videoDuration.text = StringFormatUtls.formatMillSecondsToString(videoBean.getVideoDuration())
        rootView.videoIndexText.text = (position + 1).toString()
        Glide.with(rootView.context).load(Uri.fromFile(videoBean.coverImage.toFile())).into(rootView.videoCover)
    }

}