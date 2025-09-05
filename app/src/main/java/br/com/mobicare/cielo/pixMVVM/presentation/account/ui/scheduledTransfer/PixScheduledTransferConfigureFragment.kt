package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.joinWithLastCustomSeparator
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixScheduledTransferConfigureBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer.adapters.PixScheduledTransferHoursAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixAccountChangeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixScheduledTransferConfigureFragment : PixAccountBaseFragment() {

    private val accountViewModel: PixReceiptMethodViewModel by sharedViewModel()
    private val accountChangeViewModel: PixAccountChangeViewModel by viewModel()

    private var _binding: FragmentPixScheduledTransferConfigureBinding? = null
    private val binding get() = requireNotNull(_binding)

    override val toolbarTitle get() = getString(R.string.pix_account_scheduled_transfer_configure_title)

    override val footerButtonConfigurator get() = FooterButtonConfigurator(
        text = getString(
            if (isEditing) R.string.pix_account_save_editions else R.string.pix_account_activate_modality
        ),
        isEnabled = false,
        onTap = ::onSaveModalityTap
    )

    private val adapter = PixScheduledTransferHoursAdapter()

    private val isEditing get() = accountViewModel.activeReceiptMethod == PixReceiptMethod.SCHEDULED_TRANSFER
    private val onBoardingFulfillment get() = accountViewModel.onBoardingFulfillment
    private val scheduledList get() = onBoardingFulfillment?.settlementScheduled?.list
    private val checkedHours get() = adapter.getCheckedHours()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixScheduledTransferConfigureBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDescription()
        setupDisplayingInformation()
        setupHoursRecyclerView()
        setupAgreement()
        setupObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onSaveModalityTap() {
        if (checkedHours.isNotEmpty()) {
            getToken {
                if (isEditing) updateScheduledTransfer(it) else toggleScheduledTransfer(it)
            }
        }
    }

    private fun toggleScheduledTransfer(token: String) {
        accountChangeViewModel.toggleScheduledTransfer(
            token = token,
            enableScheduledTransfer = true,
            scheduledHours = checkedHours
        )
    }

    private fun updateScheduledTransfer(token: String) {
        accountChangeViewModel.updateScheduledTransfer(
            token = token,
            scheduledHours = checkedHours
        )
    }

    private fun setupObserver() {
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
                        titleText = getString(
                            if (isEditing) {
                                R.string.pix_account_scheduled_transfer_edit_success_title
                            } else {
                                R.string.pix_account_success_title
                            }
                        ),
                        messageText = getString(
                            R.string.pix_account_scheduled_transfer_configure_success_message,
                            checkedHours.joinWithLastCustomSeparator()
                        ),
                        noteText = getString(
                            R.string.pix_account_success_note,
                            onBoardingFulfillment?.documentType?.name,
                            onBoardingFulfillment?.document
                        ),
                        primaryButtonText = getString(R.string.text_close),
                        onPrimaryButtonClick = ::navigateToPixHome
                    )
                }
            }
        )
    }

    private fun setupHoursRecyclerView() {
        binding.rvHours.adapter = adapter
        scheduledList?.let { adapter.setCheckedHours(it) }
    }

    private fun setupDescription() {
        binding.tvDescription2.text = getString(
            R.string.pix_account_scheduled_transfer_configure_description_2,
            onBoardingFulfillment?.documentType?.name
        )
    }

    private fun setupDisplayingInformation() {
        binding.tvDisplayingInformation.apply {
            text = getString(
                R.string.pix_account_scheduled_transfer_configure_document,
                onBoardingFulfillment?.documentType,
                onBoardingFulfillment?.document
            ).htmlTextFormat()
            setCustomDrawable {
                solidColor = R.color.cloud_100
                radius = R.dimen.dimen_12dp
            }
        }
    }

    private fun setupAgreement() {
        binding.apply {
            llAgreement.setOnClickListener {
                cbAgreement.isChecked = cbAgreement.isChecked.not()
                footerButton.isButtonEnabled = cbAgreement.isChecked
            }
        }
    }

}