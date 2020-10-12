package com.haoqi.teacher.videoedit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.ext.beGone
import io.github.noahzu.videoedit.ext.beVisible
import kotlinx.android.synthetic.main.view_video_edit_title.view.*
import java.util.*

class VideoEditTitleView : RelativeLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var clickEventInterceptorChain = ArrayList<ClickEventInterceptor>()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_video_edit_title, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initListener()
    }

    private fun initListener() {
        val interceptorListener = object : OnClickListener {
            override fun onClick(v: View?) {
                for (i in clickEventInterceptorChain.size - 1 downTo 0) {
                    if (clickEventInterceptorChain[i].onClickEventIntercept(v?.id ?: 0)) {
                        return
                    }
                    continue
                }
            }

        }
        titleBackBtn.setOnClickListener(interceptorListener)
        titleRightBtn.setOnClickListener(interceptorListener)
        titleLeftBtn.setOnClickListener(interceptorListener)
    }

    fun addClickEventInterceptor(interceptor: ClickEventInterceptor) {
        clickEventInterceptorChain.add(interceptor)
    }

    fun removeClickEventInterceptor(interceptor: ClickEventInterceptor) {
        clickEventInterceptorChain.remove(interceptor)
    }


    fun resetMainEditStatus() {
        titleRightBtn.setText(R.string.save_and_share)
        titleTextView.setText(R.string.edit_video)
        titleRightBtn.beVisible()
        titleBackBtn.beVisible()
        titleLeftBtn.beGone()
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setTitle(strRes: Int) {
        titleTextView.setText(strRes)
    }

    fun setRightText(text: String) {
        titleRightBtn.text = text
    }

    fun setRightText(strRes: Int) {
        titleRightBtn.setText(strRes)
    }

    fun setLeftText(strRes: Int) {
        titleLeftBtn.setText(strRes)
    }

    interface ClickEventInterceptor {
        fun onClickEventIntercept(viewId: Int): Boolean
    }
}