package br.com.mobicare.cielo.posVirtual.presentation.qrCodePix.insertAmount

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.fragment.insertAmount.BaseInsertAmountFragment
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualQRCodePixState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PosVirtualQRCodePixInsertAmountFragment : BaseInsertAmountFragment() {

    private val viewModel: PosVirtualQRCodePixInsertAmountViewModel by viewModel()
    private val handlerValidationToken: HandlerValidationToken by inject()

    private val args: PosVirtualQRCodePixInsertAmountFragmentArgs by navArgs()
    private val logicalNumber: String by lazy {
        args.posvirtuallogicalnumberargs
    }

    private val ga4: PosVirtualAnalytics by inject()
    private val screenPath: String get() = PosVirtualAnalytics.SCREEN_VIEW_QRCODE_PIX_INSERT_VALUE

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun getTitle(): String {
        return getString(R.string.txt_title_pos_virtual_qr_code_pix_insert_amount)
    }

    override fun getTextButton(): String {
        return getString(R.string.txt_label_button_pos_virtual_qr_code_pix_insert_amount)
    }

    override fun getOnButtonClicked(): () -> Unit {
        return ::getToken
    }

    override fun observe(): () -> Unit {
        return ::setupObserve
    }

    private fun setupObserve() {
        setupObserveLoadingState()
        setupObserveQRCodePixState()
    }

    private fun setupObserveLoadingState() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            onHideLoading()
        }
    }

    private fun setupObserveQRCodePixState() {
        viewModel.uiPosVirtualQRCodePixStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPosVirtualQRCodePixState.Success -> onSuccess(state.response)
                is UIPosVirtualQRCodePixState.Error -> setupErrorGenerateQRCodePix(state)
            }
        }
    }

    private fun setupErrorGenerateQRCodePix(state: UIPosVirtualQRCodePixState.Error) {
        when (state) {
            is UIPosVirtualQRCodePixState.TokenError -> onErrorToken(state.error)
            is UIPosVirtualQRCodePixState.TimeOutError -> {
                onError(
                    getString(R.string.pos_virtual_qr_code_insert_amount_title_bs_impossible_create_qr_code),
                    getString(R.string.pos_virtual_qr_code_insert_amount_message_bs_time_out),
                    getString(R.string.text_try_again_label),
                    isReturnHomePosVirtual = false,
                    state.error,
                    isLogException = true
                )
            }
            is UIPosVirtualQRCodePixState.InvalidAmountError -> {
                onError(
                    getString(R.string.pos_virtual_qr_code_insert_amount_title_bs_impossible_create_qr_code),
                    getString(R.string.pos_virtual_qr_code_insert_amount_message_bs_invalid_amount),
                    getString(R.string.text_try_again_label),
                    isReturnHomePosVirtual = false,
                    state.error,
                    isLogException = true
                )
            }
            is UIPosVirtualQRCodePixState.IntegrationError -> {
                onError(
                    getString(R.string.pos_virtual_qr_code_insert_amount_title_bs_impossible_create_qr_code),
                    getString(R.string.pos_virtual_qr_code_insert_amount_message_bs_other_errors),
                    getString(R.string.entendi),
                    isReturnHomePosVirtual = true,
                    state.error,
                    isLogException = true
                )
            }
            is UIPosVirtualQRCodePixState.LimitExceededError -> {
                onError(
                    getString(R.string.pos_virtual_qr_code_insert_amount_title_bs_limit_exceeded),
                    state.error?.message.orEmpty(),
                    getString(R.string.text_try_again_label),
                    isReturnHomePosVirtual = false,
                    state.error,
                    isLogException = false
                )
            }
            is UIPosVirtualQRCodePixState.GenericError -> {
                onError(
                    getString(R.string.commons_generic_error_title),
                    getString(R.string.pos_virtual_error_message_generic),
                    getString(R.string.entendi),
                    isReturnHomePosVirtual = true,
                    state.error,
                    isLogException = true
                )
            }
        }
    }

    private fun getToken() {
        logClickGenerateQRCodePix()

        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) =
                    viewModel.generateQRCode(
                        context,
                        token,
                        getAmount(),
                        logicalNumber
                    )

                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onHideLoading() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {}
        )
    }

    private fun onSuccess(response: PosVirtualCreateQRCodeResponse) {
        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
        object : HandlerValidationToken.CallbackAnimationSuccess {
            override fun onSuccess() {
                findNavController().navigate(
                    PosVirtualQRCodePixInsertAmountFragmentDirections.actionPosVirtualInsertAmountQRCodePixToPosVirtualViewQRCodePix(
                        response
                    )
                )
            }
        })
    }

    private fun onError(
        title: String,
        message: String,
        labelButton: String,
        isReturnHomePosVirtual: Boolean,
        error: NewErrorMessage?,
        isLogException: Boolean
    ) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                doWhenResumed {
                    if (isLogException) {
                        logException(error)
                    } else {
                        logDisplayContent()
                    }

                    navigation?.showCustomHandlerView(
                        title = title,
                        message = message,
                        labelSecondButton = labelButton,
                        isShowButtonClose = true,
                        callbackSecondButton = {
                            if (isReturnHomePosVirtual) {
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        },
                        callbackClose = {
                            requireActivity().finish()
                        }
                    )
                }
            }
        })
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        logException(error)

        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    getToken()
                }
            }
        )
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logDisplayContent() = ga4.logDisplayContentQRCodeInsertValue()

    private fun logClickGenerateQRCodePix() = ga4.logBeginCheckoutInsertValueQRCodePix()

    private fun logException(error: NewErrorMessage?) = ga4.logException(screenPath, error)

}