package io.github.noahzu.videoedit

import android.app.Activity
import android.graphics.Color
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.ImageSingleWrapper
import com.yanzhenjie.album.api.widget.Widget

object ViewConfigFactory {
    fun  createAlbumWidgetAlbum():Widget{
        return Widget.newLightBuilder(VideoEditApplication.getAppContext())
            .title(VideoEditApplication.getAppContext().getString(R.string.item_pick_album))
            .statusBarColor(Color.WHITE)
            .toolBarColor(Color.WHITE).build()
    }

    fun  createAlbumWidgetVideo():Widget{
        return Widget.newLightBuilder(VideoEditApplication.getAppContext())
            .title(VideoEditApplication.getAppContext().getString(R.string.video))
            .statusBarColor(Color.WHITE)
            .toolBarColor(Color.WHITE).build()
    }


    fun  createSystemImageSelect(activity: Activity): ImageSingleWrapper {
       return  Album.image(activity)
            .singleChoice()
            .columnCount(4)
            .camera(true)
            .widget(ViewConfigFactory.createAlbumWidgetAlbum())
    }
}