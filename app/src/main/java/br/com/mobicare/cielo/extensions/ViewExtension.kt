package br.com.mobicare.cielo.extensions

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE_HUNDRED
import br.com.mobicare.cielo.commons.constants.ZERO
import com.google.android.material.chip.ChipGroup

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.visible(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}

fun ConstraintSet?.visible(viewId: Int, isVisible: Boolean) {
    if (isVisible) visible(viewId) else gone(viewId)
}

fun ConstraintSet?.visible(viewId: Int) {
    this?.setVisibility(viewId, View.VISIBLE)
}

fun ConstraintSet?.invisible(viewId: Int) {
    this?.setVisibility(viewId, View.INVISIBLE)
}

fun ConstraintSet?.gone(viewId: Int) {
    this?.setVisibility(viewId, View.GONE)
}


fun View?.visibleOrInvisible(isVisible: Boolean) {
    if (isVisible) visible() else invisible()
}

fun View?.toRotationUp() {
    val rotateAnimation = RotateAnimation(
        -0.0f, 180.0f, Animation.RELATIVE_TO_SELF,
        0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    rotateAnimation.interpolator = DecelerateInterpolator()
    rotateAnimation.repeatCount = 0
    rotateAnimation.duration = 150
    rotateAnimation.fillAfter = true
    this?.startAnimation(rotateAnimation)
}

fun View?.toRotationDown() {
    val rotateAnimation = RotateAnimation(
        180.0f, 0.0f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    rotateAnimation.interpolator = DecelerateInterpolator()
    rotateAnimation.repeatCount = 0
    rotateAnimation.duration = 150
    rotateAnimation.fillAfter = true
    this?.startAnimation(rotateAnimation)
}

fun ViewGroup?.startTransition() {
    this?.let {
        TransitionManager.beginDelayedTransition(it, AutoTransition())
    }
}

inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > ZERO && measuredHeight > ZERO) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun View.expand(duration: Long = THREE_HUNDRED.toLong(), onAnimationEnd: (() -> Unit)? = null) {
    measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    val targetHeight: Int = measuredHeight

    layoutParams.height = ONE
    alpha = ZERO.toFloat()
    visible()

    val animation: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height = if (interpolatedTime == ONE.toFloat())
                LinearLayout.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()

            alpha = interpolatedTime
            requestLayout()
        }

        override fun willChangeBounds() = true
    }

    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            onAnimationEnd?.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {}
    })

    animation.duration = duration

    startAnimation(animation)
}

fun View.collapse(duration: Long = THREE_HUNDRED.toLong(), onAnimationEnd: (() -> Unit)? = null) {
    val initialHeight: Int = measuredHeight

    val animation: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == ONE.toFloat()) {
                gone()
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                alpha = ONE.toFloat() - interpolatedTime
                requestLayout()
            }
        }

        override fun willChangeBounds() = true
    }

    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            onAnimationEnd?.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {}
    })

    animation.duration = duration

    startAnimation(animation)
}

fun TextView.applyStrikeThru() {
    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.removeStrikeThru() {
    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

fun View.updateMargins(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom,
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }
}

fun View.replace(replacementView: View) {
    val parent = replacementView.parent as ViewGroup
    val replacementViewPosition = parent.indexOfChild(replacementView)
    parent.removeView(replacementView)
    parent.addView(this, replacementViewPosition)
}

fun View.isVisibleOnScreen(): Boolean {
    if (!isShown) {
        return false
    }
    val actualPosition = Rect()
    val isGlobalVisible = getGlobalVisibleRect(actualPosition)
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val screen = Rect(0, 0, screenWidth, screenHeight)
    return isGlobalVisible && Rect.intersects(actualPosition, screen)
}

fun View.fadeIn(durationInMillis: Long = THREE_HUNDRED.toLong()) {
    startAnimation(AlphaAnimation(ZERO.toFloat(), ONE.toFloat()).apply {
        duration = durationInMillis
        fillAfter = true

        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                this@fadeIn.visible()
            }

            override fun onAnimationEnd(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    })
}

fun View.fadeOut(durationInMillis: Long = THREE_HUNDRED.toLong()) {
    startAnimation(AlphaAnimation(ONE.toFloat(), ZERO.toFloat()).apply {
        duration = durationInMillis
        fillAfter = true

        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                this@fadeOut.gone()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    })
}

fun ChipGroup.setChildrenEnabled(enable: Boolean) {
    children.forEach { it.isEnabled = enable }
}