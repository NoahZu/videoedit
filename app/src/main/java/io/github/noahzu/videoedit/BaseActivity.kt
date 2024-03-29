package io.github.noahzu.videoedit

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initialize()
    }

    @LayoutRes
    abstract fun layoutId(): Int
    abstract fun initialize()
}