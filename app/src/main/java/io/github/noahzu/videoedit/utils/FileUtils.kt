package io.github.noahzu.videoedit.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import io.github.noahzu.videoedit.base.ToastUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    fun saveBitmapToLocal(bm: Bitmap): File? {
        val f = File(getExternalStorageParentFile().absolutePath, "${System.currentTimeMillis()}.png")
        if (f.exists()) {
            f.delete()
        }
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(f)
            //this compress will not do anything,PNG is lossless
            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            out?.close()
        }
        return f
    }

    fun getExternalStorageParentFile(): File {
        val parent = Environment.getExternalStorageDirectory()
        val file = File(parent.absolutePath + "/51Teacher")
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

    /**
     * 视频存到相册
     * isRetainOriginalVideo 是否保留原视频
     */
    fun saveVideoToGallery(context: Context, file: File, isShowFail:Boolean =  true, isRetainOriginalVideo:Boolean=false): File? {
        val metadata = VideoUtil.getSimpleMetadata(context, Uri.fromFile(file))
        if (metadata != null) {
            val fileName=file.name.replace(".mp4","")
            val newFileName="$fileName${System.currentTimeMillis()}.mp4"

            val galleryDir = File(Environment.getExternalStorageDirectory(), "DCIM/Camera/$newFileName")
            if(isRetainOriginalVideo){
                file.copyTo(galleryDir)
            }else{
                file.renameTo(galleryDir)
            }
            val createTime = System.currentTimeMillis()
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.TITLE, galleryDir.getName())
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, galleryDir.getName())
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, createTime)
            values.put(MediaStore.MediaColumns.DATE_ADDED, createTime)
            values.put(MediaStore.MediaColumns.DATA, galleryDir.getAbsolutePath())
            values.put(MediaStore.MediaColumns.SIZE, galleryDir.length())
            values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime)
            values.put(MediaStore.Video.VideoColumns.DURATION, metadata.duration)
            values.put(MediaStore.Video.VideoColumns.WIDTH, metadata.width)
            values.put(MediaStore.Video.VideoColumns.HEIGHT, metadata.height)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            return galleryDir
        } else {
            if(isShowFail){
                ToastUtils.showToast("保存文件出现错误")
            }
            return null
        }
    }

    public fun getSuperCoachingDir(): File {
        val file = File(Environment.getExternalStorageDirectory() ,"videoEdit")
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }
    fun getVideoEditTempDir(): File {
        val parent = getSuperCoachingDir()
        val file = File(parent.absolutePath + "/video_edit_temp")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }
}