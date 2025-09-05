package br.com.mobicare.cielo.openFinance.presentation.utils

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import com.facebook.shimmer.ShimmerFrameLayout

object VisibilitySharedData {

    fun showShimmerLoading(shimmer: ShimmerFrameLayout) = shimmer.visible()

    fun closeShimmerLoading(shimmer: ShimmerFrameLayout) = shimmer.gone()

    fun showList(recyclerView: RecyclerView) = recyclerView.visible()

    fun hideList(recyclerView: RecyclerView) = recyclerView.gone()

    fun showNoDataMessage(layout: LinearLayout) = layout.visible()

    fun hideNoDataMessage(layout: LinearLayout) = layout.gone()
}