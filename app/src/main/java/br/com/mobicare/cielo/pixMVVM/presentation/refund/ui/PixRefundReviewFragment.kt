package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.orSimpleLine
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentPixRefundReviewBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.presentation.refund.models.PixCreateRefundStore
import br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.dialog.PixRefundCancelBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.dialog.PixRefundReasonBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixCreateRefundUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixCreateRefundViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixRequestRefundViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate

class PixRefundReviewFragment : PixRefundBaseFragment(), AllowMeContract.View {

    private val requestViewModel: PixRequestRefundViewModel by sharedViewModel()
    private val createViewModel: PixCreateRefundViewModel by sharedViewModel()

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val handlerValidationToken: HandlerValidationToken by inject()

    private var _binding: FragmentPixRefundReviewBinding? = null
    val binding get() = requireNotNull(_binding)

    private val createRefundStore = PixCreateRefundStore()

    private val transferDetail get() = requestViewModel.transferDetail
    private val debitParty get() = transferDetail?.debitParty
    private val recipientName get() = debitParty?.name.orEmpty()
    private val amountToRefund get() = requestViewModel.store.amount
    private val reasonMessage get() = requestViewModel.store.message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createRefundStore.apply {
            idEndToEnd = transferDetail?.idEndToEnd
            idTx = transferDetail?.idTx
            amount = amountToRefund
            message = reasonMessage
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixRefundReviewBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupValues()
        setupInformation()
        setupCreateRefundObserver()
        setupGetRefundDetailObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        navigation?.configureCollapsingToolbar(
            CieloCollapsingToolbarLayout.Configurator(
                toolbar = CieloCollapsingToolbarLayout.Toolbar(
                    title = getString(R.string.pix_refund_review_title),
                    menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                        menuRes = R.menu.menu_help,
                        onOptionsItemSelected = ::onMenuOptionSelected
                    )
                )
            )
        )
    }

    private fun setupValues() {
        binding.apply {
            tbAmount.text = amountToRefund.toPtBrRealString()
            tbMessage.text = reasonMessage
                ?: getString(R.string.pix_refund_review_button_message)
        }
    }

    private fun setupInformation() {
        binding.apply {
            tvRecipientName.text = debitParty?.name.orSimpleLine()
            tvDocument.text = debitParty?.nationalRegistration.orSimpleLine()
            tvInstitution.text = debitParty?.bankName.orSimpleLine()
            tvRefundDate.text = LocalDate.now().convertToBrDateFormat()
        }
    }

    private fun setupListeners() {
        binding.apply {
            tbAmount.setOnClickListener(::onAmountTap)
            tbMessage.setOnClickListener(::onMessageTap)
            btCancel.setOnClickListener(::onCancelTap)
            btRefund.setOnClickListener(::onRefundTap)
        }
    }

    private fun setupCreateRefundObserver() {
        createViewModel.createRefundUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixCreateRefundUiState.Success -> onCreateRefundSuccess()
                is PixCreateRefundUiState.Error -> onCreateRefundError(state)
            }
        }
    }

    private fun setupGetRefundDetailObserver() {
        createViewModel.refundDetailUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixRefundDetailUiState.Loading -> showLoading()
                is PixRefundDetailUiState.Success -> onRefundDetailSuccess(state)
                is PixRefundDetailUiState.Error -> showPendingMessageScreen()
            }
        }
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onCreateRefundSuccess() {
        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess = object : HandlerValidationToken.CallbackAnimationSuccess {
                override fun onSuccess() {
                    createViewModel.getRefundDetail()
                }
            }
        )
    }

    private fun onCreateRefundError(state: PixCreateRefundUiState.Error) {
        when (state) {
            is PixCreateRefundUiState.TokenError -> onTokenError(state.error)
            is PixCreateRefundUiState.GenericError -> showErrorMessageScreen()
            is PixCreateRefundUiState.Unprocessable -> showErrorMessageScreen(state.error.message)
        }
    }

    private fun onRefundDetailSuccess(state: PixRefundDetailUiState.Success) {
        when (state) {
            is PixRefundDetailUiState.StatusExecuted -> showExecutedMessageScreen()
            is PixRefundDetailUiState.StatusPending -> showPendingMessageScreen()
            is PixRefundDetailUiState.StatusNotExecuted -> showNotExecutedMessageScreen()
        }
    }

    private fun onAmountTap(view: View) {
        findNavController().popBackStack()
    }

    private fun onMessageTap(view: View) {
        PixRefundReasonBottomSheet(
            context = requireContext(),
            message = reasonMessage,
            onSaveMessage = {
                requestViewModel.setMessage(it)
                binding.tbMessage.text = it.ifBlank { getString(R.string.pix_refund_review_button_message) }
            }
        ).show(childFragmentManager, EMPTY)
    }

    private fun onCancelTap(v: View) {
        PixRefundCancelBottomSheet(
            context = requireContext(),
            onCancelRefund = { navigateToPixHomeExtract(null) }
        ).show(childFragmentManager, EMPTY)
    }

    private fun onRefundTap(view: View) {
        allowMePresenter.collect(
            context = requireActivity(),
            mAllowMeContextual = allowMePresenter.init(requireContext()),
            mandatory = true
        )
    }

    override fun successCollectToken(result: String) {
        setButtonTransferEnabled(false)
        createRefundStore.fingerprint = result
        getToken()
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
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
                override fun onSuccess(token: String) {
                    createRefundStore.otpCode = token
                    onTokenSuccess()
                }
                override fun onError() = onTokenError()
            }
        )
    }

    private fun onTokenSuccess() {
        if (createRefundStore.validate()) {
            createViewModel.createRefund(createRefundStore)
        } else {
            showErrorMessageScreen()
        }
    }

    private fun onTokenError(error: NewErrorMessage? = null) {
        setButtonTransferEnabled(true)

        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = getToken()
            }
        )
    }

    private fun showErrorMessageScreen(message: String? = null) {
        setButtonTransferEnabled(true)

        handlerValidationToken.hideAnimation(
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {
                override fun onStop() {
                    showMessageScreen(
                        title = getString(R.string.commons_generic_error_title),
                        message = message ?: getString(R.string.commons_generic_error_message),
                        imageRes = R.drawable.ic_07,
                        primaryButtonText = getString(R.string.entendi),
                        onPrimaryButtonTap = ::onCloseTap
                    )
                }
            }
        )
    }

    private fun showExecutedMessageScreen() {
        navigation?.run {
            hideAnimatedLoading()
            showMessageScreen(
                title = getString(R.string.pix_refund_create_status_executed_title),
                message = getString(
                    R.string.pix_refund_create_status_executed_message,
                    amountToRefund.toPtBrRealString(),
                    recipientName
                ),
                imageRes = R.drawable.img_14_estrelas,
                showCloseIconButton = true,
                primaryButtonText = getString(R.string.pix_refund_create_show_receipt),
                onPrimaryButtonTap = ::onShowReceipt,
                secondaryButtonText = getString(R.string.text_close),
                onSecondaryButtonTap = ::navigateToPixHomeExtract,
                onDismissTap = ::navigateToPixHomeExtract
            )
        }
    }

    private fun showPendingMessageScreen() {
        navigation?.run {
            hideAnimatedLoading()
            showMessageScreen(
                title = getString(R.string.pix_refund_create_status_pending_title),
                message = getString(
                    R.string.pix_refund_create_status_pending_message,
                    amountToRefund.toPtBrRealString(),
                    recipientName
                ),
                imageRes = R.drawable.img_44_aguarde,
                showCloseIconButton = true,
                primaryButtonText = getString(R.string.pix_refund_create_show_receipt),
                onPrimaryButtonTap = ::onShowReceipt,
                onDismissTap = ::navigateToPixHomeExtract
            )
        }
    }

    private fun showNotExecutedMessageScreen() {
        navigation?.run {
            hideAnimatedLoading()
            showMessageScreen(
                title = getString(R.string.pix_refund_create_status_not_executed_title),
                message = getString(
                    R.string.pix_refund_create_status_not_executed_message,
                    amountToRefund.toPtBrRealString(),
                    recipientName
                ),
                imageRes = R.drawable.img_107_transacao_erro,
                showCloseIconButton = true,
                primaryButtonText = getString(R.string.text_close),
                onPrimaryButtonTap = ::navigateToPixHomeExtract,
                onDismissTap = ::navigateToPixHomeExtract
            )
        }
    }

    private fun onShowReceipt(dialog: Dialog?) {
        dialog?.dismiss()
        findNavController().safeNavigate(
            PixRefundReviewFragmentDirections
                .actionPixRefundReviewFragmentToPixRefundDetailReceiptFragment()
        )
    }

    private fun setButtonTransferEnabled(isEnabled: Boolean) {
        binding.btRefund.isEnabled = isEnabled
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager = childFragmentManager

}