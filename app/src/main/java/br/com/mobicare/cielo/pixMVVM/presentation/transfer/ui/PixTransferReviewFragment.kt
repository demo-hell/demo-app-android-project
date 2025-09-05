package br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.datePicker.CieloDatePicker
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.commons.utils.isToday
import br.com.mobicare.cielo.commons.utils.orSimpleLine
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixTransferReviewBinding
import br.com.mobicare.cielo.databinding.ItemPixTransferReviewDataBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.ifNullOrBlank
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixValidateKeyData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui.dialog.PixTransferMessageBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferUiState
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.util.Calendar

class PixTransferReviewFragment :
    BaseFragment(),
    AllowMeContract.View {
    private val viewModel: PixTransferViewModel by sharedViewModel()

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val handlerValidationToken: HandlerValidationToken by inject()

    private var _binding: FragmentPixTransferReviewBinding? = null
    val binding get() = _binding!!

    private var navigation: CieloNavigation? = null

    private val formattedAmount get() = viewModel.store.amount.toPtBrRealString()

    private val recurrenceData get() = viewModel.store.recurrenceData
    private val recurrenceIsSelected get() = viewModel.pixRecurrenceIsSelected

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixTransferReviewBinding
        .inflate(inflater, container, false)
        .apply {
            _binding = this
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupValues()
        setupTransferDataInfo()
        setupObserver()
        viewModel.getFeatureToggleRecurrence()
    }

    override fun onResume() {
        super.onResume()
        setupRecurrence()
        logScreenView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        navigation =
            (requireActivity() as? CieloNavigation)?.also {
                it.configureCollapsingToolbar(
                    CieloCollapsingToolbarLayout.Configurator(
                        toolbar =
                            CieloCollapsingToolbarLayout.Toolbar(
                                title = getString(R.string.pix_transfer_review_title),
                                menu =
                                    CieloCollapsingToolbarLayout.ToolbarMenu(
                                        menuRes = R.menu.menu_help,
                                        onOptionsItemSelected = ::onMenuOptionSelected,
                                    ),
                            ),
                    ),
                )
            }
    }

    private fun setupValues() {
        binding.apply {
            tbAmount.text = formattedAmount
            tbMessage.text = viewModel.store.message
                ?: getString(R.string.pix_transfer_review_button_message)
        }
    }

    private fun setupRecurrence() {
        binding.apply {
            switchRecurrence.apply {
                text = getString(R.string.pix_transfer_review_label_recurrence_button)
                isChecked = recurrenceIsSelected
            }

            tbDate.text =
                if (recurrenceIsSelected) {
                    formatStartDate(recurrenceData.startDate)
                } else {
                    formatStartDate(viewModel.store.schedulingDate ?: Calendar.getInstance())
                }

            tvLabelRecurrence.apply {
                val label = recurrenceData.period?.label?.let { getString(it) }
                text =
                    if (recurrenceData.endDate != null) {
                        val endDateFormatted = recurrenceData.endDate?.toString(SIMPLE_DT_FORMAT_MASK)
                        getString(R.string.pix_transfer_review_label_end_recurrence, label, endDateFormatted)
                    } else {
                        label
                    }
                visible(recurrenceIsSelected)
            }
        }
    }

    private fun setupTransferDataInfo() {
        viewModel.keyData
            ?.let {
                buildTransferDataItemView(
                    label = getString(R.string.pix_transfer_review_label_recipient),
                    value = it.ownerName.orSimpleLine(),
                )
                buildTransferDataItemView(
                    label = it.documentType.ifNullOrBlank(getString(R.string.pix_transfer_review_label_document)),
                    value = it.formattedDocumentNumber.orSimpleLine(),
                )
                if (it is PixValidateKeyData) {
                    buildTransferDataItemView(
                        label = getString(R.string.pix_transfer_review_label_key),
                        value = it.data.key.orSimpleLine(),
                    )
                }
                buildTransferDataItemView(
                    label = getString(R.string.pix_transfer_review_label_institution),
                    value = it.bankName.orSimpleLine(),
                )
                buildTransferDataItemView(
                    label = getString(R.string.pix_transfer_review_label_agency),
                    value = it.bankBranchNumber.orSimpleLine(),
                )
                buildTransferDataItemView(
                    label = it.bankAccountType?.let { res -> getString(res) } ?: getString(R.string.pix_transfer_review_label_account),
                    value = it.bankAccountNumber.orSimpleLine(),
                )
                buildTransferDataItemView(
                    label = getString(R.string.pix_transfer_review_label_transaction_type),
                    value = getString(R.string.pix_transfer_review_value_transaction_type),
                )
            }.ifNull {
                binding.llTransferData.gone()
            }
    }

    private fun setupListeners() {
        binding.apply {
            tbAmount.setOnClickListener(::onAmountTap)
            tbDate.setOnClickListener(::onDateTap)
            tbMessage.setOnClickListener(::onMessageTap)
            btnTransfer.setOnClickListener(::onTransferTap)
            switchRecurrence.setOnCheckedChangeListener(::onRecurrenceTap)
        }
    }

    private fun setupObserver() {
        setupObserverUISate()
        setupObserverRecurrenceEnabled()
    }

    private fun setupObserverUISate() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixTransferUiState.Success -> handleSuccessState(state)
                is PixTransferUiState.Error -> handleErrorState(state)
                is PixTransferUiState.DoNothing -> Unit
            }
        }
    }

    private fun setupObserverRecurrenceEnabled() {
        viewModel.ftRecurrenceEnabled.observe(viewLifecycleOwner) { isEnabled ->
            showRecurrenceButton(isEnabled)
        }
    }

    private fun showRecurrenceButton(isShow: Boolean) {
        binding.apply {
            dividerRecurrenceButton.visible(isShow)
            switchRecurrence.visible(isShow)
        }
    }

    private fun handleSuccessState(state: PixTransferUiState.Success) {
        when (state) {
            is PixTransferUiState.TransferSent -> showTransferSentMessageScreen()
            is PixTransferUiState.TransferScheduled -> showTransferScheduledMessageScreen()
        }
    }

    private fun handleErrorState(state: PixTransferUiState.Error) {
        when (state) {
            is PixTransferUiState.TokenError -> onTokenError(state.error)
            is PixTransferUiState.GenericError -> showGenericErrorScreen(state.error)
            is PixTransferUiState.TooManyRequestsError -> showTooManyManyRequestErrorScreen(state.error)
        }
    }

    private fun onAmountTap(view: View) {
        findNavController().popBackStack()
    }

    private fun onDateTap(view: View) {
        if (recurrenceIsSelected) {
            navigateToRecurrence()
        } else {
            showDatePickerScheduling()
        }
    }

    private fun showDatePickerScheduling()  {
        CieloDatePicker.show(
            title = R.string.pix_transfer_review_calendar_title,
            selectedDate = viewModel.store.schedulingDate,
            fragmentManager = childFragmentManager,
            tag = this@PixTransferReviewFragment.javaClass.simpleName,
            onDateSelected = ::onDateSelected,
        )
    }

    private fun onDateSelected(date: Calendar) {
        binding.tbDate.text = formatStartDate(date)
        viewModel.setSchedulingDate(date)
    }

    private fun formatStartDate(date: Calendar): String =
        if (date.isToday()) {
            getString(R.string.pix_transfer_review_value_date_today, date.toString(SIMPLE_DT_FORMAT_MASK))
        } else {
            date.toString(SIMPLE_DT_FORMAT_MASK)
        }

    private fun onRecurrenceTap(isChecked: Boolean) {
        if (isChecked) {
            navigateToRecurrence()
        } else {
            viewModel.selectPixRecurrence(false)
            setupRecurrence()
        }
    }

    private fun navigateToRecurrence() {
        findNavController().safeNavigate(
            PixTransferReviewFragmentDirections
                .actionPixTransferReviewFragmentToPixTransferRecurrenceFragment(),
        )
    }

    private fun onMessageTap(view: View) {
        PixTransferMessageBottomSheet(
            context = requireContext(),
            message = viewModel.store.message,
            onSaveMessage = {
                viewModel.setMessage(it)
                binding.tbMessage.text = it.ifBlank { getString(R.string.pix_transfer_review_button_message) }
            },
        ).show(childFragmentManager, EMPTY)
    }

    private fun onTransferTap(view: View) {
        startAllowMe()
    }

    private fun startAllowMe() {
        allowMePresenter.collect(
            context = requireActivity(),
            mAllowMeContextual = allowMePresenter.init(requireContext()),
            mandatory = true,
        )
    }

    override fun successCollectToken(result: String) {
        viewModel.setFingerPrint(result)
        setButtonTransferEnabled(false)
        getToken()
    }

    override fun errorCollectToken(
        result: String?,
        errorMessage: String,
        mandatory: Boolean,
    ) {
        setButtonTransferEnabled(true)

        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(errorMessage)
            .closeTextButton(getString(R.string.dialog_button))
            .build()
            .showAllowingStateLoss(childFragmentManager, getString(R.string.text_cieloalertdialog))
    }

    private fun getToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) = viewModel.requestTransfer(token)

                override fun onError() = onTokenError()
            },
        )
    }

    private fun onTokenError(error: NewErrorMessage? = null) {
        setButtonTransferEnabled(true)

        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = getToken()
            },
        )
    }

    private fun showTransferScheduledMessageScreen() {
        setButtonTransferEnabled(true)

        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess =
                object : HandlerValidationToken.CallbackAnimationSuccess {
                    override fun onSuccess() {
                        doWhenResumed {
                            showSuccessScreen(
                                titleText = getString(R.string.pix_transfer_review_success_scheduled_title),
                                messageText =
                                getString(
                                    R.string.pix_transfer_review_success_scheduled_message,
                                    formattedAmount,
                                    viewModel.keyData?.ownerName,
                                    viewModel.store.schedulingDate?.toString(SIMPLE_DT_FORMAT_MASK),
                                ),
                                primaryButtonText = getString(R.string.text_close),
                                onPrimaryButtonClick = ::onCloseTap,
                            )
                        }
                    }
                },
        )
    }

    private fun showTransferSentMessageScreen() {
        logTransferSuccess()
        setButtonTransferEnabled(true)

        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess =
                object : HandlerValidationToken.CallbackAnimationSuccess {
                    override fun onSuccess() {
                        doWhenResumed {
                            showSuccessScreen(
                                titleText = getString(R.string.pix_transfer_review_success_sent_title),
                                messageText =
                                getString(
                                    R.string.pix_transfer_review_success_sent_message,
                                    formattedAmount,
                                    viewModel.keyData?.ownerName,
                                ),
                                primaryButtonText = getString(R.string.text_close),
                                secondaryButtonText = getString(R.string.pix_transfer_review_success_sent_button_receipt),
                                onPrimaryButtonClick = ::onCloseTap,
                                onSecondaryButtonClick = ::onOpenReceiptTap,
                            )
                        }
                    }
                },
        )
    }

    private fun showTooManyManyRequestErrorScreen(error: NewErrorMessage) {
        logTooManyRequestsException(error)
        setButtonTransferEnabled(true)

        handlerValidationToken.hideAnimation(
            callbackStopAnimation =
                object : HandlerValidationToken.CallbackStopAnimation {
                    override fun onStop() {
                        doWhenResumed {
                            showErrorScreen(
                                title = getString(R.string.pix_transfer_review_error_too_many_requests_title),
                                message = getString(R.string.pix_transfer_review_error_too_many_requests_message),
                                buttonText = getString(R.string.text_close),
                                imageRes = R.drawable.ic_transfer_error_pix,
                            )
                        }
                    }
                },
        )
    }

    private fun showGenericErrorScreen(error: NewErrorMessage?) {
        logGenericErrorException(error)
        setButtonTransferEnabled(true)

        handlerValidationToken.hideAnimation(
            callbackStopAnimation =
                object : HandlerValidationToken.CallbackStopAnimation {
                    override fun onStop() {
                        doWhenResumed {
                            showErrorScreen(
                                title = getString(R.string.commons_generic_error_title),
                                message = getString(R.string.commons_generic_error_message),
                                buttonText = getString(R.string.text_close),
                                imageRes = R.drawable.ic_07,
                            )
                        }
                    }
                },
        )
    }

    private fun showErrorScreen(
        title: String,
        message: String,
        buttonText: String,
        @DrawableRes imageRes: Int,
    ) {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = title,
                message = message,
                contentImage = imageRes,
                isShowFirstButton = true,
                labelFirstButton = buttonText,
                isShowButtonClose = true,
                isShowSecondButton = false,
            )
        }
    }

    private fun showSuccessScreen(
        titleText: String,
        messageText: String,
        primaryButtonText: String,
        onPrimaryButtonClick: (Dialog?) -> Unit,
        secondaryButtonText: String? = null,
        onSecondaryButtonClick: ((Dialog?) -> Unit)? = null,
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = titleText,
                message = messageText,
                illustration = R.drawable.img_14_estrelas,
                isShowBackButton = false,
                isShowIconButtonEndHeader = false,
                labelPrimaryButton = primaryButtonText,
                labelSecondaryButton = secondaryButtonText.orEmpty(),
                onPrimaryButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) = onPrimaryButtonClick(dialog)
                    },
                onSecondaryButtonClickListener =
                    onSecondaryButtonClick?.let {
                        object : HandlerViewBuilderFluiV2.HandlerViewListener {
                            override fun onClick(dialog: Dialog?) = it.invoke(dialog)
                        }
                    },
                onBackButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) = onCloseTap(dialog)
                    },
                onIconButtonEndHeaderClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) = onCloseTap(dialog)
                    },
            )
        }
    }

    private fun buildTransferDataItemView(
        label: String,
        value: String,
    ) {
        ItemPixTransferReviewDataBinding.inflate(layoutInflater).apply {
            tvLabel.text = label
            tvValue.text = value
            binding.llTransferData.addView(root)
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
            )
        }
    }

    private fun onCloseTap(dialog: Dialog?) {
        dialog?.dismiss()
        requireActivity().toHomePix()
    }

    private fun onOpenReceiptTap(dialog: Dialog?) {
        dialog?.dismiss()
        findNavController().navigate(
            PixTransferReviewFragmentDirections
                .actionPixTransferReviewFragmentToPixTransferSentReceiptFragment(),
        )
    }

    private fun setButtonTransferEnabled(isEnabled: Boolean) {
        binding.btnTransfer.isEnabled = isEnabled
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager = childFragmentManager

    private fun logScreenView() {
        PixAnalytics.logScreenView(PixAnalytics.ScreenView.TRANSFER)
    }

    private fun logTransferSuccess() {
        PixAnalytics.run {
            logScreenView(PixAnalytics.ScreenView.TRANSFER_SUCCESS)
            logTransactionPurchase(
                amount = viewModel.store.amount,
                transactionType = PixAnalytics.Values.TRANSFER,
            )
        }
    }

    private fun logTooManyRequestsException(error: NewErrorMessage) {
        PixAnalytics.logException(
            screenView = PixAnalytics.ScreenView.TRANSFER,
            description = ERROR_CODE_TOO_MANY_REQUESTS,
            statusCode = error.httpCode.toString(),
        )
    }

    private fun logGenericErrorException(error: NewErrorMessage?) {
        PixAnalytics.logException(
            screenView = PixAnalytics.ScreenView.TRANSFER,
            statusCode = error?.httpCode?.toString(),
            description = error?.message ?: GoogleAnalytics4Values.GENERIC_ERROR,
        )
    }
}
