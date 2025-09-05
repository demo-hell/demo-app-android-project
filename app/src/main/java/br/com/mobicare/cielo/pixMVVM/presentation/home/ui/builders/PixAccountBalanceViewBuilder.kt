package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.builders

import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.extensions.fadeIn
import br.com.mobicare.cielo.extensions.fadeOut
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.ViewModelResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.AccountBalanceUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixAccountBalanceViewModel

class PixAccountBalanceViewBuilder(
    view: View,
    private val viewModel: PixAccountBalanceViewModel,
    private val onShowExtractClick: ((View) -> Unit)? = null
) : ViewModelResultHandler<AccountBalanceUiState> {

    private val btnReload = view.findViewById<TextView>(R.id.btnReload)
    private val btnBalanceVisibility = view.findViewById<ImageButton>(R.id.btnBalanceVisibility)
    private val btnAccessExtractPix: TextView? = view.findViewById(R.id.btnAccessExtractPix)
    private val shimmerLoading = view.findViewById<ViewGroup>(R.id.shimmerLoading)
    private val content = view.findViewById<ViewGroup>(R.id.content)
    private val error = view.findViewById<ViewGroup>(R.id.error)
    private val tvBalanceAmount = view.findViewById<TextView>(R.id.tvBalanceAmount)
    private val tvBalanceUpdatedAt = view.findViewById<TextView>(R.id.tvBalanceUpdatedAt)
    private val tvBalanceAmountHidden = view.findViewById<TextView>(R.id.tvBalanceAmountHidden)

    private val accountBalanceStore get() = viewModel.accountBalanceStore

    init {
        btnReload.setOnClickListener(::onTryAgainClick)
        btnBalanceVisibility.setOnClickListener(::onShowBalanceClick)
        btnAccessExtractPix?.setOnClickListener { onShowExtractClick?.invoke(it) }
    }

    override fun handleObservableResult(value: AccountBalanceUiState) {
        onAccountBalanceUIState(value)
    }

    override fun reloadSetup() {
        viewModel.accountBalanceUiState.value?.let { onAccountBalanceUIState(it) }
    }

    private fun onAccountBalanceUIState(value: AccountBalanceUiState) {
        when (value) {
            is AccountBalanceUiState.Loading -> showAccountBalanceLoading()
            is AccountBalanceUiState.Error -> showAccountBalanceError()
            is AccountBalanceUiState.Success -> configureAccountBalance()
        }
    }

    private fun onTryAgainClick(v: View) {
        viewModel.loadAccountBalance()
    }

    private fun onShowBalanceClick(v: View) {
        viewModel.toggleShowAccountBalanceValue()
        configureAccountBalanceVisibility()
    }

    private fun configureAccountBalance() {
        shimmerLoading.gone()
        error.gone()
        content.visible()

        tvBalanceAmount.text = accountBalanceStore.formattedBalance

        accountBalanceStore.updatedAt?.apply {
            tvBalanceUpdatedAt.apply{
                text = context.getString(
                    R.string.pix_home_balance_updated_at,
                    accountBalanceStore.formattedUpdatedDate,
                    accountBalanceStore.formattedUpdatedTime
                )
                visible()
            }
        }.ifNull {
            tvBalanceUpdatedAt.gone()
        }

        configureAccountBalanceVisibility()
    }

    private fun showAccountBalanceLoading() {
        content.gone()
        error.gone()
        shimmerLoading.visible()
    }

    private fun showAccountBalanceError() {
        shimmerLoading.gone()
        content.gone()
        error.visible()
    }

    private fun configureAccountBalanceVisibility() {
        viewModel.showAccountBalance.let { show ->
            btnBalanceVisibility.apply {
                setImageResource(
                    if (show) R.drawable.ic_interface_eye_on_24dp
                    else R.drawable.ic_interface_eye_off_24dp
                )
                setColorFilter(context.getColor(R.color.brand_400), PorterDuff.Mode.SRC_IN)
            }
            if (show) {
                tvBalanceAmountHidden.fadeOut(ZERO.toLong())
                tvBalanceAmount.fadeIn()
            } else {
                tvBalanceAmount.fadeOut(ZERO.toLong())
                tvBalanceAmountHidden.fadeIn()
            }
        }
    }

}