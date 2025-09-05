package br.com.mobicare.cielo.commons.helpers

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

class AnimationHelper {

    companion object {
        fun showViewByHeight(v: View?, size: Int) {
            v?.let { itView ->
                ValueAnimator.ofInt(0, size)?.let {
                    it.interpolator = DecelerateInterpolator(2F)
                    it.addUpdateListener { itValueAnimator ->
                        itView.layoutParams?.height = itValueAnimator.animatedValue as Int
                        itView.requestLayout()
                    }
                    it.addListener(object: Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationStart(animation: Animator) {
                            itView.visibility = View.VISIBLE
                        }
                    })
                    it.start()
                }
            }
        }

        fun hideViewByHeight(v: View?, size: Int) {
            v?.let { itView ->
                ValueAnimator.ofInt(size, 0)?.let {
                    it.interpolator = DecelerateInterpolator(2F)
                    it.addUpdateListener { itValueAnimator ->
                        itView.layoutParams?.height = itValueAnimator.animatedValue as Int
                        itView.requestLayout()
                    }
                    it.addListener(object: Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            itView.visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationStart(animation: Animator) {
                        }
                    })
                    it.start()
                }

            }
        }
    }

}