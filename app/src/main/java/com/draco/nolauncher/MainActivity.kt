package com.draco.nolauncher

import android.app.WallpaperManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
            window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
                windowInsets
            }

        val pagedOnTouchListener = PagedOnTouchListener(this, 3)
        pagedOnTouchListener.pagedGestureListener.pageIndex.observe(this) {
            if (window.decorView.applicationWindowToken == null)
                return@observe
            val wm = WallpaperManager.getInstance(this)
            wm.setWallpaperOffsets(
                window.decorView.applicationWindowToken,
                it!!,
                0f
            )
        }
        window.decorView.setOnTouchListener(pagedOnTouchListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val wm = WallpaperManager.getInstance(this)

        val token = window.decorView.applicationWindowToken
        val x = event.x
        val y = event.y

        val action = if (event.action == MotionEvent.ACTION_UP)
            WallpaperManager.COMMAND_TAP
        else
            WallpaperManager.COMMAND_SECONDARY_TAP

        wm.sendWallpaperCommand(
            token,
            action,
            x.roundToInt(),
            y.roundToInt(),
            0,
            null
        )

        return true
    }
}