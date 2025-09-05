package br.com.mobicare.cielo.commons.utils.recycler

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO

private const val DOT_RADIUS = 8f
private const val DOT_WIDTH = 8f
private const val DP_SIZE = 4f

class CircleIndicatorItemDecoration(
    private val context: Context,
    @ColorRes private val selectedColor: Int = R.color.brand_400,
    @ColorRes private val unselectedColor: Int = R.color.display_200,
    private val dotRadius: Float = DOT_RADIUS,
    private val dotWidth: Float = DOT_WIDTH,
    private val dpSize: Float = DP_SIZE,
) : ItemDecoration() {

    private var unselectedDot: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(context.resources, unselectedColor, null)
    }

    private val selectedDot = Paint().apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(context.resources, selectedColor, null)
    }

    private val dots = mutableListOf<Pair<Float, Float>>()
    private var currentSelectedDotIndex = ZERO
    private var indicatorInitialized = false

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        setupIndicators(parent)

        parent.adapter?.let {
            val visibleItem = (parent.layoutManager as? LinearLayoutManager
                ?: return).findFirstCompletelyVisibleItemPosition()

            if (visibleItem >= ZERO) currentSelectedDotIndex = visibleItem

            for (i in ZERO until it.itemCount) {
                drawDot(
                    canvas,
                    dots[i].first,
                    dots[i].second,
                    isSelected = currentSelectedDotIndex == i
                )
            }
        }
    }

    private fun drawDot(canvas: Canvas, x: Float, y: Float, isSelected: Boolean = false) {
        canvas.drawCircle(x, y, dotRadius, if (isSelected) selectedDot else unselectedDot)
    }

    private fun setupIndicators(recyclerView: RecyclerView) {
        recyclerView.adapter?.let {
            indicatorInitialized = true

            val indicatorTotalWidth = dotWidth
            val indicatorPositionX =
                (recyclerView.width - (indicatorTotalWidth * it.itemCount * DP_SIZE)) / TWO
            val indicatorPositionY = recyclerView.height - (TWO * dotWidth)

            for (i in ZERO until it.itemCount) dots.add((indicatorPositionX + i * indicatorTotalWidth * dpSize) to indicatorPositionY)
        }
    }
}