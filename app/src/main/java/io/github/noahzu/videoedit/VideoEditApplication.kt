package io.github.noahzu.videoedit

import android.app.Application
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import java.util.*

class VideoEditApplication : Application() {

    companion object {
        fun getAppContext(): Context = instance.applicationContext

        lateinit var instance : VideoEditApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Album.initialize(
            AlbumConfig.newBuilder(this)
            .setAlbumLoader(object : AlbumLoader {
                override fun load(imageView: ImageView, albumFile: AlbumFile?) {
                    load(imageView, albumFile?.path)
                }

                override fun load(imageView: ImageView, url: String?) {
                    Glide.with(imageView.context)
                        .load(url)
                        .into(imageView)
                }
            })
            .setLocale(Locale.getDefault()).build())
    }
}