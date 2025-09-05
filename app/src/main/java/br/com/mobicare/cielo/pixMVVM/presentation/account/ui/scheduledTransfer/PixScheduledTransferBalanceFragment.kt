package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.utils.flowCollectLatest
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixScheduledTransferBalanceBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferBalanceUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixScheduledTransferBalanceViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixScheduledTransferBalanceFragment : PixAccountBaseFragment() {

    private val receiptMethodViewModel: PixReceiptMethodViewModel by sharedViewModel()
    private val scheduledBalanceViewModel: PixScheduledTransferBalanceViewModel by viewModel()

    private var _binding: FragmentPixScheduledTransferBalanceBinding? = null
    private val binding get() = requireNotNull(_binding)

    override val toolbarTitle get() = getString(R.string.pix_account_scheduled_transfer_balance_title)

    override val footerButtonConfigurator get() = FooterButtonConfigurator(
        text = getString(R.string.pix_account_request_transfer),
        isEnabled = false,
        onTap = ::onRequestTransferTap
    )

    private val onBoardingFulfillment
        get() = receiptMethodViewModel.onBoardingFulfillment

    private val documentType
        get() = onBoardingFulfillment?.documentType?.name ?: getString(R.string.document)

    private val documentNumber
        get() = onBoardingFulfillment?.document ?: Text.SIMPLE_LINE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixScheduledTransferBalanceBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDescription()
        setupAlert()
        setupAgreement()
        setupScheduledBalanceObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onRequestTransferTap() {
        getToken { token ->
            scheduledBalanceViewModel.requestTransfer(token)
        }
    }

    private fun setupScheduledBalanceObserver() {
        flowCollectLatest(scheduledBalanceViewModel.scheduledBalanceState) { state ->
            when (state) {
                is PixScheduledTransferBalanceUiState.Success -> showSuccessMessageScreen()
                is PixScheduledTransferBalanceUiState.InsufficientBalanceError -> showInsufficientBalanceErrorScreen()
                else -> showGenericErrorScreen(buttonTextRes = R.string.text_close)
            }
        }
    }

    private fun setupDescription() {
        binding.tvDescription.text =
            getString(R.string.pix_account_scheduled_transfer_balance_description, documentType)
    }

    private fun setupAlert() {
        binding.cciAlert.cardText = getString(
            R.string.pix_account_scheduled_transfer_balance_alert_message,
            documentType,
            documentNumber
        )
    }

    private fun setupAgreement() {
        binding.cbAgreement.apply {
            text = getString(R.string.pix_account_scheduled_transfer_balance_agreement)
            setOnCheckedChangeListener { isChecked ->
                footerButton.isButtonEnabled = isChecked
            }
        }
    }

    private fun showSuccessMessageScreen() {
        navigation?.hideAnimatedLoading()

        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess = object : HandlerValidationToken.CallbackAnimationSuccess {
                override fun onSuccess() {
                    showSuccessScreen(
                        illustration = R.drawable.img_35_transferencia_dinheiro_sucesso,
                        titleText = getString(R.string.pix_account_scheduled_transfer_balance_success_title),
                        messageText = getString(R.string.pix_account_scheduled_transfer_balance_success_message),
                        primaryButtonText = getString(R.string.text_close),
                        onPrimaryButtonClick = ::navigateToPixHome,
                        showCloseButton = true
                    )
                }
            }
        )
    }

    private fun showInsufficientBalanceErrorScreen() {
        handlerValidationToken.hideAnimation(
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {
                override fun onStop() {
                    showErrorScreen(
                        title = getString(R.string.pix_account_scheduled_transfer_balance_insufficient_error_title),
                        message = getString(R.string.pix_account_scheduled_transfer_balance_insufficient_error_message),
                        buttonText = getString(R.string.text_close),
                        onClose = {
                            it?.dismiss()
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        )
    }

}