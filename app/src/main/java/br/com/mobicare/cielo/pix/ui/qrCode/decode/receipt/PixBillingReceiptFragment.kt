package br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt

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
import br.com.mobicare.cielo.databinding.FragmentPixBillingReceiptBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_GO_BACK_TO_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_ID_END_TO_END_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_TRANSACTION_CODE_ARGS
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class PixBillingReceiptFragment : BaseFragment(), CieloNavigationListener,
    PixBillingReceiptContract.View {

    private val isBackToHome: Boolean by lazy {
        arguments?.getBoolean(PIX_GO_BACK_TO_ARGS, false) ?: false
    }

    private val transactionCode: String? by lazy {
        arguments?.getString(PIX_TRANSACTION_CODE_ARGS)
    }
    private val idEndToEnd: String? by lazy {
        arguments?.getString(PIX_ID_END_TO_END_ARGS)
    }

    private val presenter: PixBillingReceiptPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentPixBillingReceiptBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixBillingReceiptBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        onClickListenerShareReceipt()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.onGetReceipt(transactionCode, idEndToEnd)
    }

    override fun onPause() {
        super.onPause()
        presenter.onResume()
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

    private fun setupAboutTransaction(response: TransferDetailsResponse) {
        val clearDate = response.transactionDate?.clearDate()
        val date = clearDate?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY
        val hour = clearDate?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND)
            ?: EMPTY

        binding?.includeAboutTransactionReceipt?.apply {
            tvAmountTitleReceipt.text =
                getString(R.string.text_pix_summary_qrcode_cob_final_amount)
            tvAmountReceipt.text =
                response.finalAmount?.toPtBrRealString()

            tvTransferTypeReceipt.text =
                getString(R.string.pix_qr_code_payment_transfer)


            val isShowMessage = response.payerAnswer.isNullOrEmpty().not()
            containerMessageReceipt.visible(isShowMessage)
            tvMessageReceipt.text = response.payerAnswer

            tvDebitInfo.visible()
            tvDebitInfo.text =
                getString(
                    R.string.screen_text_total_amount_qr_code_change,
                    response.finalAmount?.toPtBrRealString()
                )

            //TODO implement when api is returning
            containerBillingDetails.root.gone()
        }

        binding?.includeHeaderReceipt?.tvDateReceipt?.text =
            getString(R.string.text_pix_transfer_receipt_date, date, hour)


        setStyleTitle()
        setStyleValue(response)
    }

    private fun setupCreditParty(response: TransferDetailsResponse) {
        binding?.includeDestinationReceipt?.apply {
            tvToReceipt.text = response.creditParty?.name
            tvDocumentDestinationReceipt.text = response.creditParty?.nationalRegistration
            tvBankDestinationReceipt.text = response.creditParty?.bankName
        }
    }

    private fun setBillingMessage(response: TransferDetailsResponse) {
        //TODO implement when api is returning
        binding?.includeDestinationReceipt?.apply {
            val isShowReceivedMessage = response.payerAnswer.isNullOrEmpty().not()
            messageReceivedGroup.visible(isShowReceivedMessage)
            tvBankMessageReceivedValue.text = response.payerAnswer

        }
    }

    private fun setupDebitParty(response: TransferDetailsResponse) {
        binding?.includeOriginReceipt?.apply {
            tvFromReceipt.text = response.debitParty?.name
            tvDocumentOriginReceipt.text = response.debitParty?.nationalRegistration
            tvBankOriginReceipt.text = response.debitParty?.bankName
        }
    }

    private fun setupBillingInformation(response: TransferDetailsResponse) {
        //TODO implement when api is returning
        binding?.includeBillingOtherInformation?.tvDebtorIdentifier?.text = EMPTY
    }

    private fun setupOtherInformation(response: TransferDetailsResponse) {
        binding?.tvAuthenticationCodeReceipt?.text =
            response.idEndToEnd?.uppercase(Locale.getDefault())
    }

    private fun setStyleTitle() {
        binding?.includeAboutTransactionReceipt?.containerBillingDetails?.apply {
            tvOriginalValueTitle.setTextAppearance(R.style.label_16_display_300_montserrat)
            tvInterestValueTitle.setTextAppearance(R.style.label_16_display_300_montserrat)
            tvPenaltyValueTitle.setTextAppearance(R.style.label_16_display_300_montserrat)
            tvAbatementValueTitle.setTextAppearance(R.style.label_16_display_300_montserrat)
            tvDiscountValueTitle.setTextAppearance(R.style.label_16_display_300_montserrat)
        }
    }

    private fun setStyleValue(response: TransferDetailsResponse) {
        binding?.includeAboutTransactionReceipt?.containerBillingDetails?.apply {
            tvOriginalValue.apply {
                setTextAppearance(
                    R.style.label_16_display_400_ubuntu_base
                )

                text = response.amount?.toPtBrRealString()
            }

            tvInterestValue.apply {
                setTextAppearance(
                    R.style.label_16_display_400_ubuntu_base
                )

                text = response.amount?.toPtBrRealString()
            }

            tvPenaltyValue.apply {
                setTextAppearance(
                    R.style.label_16_display_400_ubuntu_base
                )

                text = response.amount?.toPtBrRealString()
            }

            tvAbatementValue.apply {
                setTextAppearance(
                    R.style.label_16_display_400_ubuntu_base
                )

                text = response.amount?.toPtBrRealString()
            }

            tvDiscountValue.apply {
                setTextAppearance(
                    R.style.label_16_display_400_ubuntu_base
                )

                text = response.amount?.toPtBrRealString()
            }
        }
    }

    private fun onClickListenerShareReceipt() {
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

    override fun onShowReceipt(response: TransferDetailsResponse) {
        setupAboutTransaction(response)
        setupCreditParty(response)
        setupDebitParty(response)
        setupOtherInformation(response)
    }

    override fun onClickSecondButtonError() {
        presenter.onGetReceipt(transactionCode, idEndToEnd)
    }

    override fun onActionSwipe() {
        onBackButtonClicked()
    }

    override fun onBackButtonClicked(): Boolean {
        if (isBackToHome)
            requireActivity().toHomePix()
        return super.onBackButtonClicked()
    }
}