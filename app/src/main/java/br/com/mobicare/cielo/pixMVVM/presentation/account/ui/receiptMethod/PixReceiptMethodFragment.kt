package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.FragmentPixReceiptMethodBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.factories.PixReceiptMethodCardListViewFactory
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.rules.ShouldEmptyBalanceRule
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixReceiptMethodUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixReceiptMethodFragment : PixAccountBaseFragment(), CieloNavigationListener {
    private val viewModel: PixReceiptMethodViewModel by sharedViewModel()

    private var _binding: FragmentPixReceiptMethodBinding? = null
    private val binding get() = requireNotNull(_binding)

    override val toolbarTitle get() = getString(R.string.pix_account_receipt_method_title)

    private val currentBalance by lazy {
        (navigation?.getData() as? PixAccountNavigationFlowActivity.NavArgs.Data)?.currentBalance
    }

    private val shouldEmptyBalanceRule by lazy { ShouldEmptyBalanceRule(currentBalance) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixReceiptMethodBinding.inflate(inflater, container, false).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        getOnBoardingFulfillment()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixReceiptMethodUiState.Loading -> handleLoadingState()
                is PixReceiptMethodUiState.Success -> handleSuccessState()
                is PixReceiptMethodUiState.Error -> handleErrorState()
            }
        }
    }

    private fun getOnBoardingFulfillment() {
        if (viewModel.isSuccessState) {
            handleSuccessState()
        } else {
            viewModel.getOnBoardingFulfillment()
        }
    }

    private fun handleLoadingState() {
        navigation?.showAnimatedLoading()
    }

    private fun handleSuccessState() {
        navigation?.run {
            setupDescription(viewModel.onBoardingFulfillment)
            setupDisplayingInformation(viewModel.onBoardingFulfillment)

            PixReceiptMethodCardListViewFactory(
                context = requireContext(),
                layoutInflater = layoutInflater,
                containerView = binding.llReceiptMethod,
                onCieloAccountTap = ::onCieloAccountTap,
                onTransferBySaleTap = ::onTransferBySaleTap,
                onScheduledTransferTap = ::onScheduledTransferTap,
            ).create(
                viewModel.activeReceiptMethod,
                viewModel.ftScheduledTransferEnable,
                viewModel.onBoardingFulfillment?.settlementScheduled?.list,
            )

            showContent()
        }
    }

    private fun setupDescription(onBoardingFulfillment: OnBoardingFulfillment?) {
        binding.tvDescription.text =
            getString(
                R.string.pix_account_receipt_method_description,
                onBoardingFulfillment?.documentType?.name,
            )
    }

    private fun setupDisplayingInformation(onBoardingFulfillment: OnBoardingFulfillment?) {
        binding.tvDisplayingInformation.apply {
            text =
                getString(
                    R.string.pix_account_receipt_method_document,
                    onBoardingFulfillment?.documentType?.name,
                    onBoardingFulfillment?.document,
                )
            setCustomDrawable {
                solidColor = R.color.cloud_100
                radius = R.dimen.dimen_12dp
            }
        }
    }

    private fun onCieloAccountTap(activeReceiptMethod: PixReceiptMethod) {
        findNavController().safeNavigate(
            PixReceiptMethodFragmentDirections
                .actionPixReceiptMethodFragmentToPixCieloAccountFragment(),
        )
    }

    private fun onTransferBySaleTap(activeReceiptMethod: PixReceiptMethod) {
        if (shouldEmptyBalanceRule(activeReceiptMethod)) {
            showEmptyBalanceBeforeProceedingMessage()
            return
        }

        findNavController().safeNavigate(
            PixReceiptMethodFragmentDirections
                .actionPixReceiptMethodFragmentToPixTransferBySaleFragment(),
        )
    }

    private fun onScheduledTransferTap(activeReceiptMethod: PixReceiptMethod) {
        if (shouldEmptyBalanceRule(activeReceiptMethod)) {
            showEmptyBalanceBeforeProceedingMessage()
            return
        }

        findNavController().safeNavigate(
            if (activeReceiptMethod == PixReceiptMethod.SCHEDULED_TRANSFER) {
                PixReceiptMethodFragmentDirections
                    .actionPixReceiptMethodFragmentToPixScheduledTransferEditFragment()
            } else {
                PixReceiptMethodFragmentDirections
                    .actionPixReceiptMethodFragmentToPixScheduledTransferIntroFragment()
            },
        )
    }

    private fun showEmptyBalanceBeforeProceedingMessage() {
        CieloMessageBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.pix_account_receipt_method_empty_balance_title),
                    showCloseButton = true,
                ),
            message =
                CieloMessageBottomSheet.Message(
                    text = getString(R.string.pix_account_receipt_method_empty_balance_description),
                ),
        ).show(childFragmentManager, EMPTY)
    }

    private fun handleErrorState() {
        navigation?.showHandlerViewV2(
            title = getString(R.string.commons_generic_error_title),
            message = getString(R.string.commons_generic_error_message),
            illustration = R.drawable.ic_07,
            isShowBackButton = false,
            isShowIconButtonEndHeader = false,
            labelPrimaryButton = getString(R.string.text_close),
            onPrimaryButtonClickListener = onErrorCloseTap,
            onBackButtonClickListener = onErrorCloseTap,
            onIconButtonEndHeaderClickListener = onErrorCloseTap,
        )
    }

    private val onErrorCloseTap get() =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) = requireActivity().finish()
        }
}
