package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_SECOND
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.databinding.FragmentPixRefundRouterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.presentation.refund.PixRefundNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixRequestRefundViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixRefundRouterFragment : BaseFragment() {

    private val viewModel: PixRequestRefundViewModel by sharedViewModel()

    private var navigation: CieloNavigation? = null

    private val navArgsData by lazy {
        navigation?.getData() as? PixRefundNavigationFlowActivity.NavArgs.Data
    }

    private val transferDetail by lazy { navArgsData?.transferDetail }

    private val reversalDeadlineDate get() = viewModel.transferDetail?.transactionReversalDeadline

    override fun onCreateView( 
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixRefundRouterBinding
        .inflate(inflater, container, false)
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeNavigation()
        setupObserver()
        getRefundReceipts()
    }

    private fun initializeNavigation() {
        navigation = requireActivity() as? CieloNavigation
    }

    private fun getRefundReceipts() {
        transferDetail?.let { detail ->
            viewModel.getRefundReceipts(detail)
        }.ifNull {
            showMessageScreen(
                onPrimaryButtonTap = { requireActivity().finish() }
            )
        }
    }

    private fun setupObserver() {
        viewModel.refundReceiptsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixRefundReceiptsUiState.Loading -> handleLoadingState()
                is PixRefundReceiptsUiState.Success -> handleSuccessState(state)
                is PixRefundReceiptsUiState.Error -> handleErrorState(state)
            }
        }
    }

    private fun handleLoadingState() {
        navigation?.showAnimatedLoading()
    }

    private fun handleSuccessState(state: PixRefundReceiptsUiState.Success) {
        navigation?.showContent(true)

        when (state) {
            is PixRefundReceiptsUiState.FullyRefunded -> navigateToFullyRefunded()
            is PixRefundReceiptsUiState.PartiallyRefunded -> navigateToPartiallyRefunded()
            is PixRefundReceiptsUiState.NotRefunded -> navigateToRefundAmount()
            is PixRefundReceiptsUiState.PartiallyRefundedButExpired -> navigateToRefundExpired()
            is PixRefundReceiptsUiState.NotRefundedButExpired -> showRefundDeadlineExpiredScreen()
            is PixRefundReceiptsUiState.Unknown -> showUnableToLoadAmountToRefundScreen()
        }
    }

    private fun handleErrorState(state: PixRefundReceiptsUiState.Error) {
        navigation?.showContent(true)

        when (state) {
            is PixRefundReceiptsUiState.ErrorWithExpiredRefund -> navigateToRefundExpired()
            is PixRefundReceiptsUiState.ErrorWithNotExpiredRefund -> showUnableToLoadAmountToRefundScreen()
        }
    }

    private fun showRefundDeadlineExpiredScreen() {
        showMessageScreen(
            title = getString(R.string.pix_refund_not_refunded_but_expired_error_title),
            message = getString(
                R.string.pix_refund_not_refunded_but_expired_error_message,
                reversalDeadlineDate?.parseToString(SIMPLE_HOUR_MINUTE_SECOND),
                reversalDeadlineDate?.parseToString(SIMPLE_DT_FORMAT_MASK)
            ),
            onPrimaryButtonTap = { requireActivity().finish() }
        )
    }

    private fun showUnableToLoadAmountToRefundScreen() {
        showMessageScreen(
            title = getString(R.string.pix_refund_not_expired_error_title),
            message = getString(R.string.pix_refund_not_expired_error_message),
            buttonText = getString(R.string.text_error_update),
            onPrimaryButtonTap = {
                it?.dismiss()
                getRefundReceipts()
            }
        )
    }

    private fun navigateToFullyRefunded() {
        navigate(
            PixRefundRouterFragmentDirections
                .actionPixRefundRouterFragmentToPixFullyRefundedFragment()
        )
    }

    private fun navigateToPartiallyRefunded() {
        navigate(
            PixRefundRouterFragmentDirections
                .actionPixRefundRouterFragmentToPixPartiallyRefundedFragment()
        )
    }

    private fun navigateToRefundExpired() {
        navigate(
            PixRefundRouterFragmentDirections
                .actionPixRefundRouterFragmentToPixRefundExpiredFragment()
        )
    }

    private fun navigateToRefundAmount() {
        navigate(
            PixRefundRouterFragmentDirections
                .actionPixRefundRouterFragmentToPixRefundAmountFragment()
        )
    }

    private fun navigate(route: NavDirections) {
        findNavController().safeNavigate(route)
    }

    private fun showMessageScreen(
        title: String = getString(R.string.commons_generic_error_title),
        message: String = getString(R.string.commons_generic_error_message),
        buttonText: String = getString(R.string.text_close),
        onPrimaryButtonTap: (Dialog?) -> Unit
    ) {
        doWhenResumed {
            navigation?.run {
                hideAnimatedLoading()
                showHandlerViewV2(
                    title = title,
                    message = message,
                    illustration = R.drawable.ic_07,
                    isShowBackButton = false,
                    isShowIconButtonEndHeader = true,
                    labelPrimaryButton = buttonText,
                    onPrimaryButtonClickListener = object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) { onPrimaryButtonTap(dialog) }
                    },
                    onBackButtonClickListener = onErrorCloseTap,
                    onIconButtonEndHeaderClickListener = onErrorCloseTap
                )
            }
        }
    }

    private val onErrorCloseTap get() = object : HandlerViewBuilderFluiV2.HandlerViewListener {
        override fun onClick(dialog: Dialog?) { requireActivity().finish() }
    }

}