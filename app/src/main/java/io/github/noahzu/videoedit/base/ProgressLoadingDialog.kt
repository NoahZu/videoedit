package io.github.noahzu.videoedit.base

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import io.github.noahzu.videoedit.R
import kotlinx.android.synthetic.main.dialog_progress_loading.view.*

class ProgressLoadingDialog : Dialog {
    private  lateinit var rootView : View
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    fun setProgress(progress : Float) {
        rootView.progressView.setProgress(progress)
    }


    class Builder(private val context: Context) {
        private var message: String? = null
        private var isCancelable: Boolean = false
        private var isCancelOutside: Boolean = false

        /**
         * 设置提示信息
         * @param message
         * @return
         */

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        /**
         * 设置是否可以按返回键取消
         * @param isCancelable
         * @return
         */

        fun setCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        /**
         * 设置是否可以取消
         * @param isCancelOutside
         * @return
         */
        fun setCancelOutside(isCancelOutside: Boolean): Builder {
            this.isCancelOutside = isCancelOutside
            return this
        }

        fun create(): ProgressLoadingDialog {

            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.dialog_progress_loading, null)
            val loadingDailog = ProgressLoadingDialog(context, R.style.LoadingDialogStyle)

            view.messageTextView.text = message
            loadingDailog.rootView = view
            loadingDailog.setContentView(view)
            loadingDailog.setCancelable(isCancelable)
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside)
            loadingDailog.window?.attributes?.width = DisplayUtils.dp2px(context,150f)
            loadingDailog.window?.attributes?.height = DisplayUtils.dp2px(context,100f)
            return loadingDailog

        }
    }
}