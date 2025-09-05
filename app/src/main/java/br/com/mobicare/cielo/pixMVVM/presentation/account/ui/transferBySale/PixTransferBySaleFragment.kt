package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.transferBySale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixTransferBySaleBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixProfileUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixAccountChangeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixTransferBySaleFragment : PixAccountBaseFragment() {

    private val accountViewModel: PixReceiptMethodViewModel by sharedViewModel()
    private val accountChangeViewModel: PixAccountChangeViewModel by viewModel()

    private var _binding: FragmentPixTransferBySaleBinding? = null
    private val binding get() = requireNotNull(_binding)

    override val toolbarTitle get() = getString(R.string.pix_account_transfer_by_sale_title)

    override val footerButtonConfigurator get() = FooterButtonConfigurator(
        text = getString(R.string.pix_account_activate_modality),
        onTap = ::onActivateModalityTap,
        isEnabled = false
    )

    private val onBoardingFulfillment get() = accountViewModel.onBoardingFulfillment
    private val activeReceiptMethod get() = accountViewModel.activeReceiptMethod

    override fun onCreateView( 
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixTransferBySaleBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDocument()
        setupAgreementCheckBox()
        setupProfileObserver()
        setupScheduledTransferObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onActivateModalityTap() {
        getToken {
            when (activeReceiptMethod) {
                PixReceiptMethod.SCHEDULED_TRANSFER -> toggleScheduledTransfer(it)
                PixReceiptMethod.CIELO_ACCOUNT -> changeProfile(it)
                else -> showGenericErrorScreen()
            }
        }
    }

    private fun toggleScheduledTransfer(token: String) {
        accountChangeViewModel.toggleScheduledTransfer(
            token = token,
            enableScheduledTransfer = false,
        )
    }

    private fun changeProfile(token: String) {
        accountChangeViewModel.changeProfile(
            token = token,
            settlementActive = true,
        )
    }

    private fun setupProfileObserver() {
        accountChangeViewModel.profileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixProfileUiState.Success -> handleSuccessState()
                is PixProfileUiState.Error -> showGenericErrorScreen()
            }
        }
    }

    private fun setupScheduledTransferObserver() {
        accountChangeViewModel.scheduledTransferState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixScheduledTransferUiState.Success -> handleSuccessState()
                is PixScheduledTransferUiState.Error -> showGenericErrorScreen()
            }
        }
    }

    private fun handleSuccessState() {
        navigation?.hideAnimatedLoading()

        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
            object : HandlerValidationToken.CallbackAnimationSuccess {
                override fun onSuccess() {
                    showSuccessScreen(
                        illustration = R.drawable.img_14_estrelas,
                        titleText = getString(R.string.pix_account_success_title),
                        messageText = getString(R.string.pix_account_transfer_by_sale_success_message),
                        noteText = getString(
                            R.string.pix_account_success_note,
                            onBoardingFulfillment?.documentType,
                            onBoardingFulfillment?.document
                        ),
                        primaryButtonText = getString(R.string.text_close),
                        onPrimaryButtonClick = ::navigateToPixHome
                    )
                }
            }
        )
    }

    private fun setupDocument() {
        binding.tvDocument.text = getString(
            R.string.pix_account_transfer_by_sale_document,
            onBoardingFulfillment?.documentType?.name,
            onBoardingFulfillment?.document
        ).htmlTextFormat()
    }

    private fun setupAgreementCheckBox() {
        binding.apply {
            llAgreement.setOnClickListener {
                cbAgreement.isChecked = cbAgreement.isChecked.not()
                footerButton.isButtonEnabled = cbAgreement.isChecked
            }
        }
    }

}