package io.github.noahzu.videoedit.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.VideoEditApplication
import io.github.noahzu.videoedit.base.DisplayUtils

/**
 * fujiuhong
 * 修改自 EasyRecyclerView
 */
class DividerDecoration : RecyclerView.ItemDecoration {
    private var mColorDrawable: ColorDrawable? = null
    private var mHeight: Int = 0
    private var mPaddingLeft: Int=0
    private var mPaddingRight: Int=0
    private var mDrawLastItem = false
    private var mDrawHeaderFooter = false

    constructor(@ColorRes color: Int= R.color.divider, dpHeight: Float=0.7f, isDrawLastItem:Boolean=false) {
        this.mColorDrawable = ColorDrawable(ContextCompat.getColor(VideoEditApplication.getAppContext(), color))
        this.mHeight = DisplayUtils.dp2px(VideoEditApplication.getAppContext(),dpHeight)
        this.mDrawLastItem = isDrawLastItem
    }

    constructor(@ColorRes color: Int=R.color.divider, dpHeight: Float=0.7f, dpPaddingLeft: Float=0f, dpPaddingRight: Float=0f,isDrawLastItem:Boolean=false) {
        this.mColorDrawable = ColorDrawable(ContextCompat.getColor(VideoEditApplication.getAppContext(), color))
        this.mHeight = DisplayUtils.dp2px(VideoEditApplication.getAppContext(),dpHeight)
        this.mPaddingLeft = DisplayUtils.dp2px(VideoEditApplication.getAppContext(),dpPaddingLeft)
        this.mPaddingRight = DisplayUtils.dp2px(VideoEditApplication.getAppContext(),dpPaddingRight)
        this.mDrawLastItem = isDrawLastItem
    }

    fun setDrawHeaderFooter(mDrawHeaderFooter: Boolean) {
        this.mDrawHeaderFooter = mDrawHeaderFooter
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        var orientation = 0
        val headerCount = 0
        val footerCount = 0


        when (val layoutManager = parent.layoutManager) {
            is StaggeredGridLayoutManager -> orientation = layoutManager.orientation
            is GridLayoutManager -> orientation = layoutManager.orientation
            is LinearLayoutManager -> orientation = layoutManager.orientation
        }

        if (position >= headerCount && position < parent.adapter!!.itemCount - footerCount || mDrawHeaderFooter) {
            if (orientation == OrientationHelper.VERTICAL) {
                outRect.bottom = mHeight
            } else {
                outRect.right = mHeight
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        if (parent.adapter == null) {
            return
        }

        var orientation = 0
        val headerCount = 0

        val  dataCount = parent.adapter!!.itemCount
        val dataEndPosition = headerCount + dataCount


        //数据项除了最后一项
        //数据项最后一项
        //header&footer且可绘制
        when (val layoutManager = parent.layoutManager) {
            is StaggeredGridLayoutManager -> orientation = layoutManager.orientation
            is GridLayoutManager -> orientation = layoutManager.orientation
            is LinearLayoutManager -> orientation = layoutManager.orientation
        }
        val start: Int
        val end: Int
        if (orientation == OrientationHelper.VERTICAL) {
            start = parent.paddingLeft + mPaddingLeft
            end = parent.width - parent.paddingRight - mPaddingRight
        } else {
            start = parent.paddingTop + mPaddingLeft
            end = parent.height - parent.paddingBottom - mPaddingRight
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position >= headerCount && position < dataEndPosition - 1//数据项除了最后一项

                    || position == dataEndPosition - 1 && mDrawLastItem//数据项最后一项

                    || position !in headerCount until dataEndPosition && mDrawHeaderFooter//header&footer且可绘制
            ) {

                if (orientation == OrientationHelper.VERTICAL) {
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + mHeight
                    mColorDrawable!!.setBounds(start, top, end, bottom)
                    mColorDrawable!!.draw(c)
                } else {
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val left = child.right + params.rightMargin
                    val right = left + mHeight
                    mColorDrawable!!.setBounds(left, start, right, end)
                    mColorDrawable!!.draw(c)
                }
            }
        }
    }
}