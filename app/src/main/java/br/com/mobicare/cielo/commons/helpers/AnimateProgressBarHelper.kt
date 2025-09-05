package br.com.mobicare.cielo.commons.helpers

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar


class AnimateProgressBarHelper {
    companion object {

        fun setProgressValue(pb: RoundCornerProgressBar,
                             value: Double,
                             max: Int,
                             onProgress: (totalRevenue: Int) -> Unit,
                             onFinish: () -> Unit) {

            if (pb is ProgressBar) {
                setLegacyAnimationProgressBar(pb, value, max, onProgress,  onFinish)
            } else {
                val va = ValueAnimator.ofFloat(0F, value.times(100).div(max)
                    .toFloat())
                pb.max = 100F
                va.duration = 1000
                va.interpolator = DecelerateInterpolator()
                va.addUpdateListener {
                    pb.progress = it.animatedValue as Float
                    onProgress((it.animatedValue as Float).toInt())
                }
                configureAnimatorListener(va, onFinish)
                va.start()
            }

        }


        fun setLegacyAnimationProgressBar(pb: ProgressBar,
                                          value: Double,
                                          max: Int,
                                          onProgress: (value: Int) -> Unit,
                                          onFinish: () -> Unit) {

            val va = ValueAnimator.ofFloat(0F, value.toFloat())
            pb.max = max
            va.duration = 1000
            va.interpolator = DecelerateInterpolator()
            va.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                pb.progress = animatedValue
                onProgress(animatedValue)
            }
            configureAnimatorListener(va, onFinish)
            va.start()
        }

        private fun configureAnimatorListener(
            va: ValueAnimator,
            onFinish: () -> Unit
        ) {
            va.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    onFinish()
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationStart(p0: Animator) {
                }

            })
        }

    }
}