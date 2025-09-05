package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.scheduleCancel

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailConfirmCancelScheduleBottomSheetBinding
import br.com.mobicare.cielo.extensions.ifNullOrBlank
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity.NavArgs.SCHEDULED_PIX_WAS_CANCELED
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.PixNewExtractDetailViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.CancelScheduleError
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.CancelScheduleSuccess
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.HideLoading
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.HideLoadingCancelSchedule
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.ScheduleDetailError
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.ScheduleDetailPending
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.ScheduleDetailSuccess
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState.ShowLoading

class PixCancelScheduleViewHandler {
    private var viewModel: PixNewExtractDetailViewModel? = null
    private var fragment: Fragment? = null
    private var handlerValidationToken: HandlerValidationToken? = null
    private var navigation: CieloNavigation? = null

    private val context get() = fragment?.context

    private val onReturnExtractDetail
        get() =
            object : HandlerViewBuilderFluiV2.HandlerViewListener {
                override fun onClick(dialog: Dialog?) {
                    navigation?.showContent()
                    dialog?.dismiss()
                }
            }

    private val onReturnExtractPages
        get() =
            object : HandlerViewBuilderFluiV2.HandlerViewListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    fragment?.activity?.onBackPressedDispatcher?.onBackPressed()
                    navigation?.saveData(
                        Bundle().apply {
                            putBoolean(SCHEDULED_PIX_WAS_CANCELED, true)
                        },
                    )
                }
            }

    private val isRecurrentTransferSchedule get() = viewModel?.isRecurrentTransferSchedule == true

    private val content get() = PixCancelScheduleContentFactory(isRecurrentTransferSchedule)

    fun setup(
        fragment: Fragment?,
        viewModel: PixNewExtractDetailViewModel?,
        handlerValidationToken: HandlerValidationToken?,
    ) {
        this.fragment = fragment
        this.viewModel = viewModel
        this.handlerValidationToken = handlerValidationToken
    }

    fun setNavigation(navigation: CieloNavigation?) {
        this.navigation = navigation
    }

    fun initializeCancelScheduleStateObserver() {
        fragment?.viewLifecycleOwner?.let {
            viewModel?.cancelScheduleState?.observe(it) { state ->
                when (state) {
                    is ShowLoading -> navigation?.showAnimatedLoading()
                    is HideLoading -> navigation?.hideAnimatedLoading()
                    is HideLoadingCancelSchedule -> onHideLoadingOTP()
                    is CancelScheduleSuccess -> onCancelScheduleSuccess()
                    is CancelScheduleError -> onCancelScheduleError(state.error)
                    is ScheduleDetailSuccess -> onScheduleDetailSuccess()
                    is ScheduleDetailPending -> onScheduleDetailPending()
                    is ScheduleDetailError -> onScheduleDetailPending()
                }
            }
        }
    }

    fun cancelSchedule() {
        val fragmentManager = fragment?.childFragmentManager ?: return
        val content = content.bottomSheetContent

        CieloContentBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(content.title),
                    showCloseButton = true,
                ),
            contentLayoutRes = R.layout.layout_pix_extract_detail_confirm_cancel_schedule_bottom_sheet,
            disableExpandableMode = true,
            onContentViewCreated = { view, bottomSheet ->
                LayoutPixExtractDetailConfirmCancelScheduleBottomSheetBinding.bind(view).apply {
                    tvMessage.text = getString(content.message)
                    btnConfirm.apply {
                        text = getString(content.confirmButtonText)
                        setOnClickListener {
                            getToken()
                            bottomSheet.dismiss()
                        }
                    }
                    btnCancel.apply {
                        text = getString(content.cancelButtonText)
                        setOnClickListener {
                            bottomSheet.dismiss()
                        }
                    }
                }
            },
        ).show(fragmentManager, EMPTY)
    }

    private fun getToken() {
        val fragmentManager = fragment?.childFragmentManager ?: return

        handlerValidationToken?.getToken(
            fragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) {
                    viewModel?.cancelTransferSchedule(token)
                }

                override fun onError() = onErrorToken()
            },
        )
    }

    private fun onHideLoadingOTP() {
        handlerValidationToken?.hideAnimation(
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {},
        )
    }

    private fun onCancelScheduleSuccess() {
        navigation?.showContent(false)
        handlerValidationToken?.playAnimationSuccess(
            callbackAnimationSuccess =
                object : HandlerValidationToken.CallbackAnimationSuccess {
                    override fun onSuccess() {
                        viewModel?.getScheduleDetailsAfterCancelTransferSchedule()
                    }
                },
        )
    }

    private fun onScheduleDetailSuccess() {
        val content = content.scheduleDetailSuccessContent

        navigation?.showHandlerViewV2(
            illustration = R.drawable.img_14_estrelas,
            title = getString(content.title),
            message = getString(content.message),
            labelPrimaryButton = getString(R.string.text_close),
            onPrimaryButtonClickListener = onReturnExtractPages,
            onIconButtonEndHeaderClickListener = onReturnExtractPages,
            onBackButtonClickListener = onReturnExtractPages,
        )
    }

    private fun onScheduleDetailPending() {
        navigation?.showHandlerViewV2(
            illustration = R.drawable.img_44_aguarde,
            title = getString(R.string.pix_extract_detail_title_bs_pending_cancel_schedule),
            message = getString(R.string.pix_extract_detail_message_bs_pending_cancel_schedule),
            labelPrimaryButton = getString(R.string.text_close),
            onPrimaryButtonClickListener = onReturnExtractPages,
            onIconButtonEndHeaderClickListener = onReturnExtractPages,
            onBackButtonClickListener = onReturnExtractPages,
        )
    }

    private fun onCancelScheduleError(error: NewErrorMessage? = null) {
        val message = error?.message.ifNullOrBlank(getString(R.string.commons_generic_error_message))

        handlerValidationToken?.hideAnimation(
            callbackStopAnimation =
                object : HandlerValidationToken.CallbackStopAnimation {
                    override fun onStop() {
                        navigation?.showHandlerViewV2(
                            title = getString(R.string.commons_generic_error_title),
                            message = message,
                            labelPrimaryButton = getString(R.string.back),
                            onPrimaryButtonClickListener = onReturnExtractDetail,
                            onIconButtonEndHeaderClickListener = onReturnExtractDetail,
                            onBackButtonClickListener = onReturnExtractDetail,
                        )
                    }
                },
        )
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken?.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = getToken()

                override fun onBack() {
                    navigation?.showContent()
                }

                override fun onClose() {
                    navigation?.showContent()
                }
            },
        )
    }

    private fun getString(resId: Int) = context?.getString(resId) ?: EMPTY
}
