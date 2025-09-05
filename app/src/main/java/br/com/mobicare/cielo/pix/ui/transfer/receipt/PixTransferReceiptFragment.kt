package br.com.mobicare.cielo.pix.ui.transfer.receipt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentPixTransferReceiptBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.BankAccountTypeEnum
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class PixTransferReceiptFragment : BaseFragment(), CieloNavigationListener,
    PixTransferReceiptContract.View {

    private val backTo: String? by lazy {
        arguments?.getString(PIX_GO_BACK_TO_ARGS)
    }

    private val transactionCode: String? by lazy {
        arguments?.getString(PIX_TRANSACTION_CODE_ARGS)
    }
    private val idEndToEnd: String? by lazy {
        arguments?.getString(PIX_ID_END_TO_END_ARGS)
    }

    private val detailsTransfer: TransferDetailsResponse? by lazy {
        arguments?.getParcelable(PIX_TRANSFER_DETAILS_RESPONSE_ARGS)
    }

    private val presenter: PixTransferReceiptPresenter by inject {
        parametersOf(this)
    }

    private var binding: FragmentPixTransferReceiptBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPixTransferReceiptBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupShare()
        presenter.onValidateDetails(transactionCode, idEndToEnd, detailsTransfer)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_pix_transfer_receipt_toolbar))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    private fun setupCommonTransaction(response: TransferDetailsResponse) {
        val clearDate = response.transactionDate?.clearDate()
        val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
        val hour = clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
            ?: EMPTY
        binding?.includeHeaderReceipt?.tvDateReceipt?.text =
            getString(R.string.text_pix_transfer_receipt_date, date, hour)
        binding?.includeAboutTransactionReceipt?.tvAmountReceipt?.text =
            response.finalAmount?.toPtBrRealString()
        binding?.tvAuthenticationCodeReceipt?.text =
            response.idEndToEnd?.uppercase(Locale.getDefault())

        binding?.includeAboutTransactionReceipt?.apply {
            tvChannelUsed.text = response.originChannel
            if (response.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name) {
                tvTitleChannelUsed.text =
                    getString(R.string.text_pix_transfer_channel_used_receiving_without_two_points)
                tvTransferTypeReceipt.text =
                    getString(R.string.text_pix_extract_detail_status_title_pix_receive)
                tvRateTitleReceipt.gone()
                tvRateReceipt.gone()
            }
        }
    }

    private fun setupCommonDestination(response: TransferDetailsResponse) {
        binding?.includeDestinationReceipt?.apply {
            tvToReceipt.text = response.creditParty?.name
            tvDocumentDestinationReceipt.text = response.creditParty?.nationalRegistration
            tvBankDestinationReceipt.text = response.creditParty?.bankName

            if (response.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name) {
                merchantNumberGroupDestination.visible()
                tvMerchantNumberDestination.text = response.merchantNumber
            }
        }
        binding?.includeAboutTransactionReceipt?.apply {
            if (response.payerAnswer == null)
                containerMessageReceipt.gone()
            else {
                containerMessageReceipt.visible()
                tvMessageReceipt.text = response.payerAnswer
            }
        }
    }

    private fun setupCommonOrigin(response: TransferDetailsResponse) {
        binding?.includeOriginReceipt?.apply {
            tvFromReceipt.text = response.debitParty?.name
            tvDocumentOriginReceipt.text = response.debitParty?.nationalRegistration
            tvBankOriginReceipt.text = response.debitParty?.bankName

            if (response.transactionType == PixExtractTypeEnum.TRANSFER_DEBIT.name) {
                merchantNumberGroupOrigin.visible()
                tvMerchantNumberOrigin.text = response.merchantNumber
            }
        }
    }

    private fun setupShare() {
        binding?.shareReceipt?.setOnClickListener {
            binding?.containerReceipt?.takeScreenshot()?.let { receipt ->
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = requireActivity().contentResolver.getType(receipt)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, receipt)
                }
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        resources.getText(R.string.share_receipt_pix)
                    )
                )
            } ?: run {
                Toast.makeText(
                    activity,
                    getString(R.string.text_pix_transfer_receipt_share_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun goBackTo() {
        requireActivity().toHomePix()
    }

    override fun onBackButtonClicked(): Boolean {
        goBackTo()
        return super.onBackButtonClicked()
    }

    override fun onShowCommonTransfer(response: TransferDetailsResponse) {
        setupCommonTransaction(response)
        setupCommonDestination(response)
        setupCommonOrigin(response)
    }

    override fun onShowManualTransferReceipt(response: TransferDetailsResponse) {
        onShowCommonTransfer(response)

        binding?.includeAboutTransactionReceipt?.tvAmountTitleReceipt?.text =
            getString(R.string.text_pix_transferred_value)

        val accountType =
            response.creditParty?.bankAccountType?.let { BankAccountTypeEnum.acronymToName(it) }

        binding?.includeDestinationReceipt?.apply {
            llManualTransferFields.visible()
            tvBankDestinationAgencySubtitle.text = response.creditParty?.bankBranchNumber
            tvBankDestinationAccountSubtitle.text = response.creditParty?.bankAccountNumber
            tvBankDestinationAccountTypeSubtitle.text = accountType
        }

        binding?.includeOriginReceipt?.apply {
            accountAndAgencyOriginGroup.visible()
            tvBankOriginAgencySubtitle.text = response.debitParty?.bankBranchNumber
            tvBankOriginAccountSubtitle.text = response.debitParty?.bankAccountNumber
        }
    }

    override fun onShowQrCodePaymentReceipt(response: TransferDetailsResponse) {
        onShowCommonTransfer(response)
        binding?.includeAboutTransactionReceipt?.apply {
            tvAmountTitleReceipt.text =
                getString(R.string.text_pix_summary_transfer_value_title)
            tvTransferTypeReceipt.text =
                if (response.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name)
                    getString(R.string.text_pix_extract_detail_status_title_qr_code_pix_receive)
                else
                    getString(R.string.screen_text_read_qr_code_summary_pay_type)
        }
        binding?.tvAuthenticationCodeTitleReceipt?.text =
            getString(R.string.text_pix_transfer_auth_code)
    }

    override fun onShowWithdrawReceipt(response: TransferDetailsResponse) {
        onShowCommonTransfer(response)

        response.finalAmount?.toPtBrRealString()?.let {
            binding?.includeAboutTransactionReceipt?.tvDebitInfo?.visible()
            binding?.includeAboutTransactionReceipt?.tvDebitInfo?.text =
                getString(R.string.screen_text_total_amount_qr_code_change, it)
        }

        binding?.includeAboutTransactionReceipt?.apply {
            tvAmountTitleReceipt.text =
                getString(R.string.text_pix_summary_transfer_value_title)
            tvTransferTypeReceipt.text =
                if (response.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name)
                    getString(R.string.text_pix_extract_detail_status_title_qr_code_saque_receive)
                else
                    getString(R.string.screen_text_withdraw_qr_code_receipt)
        }
        binding?.tvAuthenticationCodeTitleReceipt?.text =
            getString(R.string.text_pix_transfer_auth_code)
    }

    override fun onShowChangeReceipt(response: TransferDetailsResponse) {
        onShowCommonTransfer(response)

        binding?.includeAboutTransactionReceipt?.apply {
            tvAmountReceipt.text =
                response.purchaseAmount?.toPtBrRealString()
                    ?: response.finalAmount?.toPtBrRealString()

            response.finalAmount?.toPtBrRealString()?.let {
                tvPixAmountTotalTitleValue.text = it
                tvDebitInfo.visible()
                tvDebitInfo.text = getString(
                    R.string.screen_text_total_amount_qr_code_change, it
                )
            }

            response.changeAmount?.toPtBrRealString()?.let {
                changeDebitValueGroup.visible()
                tvPixChangeValue.text = it
            }

            if (response.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name) {
                tvAmountTitleReceipt.text = getString(R.string.text_pix_qr_code_sale_amount)
                tvTransferTypeReceipt.text =
                    getString(R.string.text_pix_extract_detail_status_title_qr_code_troco_receive)
            } else {
                tvDebitInfo.visible()
                tvPixAmountTotalTitle.gone()
                tvPixAmountTotalTitleValue.gone()
                tvAmountTitleReceipt.text = getString(R.string.text_pix_qr_code_purchase_amount)
                tvTransferTypeReceipt.text =
                    getString(R.string.screen_text_change_qr_code_receipt)
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_try_again_label),
            error = error
        )
    }

    override fun onClickSecondButtonError() {
        presenter.onGetDetails(transactionCode, idEndToEnd)
    }

    override fun onActionSwipe() {
        onBackButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}