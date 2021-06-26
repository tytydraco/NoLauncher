package com.draco.nolauncher

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class PagedGestureListener(private val pages: Int) : GestureDetector.SimpleOnGestureListener() {
    private val _pageIndex = MutableLiveData(0f)
    val pageIndex: LiveData<Float> = _pageIndex

    private var desiredPageIndex = 0

    private var animatorLock = AtomicBoolean(false)
    private lateinit var currentAnimator: ValueAnimator

    private fun smoothPageIndexTransition(from: Float, to: Float) {
        desiredPageIndex = to.toInt()
        if (animatorLock.get())
            currentAnimator.cancel()
        currentAnimator = ValueAnimator.ofFloat(from,to)
            .setDuration(200)
            .also {
                it.addUpdateListener { animator ->
                    _pageIndex.postValue(animator.animatedValue as Float)
                }
                it.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        animatorLock.set(true)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        animatorLock.set(false)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        animatorLock.set(false)
                    }

                    override fun onAnimationRepeat(animation: Animator?) {}
                })
                it.start()
            }
    }

    private fun onPageLeft() {
        if (desiredPageIndex <= 0)
            return
        smoothPageIndexTransition(
            desiredPageIndex.toFloat(),
            desiredPageIndex.toFloat() - 1
        )
    }

    private fun onPageRight() {
        if (desiredPageIndex >= pages - 1)
            return
        smoothPageIndexTransition(
            desiredPageIndex.toFloat(),
            desiredPageIndex.toFloat() + 1
        )
    }

    companion object {
        const val DISTANCE_THRESHOLD = 100
        const val VELOCITY_THRESHOLD = 100
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val dX = e2.x - e1.x
        val dY = e2.y - e1.y

        if (abs(dX) > abs(dY) &&
                abs(dX) >= DISTANCE_THRESHOLD &&
                abs(velocityX) >= VELOCITY_THRESHOLD) {
            if (dX > 0)
                onPageRight()
            else
                onPageLeft()
            return true
        }

        return super.onFling(e1, e2, velocityX, velocityY)
    }
}