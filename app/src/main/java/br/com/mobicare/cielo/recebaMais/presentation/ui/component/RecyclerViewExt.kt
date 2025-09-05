package br.com.mobicare.cielo.recebaMais.presentation.ui.component

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper


/**
 * @author Enzo Teles 22/05/2019
 * */
fun RecyclerView.attachSnapHelperWithListener(
        snapHelper: SnapHelper,
        behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
        onSnapPositionChangeListener: OnSnapPositionChangeListener?,
        callback: OnPickerSelectionCallback
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener, callback)
    addOnScrollListener(snapOnScrollListener)
}