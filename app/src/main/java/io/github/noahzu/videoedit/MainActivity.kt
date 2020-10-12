package io.github.noahzu.videoedit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yanzhenjie.album.Album

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        Album.video(this)
            .multipleChoice()
            .columnCount(4)
            .camera(false)
            .widget(ViewConfigFactory.createAlbumWidgetVideo())
            .onResult { result ->
                val videoArrayList = kotlin.collections.ArrayList<String>(result.size)
                result.forEach {
                    videoArrayList.add(it.path)
                }
                startActivity(Intent(this,VideoEditActivity::class.java).apply {
                    putStringArrayListExtra(VideoEditActivity.VIDEO_PATH,videoArrayList);
                })
            }.start()
    }
}