package br.com.mobicare.cielo.pix.ui.extract.reversal.receipt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentPixReversalReceiptBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_TRANSACTION_CODE_ARGS
import br.com.mobicare.cielo.pix.domain.ReversalDetailsFullResponse
import br.com.mobicare.cielo.pix.domain.ReversalDetailsResponse
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum.*
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class PixReversalReceiptFragment : BaseFragment(), CieloNavigationListener,
    PixReversalReceiptContract.View {

    private val transactionCode: String? by lazy {
        arguments?.getString(PIX_TRANSACTION_CODE_ARGS)
    }

    private val presenter: PixReversalReceiptPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    private var binding: FragmentPixReversalReceiptBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPixReversalReceiptBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupShare()
        presenter.getReversalDetails(transactionCode)
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
            navigation?.apply {
                setTextToolbar(getString(R.string.text_pix_transfer_receipt_toolbar))
                showContainerButton()
                showHelpButton()
                setNavigationListener(this@PixReversalReceiptFragment)
            }
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    private fun setupReversalData(response: ReversalDetailsResponse) {
        val clearDate = response.transactionDate?.clearDate()
        val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
        val hour = clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
            ?: EMPTY

        binding?.tvTransactionCodeReceipt?.text =
            response.transactionCode?.uppercase(Locale.getDefault())

        binding?.tvDateReceipt?.text =
            getString(R.string.text_pix_transfer_receipt_date, date, hour)
        binding?.includeReversalDataReceipt?.apply {
            if (response.transactionType == REVERSAL_CREDIT.name) {
                tvAmountTitleReceiptReversal.text =
                    getString(R.string.tv_amount_title_receipt_reversal_credit)
                tvTransferTypeReceipt.text = getString(R.string.pix_received_reversal)
            } else {
                tvAmountTitleReceiptReversal.text =
                    getString(R.string.tv_amount_title_receipt_reversal_debit)
                tvTransferTypeReceipt.text = getString(R.string.pix_sent_reversal)
            }
            tvReversalDataAmount.text = response.finalAmount?.toPtBrRealString()
            tvReversalAuthCodeReceipt.text = response.idEndToEndReturn?.uppercase(
                Locale.getDefault()
            )
            if (response.reversalReason.isNullOrEmpty().not()) {
                containerReasonReceipt.visible()
                tvReasonReceipt.text = response.reversalReason
            }
            tvReversalDataAmount.text = response.finalAmount?.toPtBrRealString()
        }
    }

    private fun setupOriginalTransaction(response: TransferDetailsResponse) {
        binding?.includeReversalOriginalTransaction?.apply {
            tvOriginalAmount.text = response.finalAmount?.toPtBrRealString()
            tvOriginalTransactionAuthCode.text = response.idEndToEnd?.uppercase(Locale.getDefault())
            tvOriginalTransactionType.text =
                getTransactionType(response.pixType.toString(), response.idTx)

            val clearDate = response.transactionDate?.clearDate()
            val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
            val hour =
                clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
                    ?: EMPTY

            tvOriginalTransactionDateHour.text = getString(
                R.string.text_pix_reversal_receipt_original_transaction_date_hour,
                date,
                hour
            )

            if (response.transactionType == TRANSFER_DEBIT.name) {
                tvPayerTitleReceipt.text =
                    getString(R.string.text_pix_reversal_receipt_original_payer_credit)
                tvOriginalTransactionDocument.text = response.creditParty?.nationalRegistration
                tvOriginalTransactionInstitution.text = response.creditParty?.bankName
                tvOriginalTransactionPayer.text = response.creditParty?.name
            } else {
                tvPayerTitleReceipt.text =
                    getString(R.string.text_pix_reversal_receipt_original_payer_debit)
                tvOriginalTransactionDocument.text = response.debitParty?.nationalRegistration
                tvOriginalTransactionInstitution.text = response.debitParty?.bankName
                tvOriginalTransactionPayer.text = response.debitParty?.name
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

    override fun onShowReversalData(reversalDetailsResponse: ReversalDetailsFullResponse) {
        setupReversalData(reversalDetailsResponse.refundDetail)
        setupOriginalTransaction(reversalDetailsResponse.transferDetail)
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_try_again_label),
            error = error
        )
    }

    override fun onClickSecondButtonError() {
        presenter.getReversalDetails(transactionCode)
    }

    override fun onActionSwipe() {
        findNavController().popBackStack()
    }

    private fun getTransactionType(pixType: String, idTx: String?): String {
        if (idTx.isNullOrEmpty())
            return getString(R.string.text_pix_extract_detail_status_title_pix_receive)
        return getString(
            when (pixType) {
                PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> R.string.text_pix_extract_detail_status_title_qr_code_saque_receive
                PixQRCodeOperationTypeEnum.CHANGE.name -> R.string.text_pix_extract_detail_status_title_qr_code_troco_receive
                PixQRCodeOperationTypeEnum.TRANSFER.name -> R.string.text_pix_extract_detail_status_title_qr_code_pix_receive
                else -> R.string.text_pix_extract_detail_status_title_pix_receive
            }
        )
    }
}