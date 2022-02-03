package com.lefffo.oxyengynar1.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.lefffo.oxyengynar1.databinding.ViewSwipeLeftRightBinding

class SwipeLeftRightView(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private var animator: ViewPropertyAnimator? = null

    init {
        ViewSwipeLeftRightBinding.inflate(LayoutInflater.from(context), this, true)

        doOnLayout {
            translationX = -width.toFloat()
            animateIn()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun animateIn() {
        animator = animate()
            .translationX(0f)
            .setStartDelay(2_000)
            .setDuration(1_000)
            .withEndAction {
                animateOut()
            }
            .apply { start() }
    }

    private fun animateOut() {
        animator = animate()
            .translationX(-width.toFloat())
            .setStartDelay(2_000)
            .setDuration(1_000)
            .withEndAction {
                animateOut()
            }
            .apply { start() }
    }
}