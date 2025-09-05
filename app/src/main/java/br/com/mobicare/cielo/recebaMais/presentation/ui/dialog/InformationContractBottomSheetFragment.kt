package br.com.mobicare.cielo.recebaMais.presentation.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.text.bold
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.formatBankName
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetails
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_information_contract_bottom_sheet.*

const val ARG_PARAM_CONTRACT_DETAILS = "ARG_PARAM_CONTRACT_DETAILS"

class InformationContractBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var contract: ContractDetails

    companion object {
        fun create(contract: ContractDetails) = InformationContractBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PARAM_CONTRACT_DETAILS, contract)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_information_contract_bottom_sheet, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupDialog(dialog)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getContract()
        closeBottomSheet()
    }

    private fun setupDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= 4) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun getContract() {
        arguments?.getParcelable<ContractDetails>(ARG_PARAM_CONTRACT_DETAILS)?.let {
            contract = it
            setupContract(contract)
        }
    }

    private fun setupContract(contract: ContractDetails) {
        val percentAnnual = Utils.convertToPercent(contract.annualEffectiveCostRate)
        val percent = " (${getString(R.string.value_contract_receive_more_percent, percentAnnual)} a.a)"
        val valueContract = SpannableStringBuilder().bold { append(contract.valueContract.toPtBrRealString()) }.append(percent)

        val percentMonth = " (${Utils.convertToPercent(contract.interestRate)} ${getString(R.string.value_contract_receive_more_tax_month)})"
        val installment = "${contract.quantity}${getString(R.string.value_contract_receive_more_quantity)} ${contract.installmentAmount.toPtBrRealString()}"
        val installmentContract = SpannableStringBuilder().bold { append(installment) }.append(percentMonth)

        var accountDigit = ""
        contract.bankAccount.accountDigit?.let {
            accountDigit = " - $it"
        }

        val account = "${contract.bankAccount.account}$accountDigit"
        val dayPay = getDayPay(contract.paymentFirstInstallmentDate)

        text_title_total_value?.text = valueContract
        text_title_parcel_value?.text = installmentContract
        text_title_opening_fee_value?.text = contract.registrationFee.toPtBrRealString()
        text_title_contract_date_value?.text = contract.contractDate.dateFormatToBr()
        text_title_first_parcel_value?.text = contract.paymentFirstInstallmentDate.dateFormatToBr()
        text_title_code_value?.text = contract.mechantId
        text_title_document_value?.text = contract.customerId
        text_financial_partner_value?.text = "${contract.partner.name} - ${contract.partner.code}"
        text_bank_name?.text = contract.bankAccount.name.formatBankName()
        text_agency_number?.text = getString(R.string.text_bank_agency_template, contract.bankAccount.agency)
        text_account_number?.text = getString(R.string.text_bank_account_number_template, account)

        if (dayPay.isNotEmpty()) {
            text_title_due_date?.visible()
            text_title_due_date_value?.visible()
            text_title_due_date_value?.text = "$dayPay ${getString(R.string.day_of_month_sheet_details_receive_more)}"
        }
    }

    private fun closeBottomSheet() {
        btn_close?.setOnClickListener {
            dismiss()
        }
    }

    private fun getDayPay(date: String?): String {
        return date?.let {
            it.dateFormatToBr().substring(0, 2)
        } ?: ""
    }
}