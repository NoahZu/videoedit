package io.github.noahzu.videoedit.utils

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver

class KeyboardChangeListener(contextObj: Activity) : ViewTreeObserver.OnGlobalLayoutListener {
    private val mContentView: View?
    private var mOriginHeight: Int = 0
    private var mPreHeight: Int = 0
    private var mKeyBoardListen: KeyBoardListener? = null

    interface KeyBoardListener {
        fun onKeyboardChange(isShow: Boolean, keyboardHeight: Int)
    }

    fun setKeyBoardListener(keyBoardListen: KeyBoardListener) {
        this.mKeyBoardListen = keyBoardListen
    }

    init {
        mContentView = findContentView(contextObj)
        addContentTreeObserver()
    }

    private fun findContentView(contextObj: Activity): View {
        return contextObj.findViewById(android.R.id.content)
    }

    private fun addContentTreeObserver() {
        mContentView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val currHeight = mContentView!!.height
        if (currHeight == 0) {
            return
        }
        var hasChange = false
        if (mPreHeight == 0) {
            mPreHeight = currHeight
            mOriginHeight = currHeight
        } else {
            if (mPreHeight != currHeight) {
                hasChange = true
                mPreHeight = currHeight
            } else {
                hasChange = false
            }
        }
        if (hasChange) {
            val isShow: Boolean
            var keyboardHeight = 0
            if (mOriginHeight == currHeight) {
                //hidden
                isShow = false
            } else {
                //show
                keyboardHeight = mOriginHeight - currHeight
                isShow = true
            }

            if (mKeyBoardListen != null) {
                mKeyBoardListen!!.onKeyboardChange(isShow, keyboardHeight)
            }
        }
    }

    fun destroy() {
        mContentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
    }
}
