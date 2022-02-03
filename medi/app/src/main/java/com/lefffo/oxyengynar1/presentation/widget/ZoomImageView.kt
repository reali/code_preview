package com.lefffo.oxyengynar1.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.ortiz.touchview.TouchImageView

@SuppressLint("ClickableViewAccessibility")
class ZoomImageView(context: Context, attrs: AttributeSet?) :
    TouchImageView(context, attrs) {

    init {
        setOnTouchListener { _, event ->
            var result = true
            if (event.pointerCount >= 2 || canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        parent.requestDisallowInterceptTouchEvent(true)
                        result = true
                    }
                    MotionEvent.ACTION_UP -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                        result = false
                    }
                }
            }
            result
        }
    }

    fun onClicked(onSingleClick: () -> Unit, onDoubleClick: () -> Unit) {
        setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                onSingleClick()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                onDoubleClick()
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent?): Boolean = false
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth == 0 || measuredHeight == 0) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }
}