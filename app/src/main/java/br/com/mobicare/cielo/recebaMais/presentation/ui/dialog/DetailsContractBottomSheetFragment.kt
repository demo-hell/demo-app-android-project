package br.com.mobicare.cielo.recebaMais.presentation.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.recebaMais.RM_HELP_ID
import br.com.mobicare.cielo.recebaMais.domains.entities.InstallmentDetails
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.STATUS_OPENED
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_details_contract_bottom_sheet_dialog.*

const val ARG_PARAM_INSTALLMENT_ITEM = "ARG_PARAM_INSTALLMENT_ITEM"

class DetailsContractBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var installment: InstallmentDetails

    companion object {
        fun create(installment: InstallmentDetails): DetailsContractBottomSheetFragment = DetailsContractBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PARAM_INSTALLMENT_ITEM, installment)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupDialog(dialog)
        return inflater.inflate(R.layout.fragment_details_contract_bottom_sheet_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getContract()
        setupBtnHelp()
    }

    private fun setupDialog(dialog: Dialog?) {
        dialog?.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun getContract() {
        arguments?.getParcelable<InstallmentDetails>(ARG_PARAM_INSTALLMENT_ITEM)?.let {
            installment = it
            setupContract(installment)
        }
    }

    private fun setupContract(installment: InstallmentDetails) {
        var status = ""
        if (installment.statusCode == STATUS_OPENED) {
            status = getString(R.string.status_not_receive_more)
            image_view?.setBackgroundResource(R.drawable.ic_next_installment)

            if (installment.amountOwed != null && installment.amountOwed != 0.0) {
                container_amount?.visible()
                text_amount_parcel_value?.text = installment.amountOwed.toPtBrRealString()
            }
        } else {
            image_view?.setBackgroundResource(R.drawable.ic_installment_paid)
        }

        val month = DataCustomNew()
                .setDateFromAPI(installment.dueDate)
                .toCalendar()
                .format(LONG_MONTH_DESCRIPTION)
                .capitalize()

        val year = installment.dueDate.dateYear()
        val parcel = "${installment.installmentNumber}${getString(R.string.parcel_receive_more_parcel)}"
        val moreInformation = "${installment.installmentAmount.toPtBrRealString()}  ${installment.installmentNumber}ª - $month de $year $status"
        val title = getString(R.string.title_bottom_sheet_details_receive_more, moreInformation)

        text_title_contract?.text = title
        text_date_parcel?.text = getString(R.string.item_parcel_bottom_sheet_details_receive_more, parcel)
        text_date_parcel_value?.text = installment.dueDate.dateFormatToBr()
    }

    private fun setupBtnHelp() {
        btn_help?.setOnClickListener {
            HelpMainActivity.create(requireActivity(), getString(R.string.text_rm_help_title), RM_HELP_ID)
        }
    }
}