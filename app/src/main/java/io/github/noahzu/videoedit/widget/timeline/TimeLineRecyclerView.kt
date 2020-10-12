package com.haoqi.teacher.videoedit.widget.timeline

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.noahzu.videoedit.utils.SizeUtils
import io.github.noahzu.videoedit.utils.VideoSourceBean

/**
 * 不固定长度的TimeLiveView
 */
class TimeLineRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private var slideWidth = 0
    private var sumWidth = 0L
    var currentSelectTime = 0L
    lateinit var onSlideFunc: (time: Long) -> Unit


    fun loadFile(videoSourceBean: VideoSourceBean, frameTime: Long) {
        currentSelectTime = 0
        slideWidth = 0
        val duration = videoSourceBean.getVideoDuration()
        val count = duration / frameTime
        sumWidth = count * SizeUtils.dp2px(50f)
        clearOnScrollListeners()
        val adapter = TimeLineAdapter(count.toInt(), context, videoSourceBean)
        setAdapter(adapter)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter.notifyDataSetChanged()
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                slideWidth += dx
                currentSelectTime = ((slideWidth.toFloat() / sumWidth) * duration).toLong()
                onSlideFunc.invoke(currentSelectTime)
            }
        })
    }


    class TimeLineAdapter(val count: Int, val context: Context, val data: VideoSourceBean) : Adapter<ViewHolder>() {
        private val screenWidth: Int = io.github.noahzu.videoedit.base.DisplayUtils.getScreenWidthPixels(context.applicationContext)

        companion object {
            const val VIEW_TYPE_VIDEO = 0x01
            const val VIEW_TYPE_HEADER = 0x02
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                VIEW_TYPE_HEADER -> {
                    val view = View(context)
                    view.setBackgroundColor(Color.parseColor("#fff8f8f8"))
                    view.layoutParams = LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT)
                    TimeLineHeadViewHolder(view)
                }
                else -> {
                    val imageView = ImageView(context)
                    Glide.with(context).load(data.coverImage).into(imageView)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.layoutParams = LayoutParams(SizeUtils.dp2px(50f), ViewGroup.LayoutParams.MATCH_PARENT)
                    TimeLineViewHolder(imageView)
                }
            }
        }

        override fun getItemCount(): Int = count + 2

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0 || position == itemCount - 1) VIEW_TYPE_HEADER else VIEW_TYPE_VIDEO
        }

    }


    class TimeLineViewHolder(itemView: View) : ViewHolder(itemView)
    class TimeLineHeadViewHolder(itemView: View) : ViewHolder(itemView)
}