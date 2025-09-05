package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import android.app.Dialog
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.bottomsheet.CieloButtonListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THOUSAND
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionTransactionsBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_MY_KEYS_ARGS
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.PixMyKeysFragment
import br.com.mobicare.cielo.pix.ui.qrCode.charge.PixGenerateQrCodeNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.presentation.home.adapters.PixTransactionsMenuAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButton
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixTransactionButtonId
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.key.PixKeyNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixKeyTypeButton
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.PixQRCodeNavigationFlowActivity
import org.jetbrains.anko.startActivity

class PixTransactionsViewSection(
    fragment: PixHomeFragment,
    viewModel: PixHomeViewModel,
    binding: IncludePixHomeSectionTransactionsBinding,
    private val buttons: List<PixTransactionButton>,
    private val onVerifyAllowMe: () -> Unit,
) : PixHomeViewSection(fragment, viewModel) {
    private var isTransfer = false

    init {
        binding.apply {
            root.postDelayed({
                shimmerLoading.gone()
                rvTransactions.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = PixTransactionsMenuAdapter(buttons, ::onTransactionButtonClick)
                    visible()
                }
            }, THOUSAND)
        }
    }

    private fun onTransactionButtonClick(id: PixTransactionButtonId) {
        when (id) {
            PixTransactionButtonId.TRANSFER -> onTransferButtonClick()
            PixTransactionButtonId.READ_QR_CODE -> onReadQrCodeButtonClick()
            PixTransactionButtonId.GENERATE_CHARGE -> onGenerateChargeButtonClick()
            PixTransactionButtonId.COPY_AND_PASTE -> onCopyAndPasteButtonClick()
        }
    }

    private fun onTransferButtonClick() {
        isTransfer = true
        onVerifyAllowMe()
    }

    private fun onReadQrCodeButtonClick() {
        isTransfer = false
        onVerifyAllowMe()
    }

    private fun onGenerateChargeButtonClick() {
        validateMyKeys()
    }

    private fun onCopyAndPasteButtonClick() {
        context.startActivity<PixQRCodeNavigationFlowActivity>(
            PixQRCodeNavigationFlowActivity.NavArgs.IS_READING_QR_CODE_ARGS to false,
            PixQRCodeNavigationFlowActivity.NavArgs.CURRENT_BALANCE to accountBalanceStore.balance,
        )
    }

    fun onSuccessCollectToken() {
        if (isTransfer) {
            showSelectKey()
        } else {
            context.startActivity<PixQRCodeNavigationFlowActivity>(
                PixQRCodeNavigationFlowActivity.NavArgs.IS_READING_QR_CODE_ARGS to true,
                PixQRCodeNavigationFlowActivity.NavArgs.CURRENT_BALANCE to accountBalanceStore.balance,
            )
        }
    }

    fun onErrorCollectToken(errorMessage: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(errorMessage)
            .closeTextButton(getString(R.string.dialog_button))
            .build()
            .showAllowingStateLoss(activity.supportFragmentManager, getString(R.string.text_cieloalertdialog))
    }

    private fun validateMyKeys() {
        keysStore.keys?.let {
            if (hasValidKeyToGenerateQrCode(it)) {
                navigateToQrCodeGeneration()
            } else {
                showCreateKeyDialog()
            }
        } ?: run {
            showErrorToGenerateQrCode()
        }
    }

    private fun showSelectKey() {
        CieloButtonListBottomSheet
            .create(
                headerConfigurator =
                    CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.pix_home_transaction_transfer_bs_title),
                        showCloseButton = true,
                    ),
                buttons =
                    PixKeyTypeButton.values().map {
                        CieloButtonListBottomSheet.Button(
                            id = it.ordinal,
                            text = getString(it.label),
                            drawableRes = it.iconId,
                        )
                    },
                onButtonItemClicked = { button, bs ->
                    bs.dismissAllowingStateLoss()

                    activity.startActivity<PixKeyNavigationFlowActivity>(
                        PixKeyNavigationFlowActivity.NavArgs.KEY_TYPE_ARGS to button.id,
                        PixKeyNavigationFlowActivity.NavArgs.CURRENT_BALANCE_ARGS to accountBalanceStore.balance,
                    )
                },
            ).show(fragment.parentFragmentManager, PixTransactionsViewSection::class.java.simpleName)
    }

    private fun hasValidKeyToGenerateQrCode(keys: List<PixKeysResponse.KeyItem>?) =
        keys?.any {
            it.claimType != PixClaimTypeEnum.PORTABILITY.name &&
                it.claimType != PixClaimTypeEnum.OWNERSHIP.name
        } ?: false

    private fun navigateToQrCodeGeneration() {
        activity.startActivity<PixGenerateQrCodeNavigationFlowActivity>(
            PIX_MY_KEYS_ARGS to keysStore.keys,
        )
    }

    private fun showCreateKeyDialog() {
        CieloDialog
            .create(
                title = getString(R.string.pix_home_dialog_new_key_title),
                message = getString(R.string.pix_home_dialog_new_key_message),
            ).setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .setPrimaryButton(getString(R.string.pix_home_key_registration_random))
            .setSecondaryButton(getString(R.string.pix_home_dialog_new_key_button))
            .setOnPrimaryButtonClickListener {
                navigateToAddRandomKey()
            }.setOnSecondaryButtonClickListener {
                navigateToMyKeys()
            }.show(activity.supportFragmentManager, PixMyKeysFragment::class.java.simpleName)
    }

    private fun showErrorToGenerateQrCode() {
        fragment
            .bottomSheetGenericFlui(
                EMPTY,
                R.drawable.ic_07,
                getString(R.string.text_title_generic_error),
                getString(R.string.business_error),
                getString(R.string.text_try_again_label),
                getString(R.string.text_try_again_label),
                statusNameTopBar = false,
                statusTitle = true,
                statusSubTitle = true,
                statusImage = true,
                statusBtnClose = false,
                statusBtnFirst = false,
                statusBtnSecond = true,
                statusView1Line = true,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            dismiss()
                            validateMyKeys()
                        }

                        override fun onSwipeClosed() {
                            dismiss()
                        }
                    }
            }.show(fragment.parentFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}
