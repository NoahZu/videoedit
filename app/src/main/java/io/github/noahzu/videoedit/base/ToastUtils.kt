package io.github.noahzu.videoedit.base

import android.app.Activity
import android.widget.Toast
import io.github.noahzu.videoedit.VideoEditApplication

object ToastUtils {
    fun showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(VideoEditApplication.getAppContext(), msg, length).show()
    }

    fun showToast(msgId: Int, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(VideoEditApplication.getAppContext(), msgId, length).show()
    }

    fun showLongToast(msgId: Int) {
        Toast.makeText(VideoEditApplication.getAppContext(), msgId, Toast.LENGTH_LONG).show()
    }

    fun showLongToast(msg: String) {
        Toast.makeText(VideoEditApplication.getAppContext(), msg, Toast.LENGTH_LONG).show()
    }
}