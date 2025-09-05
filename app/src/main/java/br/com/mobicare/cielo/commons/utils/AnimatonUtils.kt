package br.com.mobicare.cielo.commons.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

fun View.collapse() {
    val view = this

    val initialHeight = view.measuredHeight

    val animationObj = object : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                view.setVisibility(View.GONE)
            } else {
                view.getLayoutParams().height = initialHeight - (initialHeight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }

    }

    // 1dp/ms
    animationObj.duration = (initialHeight / view.context.resources.displayMetrics.density).toInt().toLong()
    view.startAnimation(animationObj)

}


fun View.expand() {
    val view = this

    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = view.measuredHeight
    view.getLayoutParams().height = 1
    view.setVisibility(View.VISIBLE)

    val animationObj = object : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            view.getLayoutParams().height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            view.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }

    }

    // 1dp/ms
    animationObj.duration = (targetHeight / view.context.resources.displayMetrics.density).toInt().toLong()
    view.startAnimation(animationObj)

}