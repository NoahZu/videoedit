package io.github.noahzu.videoedit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.album.Album
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectVideoBtn.setOnClickListener {
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
}