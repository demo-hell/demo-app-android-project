package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.insertAmount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.ui.fragment.insertAmount.BaseInsertAmountV2Fragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.orZero
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixTransferAmountHeaderBinding
import br.com.mobicare.cielo.extensions.ifNullOrBlank
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.PixQRCodeNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel.PixQRCodePaymentViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class PixQRCodePaymentInsertAmountFragment : BaseInsertAmountV2Fragment() {
    private var bindingHeader: FragmentPixTransferAmountHeaderBinding? = null

    private val viewModel: PixQRCodePaymentViewModel by sharedViewModel()
    private val navArgs: PixQRCodePaymentInsertAmountFragmentArgs by navArgs()

    private val currentBalance by lazy {
        (navigation?.getData() as? PixQRCodeNavigationFlowActivity.NavArgs.Data)
            ?.currentBalance ?: ZERO_DOUBLE
    }

    private val pixDecodeQRCode by lazy { navArgs.pixdecodeqrcodemodelargs }
    private val openedFromTheSummaryFragment by lazy { navArgs.openedfromthesummaryfragmentargs }
    private val isToChangeTheChangeAmount by lazy { navArgs.istochangethechangeamountargs }

    private val paymentAmount get() = viewModel.paymentAmount.value.orZero()
    private val changeAmount get() = viewModel.changeAmount.value.orZero()

    override val title: String
        get() =
            getString(
                if (isToChangeTheChangeAmount) {
                    R.string.pix_qr_code_payment_insert_amount_toolbar_title_change
                } else {
                    R.string.pix_qr_code_payment_insert_amount_toolbar_title
                },
            )

    override val headerView get() = bindingHeader?.root

    override val toolbarMenu
        get() =
            CieloCollapsingToolbarLayout.ToolbarMenu(
                menuRes = R.menu.menu_help,
                onOptionsItemSelected = ::onClickOptionsItemMenuToolbar,
            )

    override val footerText
        get() =
            if (currentBalance > ZERO_DOUBLE) {
                getString(R.string.pix_transfer_amount_footer, currentBalance.toPtBrRealString())
            } else {
                null
            }

    override val validators
        get() =
            if (currentBalance > ZERO_DOUBLE) {
                super.validators +
                    listOf(
                        TextFieldFlui.Validator(
                            rule = { it.moneyToDoubleValue() <= currentBalance },
                            errorMessage = getString(R.string.pix_transfer_amount_error_insufficient_balance),
                        ),
                    )
            } else {
                super.validators
            }

    override val actionButton: ActionButton
        get() =
            ActionButton(
                text = getString(R.string.confirmar),
                onTap = ::onClickConfirmButton,
            )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): LinearLayout {
        bindingHeader = FragmentPixTransferAmountHeaderBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        pixDecodeQRCode?.let { viewModel.setPixDecodeQRCode(it) }
    }

    override fun onResume() {
        super.onResume()
        navigation?.showContent()
        setupHeaderView()
        setupAmount()
    }

    override fun onDestroyView() {
        bindingHeader = null
        super.onDestroyView()
    }

    private fun setupHeaderView() {
        bindingHeader?.apply {
            root.setCustomDrawable {
                solidColor = R.color.cloud_100
                radius = R.dimen.dimen_8dp
            }

            viewModel.pixDecodeQRCode.value?.also {
                tvRecipientName.text =
                    getString(
                        R.string.pix_transfer_amount_info_recipient_name,
                        it.receiverName,
                    )

                tvRecipientInfo.text =
                    getString(
                        R.string.pix_transfer_amount_info_recipient_info,
                        it.receiverPersonType?.documentType.ifNullOrBlank(getString(R.string.pix_transfer_review_label_document)),
                        it.receiverDocument,
                        PixQRCodeUtils.getBankName(it),
                    )
            }
        }
    }

    private fun setupAmount() {
        if (isToChangeTheChangeAmount && changeAmount > ZERO_DOUBLE) {
            setAmount(changeAmount)
        } else if (paymentAmount > ZERO_DOUBLE) {
            setAmount(paymentAmount)
        }
    }

    private fun onClickConfirmButton(amount: BigDecimal) {
        if (isToChangeTheChangeAmount) {
            viewModel.setChangeAmount(amount.toDouble())
        } else {
            viewModel.setPaymentAmount(amount.toDouble())
        }
        navigateToSummary()
    }

    private fun navigateToSummary() {
        if (openedFromTheSummaryFragment) {
            findNavController().popBackStack()
        } else {
            findNavController().safeNavigate(
                PixQRCodePaymentInsertAmountFragmentDirections.actionPixQRCodePaymentInsertAmountFragmentToPixQRCodePaymentSummaryFragment(
                    null,
                ),
            )
        }
    }

    private fun onClickOptionsItemMenuToolbar(item: MenuItem) {
        when (item.itemId) {
            R.id.menuActionHelp -> openFAQPix()
        }
    }

    private fun openFAQPix() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
            subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
        )
    }
}
