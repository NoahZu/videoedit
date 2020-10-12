package io.github.noahzu.videoedit.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes

abstract class BaseDialog(context: Context, @LayoutRes val layoutId: Int, @StyleRes val styleId: Int = 0) :
    Dialog(context, styleId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        initialize()
    }

    abstract fun initialize()
}