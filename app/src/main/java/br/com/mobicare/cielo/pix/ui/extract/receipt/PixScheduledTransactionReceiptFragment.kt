package br.com.mobicare.cielo.pix.ui.extract.receipt

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
import br.com.mobicare.cielo.databinding.FragmentPixScheduledTransactionReceiptBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.SchedulingDetailResponse
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.QRCODE
import kotlinx.android.synthetic.main.layout_pix_scheduled_about_transaction.*
import kotlinx.android.synthetic.main.layout_pix_transfer_destination_transaction.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class PixScheduledTransactionReceiptFragment : BaseFragment(), CieloNavigationListener,
    PixScheduledTransactionReceiptContract.View {

    private val pixSchedulingDetail: SchedulingDetailResponse? by lazy {
        arguments?.getParcelable(PIX_EXTRACT_RESPONSE_ARGS)
    }

    private val schedulingCode: String? by lazy {
        arguments?.getString(PIX_SCHEDULING_CODE_ARGS)
    }

    private val backTo: String? by lazy {
        arguments?.getString(PIX_GO_BACK_TO_ARGS)
    }

    private val presenter: PixScheduledTransactionReceiptPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    private var _binding: FragmentPixScheduledTransactionReceiptBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixScheduledTransactionReceiptBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupShare()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.onGetScheduling(pixSchedulingDetail, schedulingCode)
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

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_try_again_label),
            error = error
        )
    }

    override fun onClickSecondButtonError() {
        presenter.onGetScheduling(pixSchedulingDetail, schedulingCode)
    }

    override fun onActionSwipe() {
        onBackButtonClicked()
    }

    override fun onShowScheduledTransactionReceipt(pixSchedulingDetail: SchedulingDetailResponse) {
        binding?.apply {
            tvAboutAmountValue.text = pixSchedulingDetail.finalAmount?.toPtBrRealString()
            pixSchedulingDetail.message?.let {
                containerAboutMessageSent.visible()
                tvAboutMessageSentValue.text = it
            }
            tvAboutTransferTypeValue.text = getString(
                if (pixSchedulingDetail.transactionType?.contains(QRCODE.name) == true)
                    R.string.pix_qr_code_payment_transfer
                else R.string.text_pix_summary_transfer_type_value
            )
            tvAboutDateScheduledValue.text =
                pixSchedulingDetail.schedulingDate?.convertToBrDateFormat()
            tv_to_receipt.text = pixSchedulingDetail.payeeName
            tv_document_destination_receipt.text = pixSchedulingDetail.payeeDocumentNumber
            tv_bank_destination_receipt.text = pixSchedulingDetail.payeeBankName
            tvAuthenticationCodeValue.text =
                pixSchedulingDetail.idEndToEnd?.uppercase(Locale.getDefault())
            val clearDate = pixSchedulingDetail.schedulingCreationDate?.clearDate()
            val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
            val hour =
                clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
                    ?: EMPTY
            tvDateReceipt.text = getString(R.string.text_pix_scheduling_receipt_date, date, hour)
        }
    }

    private fun setupShare() {
        binding?.tvShareReceipt?.setOnClickListener {
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
}