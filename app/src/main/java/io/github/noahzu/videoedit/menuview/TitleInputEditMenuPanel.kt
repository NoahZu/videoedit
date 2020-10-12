package com.haoqi.teacher.videoedit.menuview

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import androidx.core.view.forEach
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.base.ToastUtils
import io.github.noahzu.videoedit.ext.adjustHeight
import io.github.noahzu.videoedit.ext.adjustWidth
import io.github.noahzu.videoedit.utils.FileUtils
import io.github.noahzu.videoedit.utils.KeyboardChangeListener
import io.github.noahzu.videoedit.utils.KeyboardUtil
import io.github.noahzu.videoedit.utils.VideoUtil
import io.github.noahzu.videoedit.widget.ColorRoundImageView
import kotlinx.android.synthetic.main.view_edit_video_title_input_menu.view.*
import java.io.File

class TitleInputEditMenuPanel : EditMenuPanel {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun layoutId(): Int = R.layout.view_edit_video_title_input_menu
//    private val progressDialog by lazy {
//        CustomLoadingDialog.Builder(context).setCancelOutside(false).setCancelable(false).setMessage("生成标题中...")
//            .create()
//    }
    private var timeCheckedBtn = timeRadio3
    private lateinit var keybardListener: KeyboardChangeListener

    override fun initialize() {
        changeHeight()
        initListener()
    }

    private fun changeHeight() {
        post {
            if (editingVideoModel.outputVideoWidth > editingVideoModel.outputVideoHeight) {
                val newHeight = titleCanvasLayout.width / editingVideoModel.outRatio()
                titleCanvasLayout.adjustHeight(newHeight.toInt())
            } else {
                val newWidth = titleCanvasLayout.height * editingVideoModel.outRatio()
                titleCanvasLayout.adjustWidth(newWidth.toInt())
            }
        }
    }

    private fun initListener() {
        val backColorListener = object : OnClickListener {
            private var currentTextBgBtn = backRadio2
            override fun onClick(v: View?) {
                if (v as ColorRoundImageView != currentTextBgBtn) {
                    currentTextBgBtn.setImageResource(0)
                    v.setImageResource(R.drawable.ic_checked)
                    currentTextBgBtn = v
                    titleCanvasLayout.setBackgroundColor(currentTextBgBtn.getFillColor())
                }
            }
        }
        val textColorListener = object : OnClickListener {
            private var currentTextColorBtn = colorRadio2
            override fun onClick(v: View?) {
                if (v as ColorRoundImageView != currentTextColorBtn) {
                    currentTextColorBtn.setImageResource(0)
                    v.setImageResource(R.drawable.ic_checked)
                    currentTextColorBtn = v
                    titleEdit.setTextColor(currentTextColorBtn.getFillColor())
                }
            }

        }
        backColorMenuLayout.forEach {
            it.setOnClickListener(backColorListener)
        }
        colorMenuLayout.forEach {
            it.setOnClickListener(textColorListener)
        }

        timeMenuLayout.setOnCheckedChangeListener { group, checkedId ->
            timeCheckedBtn.setTextColor(Color.BLACK)
            timeCheckedBtn = group.findViewById(checkedId) as RadioButton
            timeCheckedBtn.setTextColor(Color.WHITE)
        }
    }

    override fun startEditAction() {
        super.startEditAction()
        titleEdit.setText("")
    }

    override fun panelTitle(): Int = R.string.edit_insert_title


    override fun closeEditAction() {
        super.closeEditAction()

        editTitleBar.resetMainEditStatus()
        editTitleBar.removeClickEventInterceptor(this)
    }

    override fun onSaveEditAction() {
        KeyboardUtil.closeKeyboard(context as Activity)
        postDelayed({
            generateVideo()
        }, 200)
    }

    override fun onCancelEditAction() {
        KeyboardUtil.closeKeyboard(context as Activity)
        closeEditAction()
        videoMenuControlManager.onEndTitleInput(true)
    }

    private fun generateVideo() {
//        progressDialog.show()
        val bitmap = Bitmap.createBitmap(titleCanvasLayout.width, titleCanvasLayout.height, Bitmap.Config.RGB_565)
        titleCanvasLayout.draw(Canvas(bitmap))
        val picPath = FileUtils.saveBitmapToLocal(bitmap)
        videoEditControlManger.startImage2Video(
            picPath,
            getSelectDuration(),
            object : VideoUtil.EditCallback {
                override fun onProgress(progress: Float) = Unit

                override fun onFail(error: String) {
                    ToastUtils.showToast("生成失败")
                }

                override fun onSuccess(file: File?) {
                    post {
//                        progressDialog.dismiss()
                        closeEditAction()
                        videoMenuControlManager.onEndTitleInput(false, file)
                    }
                }

            })
    }

    private fun getSelectDuration(): Int {
        return when (timeCheckedBtn.id) {
            R.id.timeRadio1 -> 1
            R.id.timeRadio3 -> 3
            R.id.timeRadio5 -> 5
            else -> 3
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        keybardListener = KeyboardChangeListener(context as Activity)
        keybardListener.setKeyBoardListener(object :
            KeyboardChangeListener.KeyBoardListener {
            override fun onKeyboardChange(isShow: Boolean, keyboardHeight: Int) {
                if (isShow) {
                    titleEdit.requestFocus()
                } else {
                    titleEdit.clearFocus()
                }
            }
        })
    }

    override fun destroyPanel() {
        super.destroyPanel()
        keybardListener.destroy()
    }

}