package br.com.mobicare.cielo.interactbannersoffers.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

class InteractLeaderboardBannerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private val defWidth = 328.0
    private val defHeightLeaderBoard = 88.0
    private val defHeightRectagle = 250.0
    private var factor = defHeightLeaderBoard / defWidth

    fun setImage(@DrawableRes idImageRes: Int, bannerType: InteractBannerType) {
        this.removeAllViews()
        val imageView = AppCompatImageView(this.context)
        factor = when (bannerType) {
            InteractBannerType.LEADERBOARD -> defHeightLeaderBoard / defWidth
            InteractBannerType.RECTANGLE -> defHeightRectagle / defWidth
        }

        post {
            val totalWidth = this.width
            val sizeHeight = totalWidth.toDouble() * factor
            this.layoutParams.height = sizeHeight.toInt()
            imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            imageView.setImageResource(idImageRes)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            this.addView(imageView)
            this.requestLayout()
        }
    }
}

enum class InteractBannerType {
    LEADERBOARD,
    RECTANGLE
}