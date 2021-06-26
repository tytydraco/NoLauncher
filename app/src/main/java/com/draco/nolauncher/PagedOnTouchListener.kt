package com.draco.nolauncher

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class PagedOnTouchListener(context: Context, pages: Int) : View.OnTouchListener {
    val pagedGestureListener = PagedGestureListener(pages)
    private val gestureDetector = GestureDetector(context, pagedGestureListener)

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        return gestureDetector.onTouchEvent(event)
    }
}