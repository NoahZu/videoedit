package com.haoqi.teacher.videoedit.videoprocess

import VideoHandle.EpEditor
import VideoHandle.OnEditorListener

object EpEditorExt {

    /**
     * @param picPath 图片路径
     * @param outVideoPath 输出视频路径
     * @param duration 视频时长 单位 秒
     * @param onEditorListener 回调
     */
    fun onePic2Video(
        picPath: String,
        outVideoPath: String,
        duration: Int,
        width: Int,
        height: Int,
        onEditorListener: OnEditorListener
    ) {
        val cmd = String.format(
            "-loop 1 -i %s -c:v libx264 -t %d -pix_fmt yuv420p -vf scale=%d:%d %s",
            picPath,
            duration,
            width,
            height,
            outVideoPath
        )
        EpEditor.execCmd(cmd, duration.toLong() * 1000 * 1000, onEditorListener)
    }

    fun compressVideo() {

    }
}