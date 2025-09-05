package br.com.mobicare.cielo.chargeback.presentation.details.helper

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.ChargebackLifecycleAdapter
import br.com.mobicare.cielo.extensions.*
import com.facebook.shimmer.ShimmerFrameLayout

class ChargebackLifecycleStateController(
    private val containerView: ViewGroup,
    private val currentLifecycleView: ViewGroup
) {
    private val loadingView = containerView.findViewById<ShimmerFrameLayout>(R.id.shimmerLifecycleIndicator)
    private val errorView = containerView.findViewById<ViewGroup>(R.id.containerLifecycleError)
    private val listView = containerView.findViewById<RecyclerView>(R.id.rvLifecycle)
    private val buttonReload = errorView.findViewById<TextView>(R.id.tvLifecycleReload)
    private val arrowIndicator = currentLifecycleView.findViewById<ImageView>(R.id.ivLifecycleArrowIndicator)

    private var onReload: (() -> Unit)? = null
    private var hasData = false
    private var isArrowDown = true

    init {
        currentLifecycleView.setOnClickListener(::onCurrentLifecycleViewClicked)
        buttonReload.setOnClickListener(::onReloadButtonClicked)
        hideContainer()
    }

    private val isContainerVisible get() = containerView.isVisible
    private val isLoadingIndicatorVisible get() = loadingView.isVisible

    fun setOnReloadClickListener(listener: () -> Unit) {
        onReload = listener
    }

    private fun expandContainer() {
        containerView.expand {
            rotateArrowUp()
        }
    }

    private fun hideContainer() {
        listView.gone()
        errorView.gone()
        loadingView.gone()
        containerView.gone()
    }

    fun collapseContainer() {
        containerView.collapse {
            listView.gone()
            errorView.gone()
            loadingView.gone()
            rotateArrowDown()
        }
    }

    fun showLoadingIndicator() {
        listView.gone()
        errorView.gone()
        loadingView.visible()
        expandContainer()
    }

    fun showError() {
        listView.gone()
        loadingView.gone()
        errorView.visible()
        expandContainer()
    }

    fun showList(lifecycleList: List<Lifecycle>? = null) {
        loadingView.gone()
        errorView.gone()
        listView.apply {
            if (lifecycleList != null) {
                adapter = ChargebackLifecycleAdapter(lifecycleList)
                hasData = true
                visible()
                afterMeasured { expandContainer() }
            } else {
                visible()
                expandContainer()
            }
        }
    }

    private fun onReloadButtonClicked(view: View) {
        onReload?.invoke()
    }

    private fun onCurrentLifecycleViewClicked(view: View) {
        if (isLoadingIndicatorVisible)
            return
        else if (isContainerVisible)
            collapseContainer()
        else if (hasData)
            showList()
        else
            onReload?.invoke()
    }

    private fun rotateArrowUp() {
        if (isArrowDown) {
            arrowIndicator.toRotationUp()
            isArrowDown = false
        }
    }

    private fun rotateArrowDown() {
        if (isArrowDown.not())  {
            arrowIndicator.toRotationDown()
            isArrowDown = true
        }
    }
}

