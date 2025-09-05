package br.com.mobicare.cielo.recebaRapido.cancellation.requested

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.extensions.hasIndex
import br.com.mobicare.cielo.recebaRapido.cancellation.CancelationRRActionListener
import br.com.mobicare.cielo.recebaRapido.cancellation.CancellationRRListener
import br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen.CancelattionRecebaRapidoComparationFragment
import br.com.mobicare.cielo.taxaPlanos.mapper.ComparationViewModelRR
import kotlinx.android.synthetic.main.fragment_cancellation_requested.*

class CancellationRequestedFragment : BaseFragment(), CancelationRRActionListener {

    var listener: CancellationRRListener? = null
    private var comparationModel: ComparationViewModelRR? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cancellation_requested, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        configureListener()
    }

    private fun init() {
        comparationModel = arguments?.getParcelable(CancelattionRecebaRapidoComparationFragment.EXTRA_TAX_COMPARATION)

        val list = ArrayList<ComponentLayoutFeeAndPlansItem>()

        comparationModel?.listWithoutRequestedScreen?.let {
            if (it.hasIndex(DEBIT_INDEX)) {
                val debit = ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_debit_label),
                    it[DEBIT_INDEX].labelValue
                )
                debit.labelValueColor = R.color.colorPrimary
                list.add(debit)
            }

            if (it.hasIndex(CREDIT_INDEX)) {
                val credit = ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_credit_on_cash_label),
                    it[CREDIT_INDEX].labelValue
                )
                credit.labelValueColor = R.color.colorPrimary
                list.add(credit)
            }

            if (it.hasIndex(INSTALLMENT_INDEX)) {
                val installment = ComponentLayoutFeeAndPlansItem(
                    getString(R.string.text_view_instalment_with_artherisc_label),
                    it[INSTALLMENT_INDEX].labelValue
                )
                installment.labelSubTitle =
                    getString(R.string.incomint_fast_cancellation_subtitle_item)
                installment.labelValueColor = R.color.colorPrimary
                list.add(installment)
            }
        }
    }

    private fun configureListener() {
        if (requireActivity() is CancellationRRListener) {
            listener = requireActivity() as CancellationRRListener
            listener?.setActionListener(this)
            listener?.showButtonDone(false)
            listener?.showToolbarBackButton(false)
            listener?.setTextButtonFinish(getString(R.string.incomint_fast_cancellation_finish_button))
        }
    }

    override fun callButtonFinish(buttonLabel: String) {
        requireActivity().finish()
    }

    companion object {
        private const val DEBIT_INDEX = 0
        private const val CREDIT_INDEX = 1
        private const val INSTALLMENT_INDEX = 2
    }
}