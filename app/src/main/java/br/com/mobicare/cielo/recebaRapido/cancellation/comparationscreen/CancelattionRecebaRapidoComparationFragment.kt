package br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.recebaRapido.cancellation.CancelationRRActionListener
import br.com.mobicare.cielo.recebaRapido.cancellation.CancellationRRListener
import br.com.mobicare.cielo.taxaPlanos.mapper.ComparationViewModelRR
import kotlinx.android.synthetic.main.fragment_cancelattion_receba_rapido_comparation.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CancelattionRecebaRapidoComparationFragment : BaseFragment(), CancelattionRecebaRapidoComparationView,
        CancelationRRActionListener {

    private val presenter: CancelattionRecebaRapidoComparationPresenterImpl by inject {
        parametersOf(this)
    }

    private var planName: String? = ""
    private var listener: CancellationRRListener? = null
    private var comparationModel: ComparationViewModelRR? = null

    companion object {
        const val EXTRA_TAX_COMPARATION = "EXTRA_TAX_COMPARATION"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cancelattion_receba_rapido_comparation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (requireActivity() is CancellationRRListener) {
            listener = requireActivity() as CancellationRRListener
            listener?.enableFirstButton(true)
            listener?.setTextButtonDone(getString(R.string.incomint_fast_cancellation_yes_cancel))
            listener?.setTextButtonFinish(getString(R.string.incomint_fast_cancellation_keep_rr))
            listener?.setActionListener(this)
        }

        planName = listener?.getPlanName()
        presenter.getTaxAndBrand()
    }

    override fun callButtonDone(buttonLabel: String) {
        gaSendButton(buttonLabel)
        findNavController()
                .navigate(CancelattionRecebaRapidoComparationFragmentDirections
                        .actionCancelattionRecebaRapidoComparationFragmentToRecebaRapidoCancellationReasonFragment(planName, comparationModel))
    }

    override fun callButtonFinish(buttonLabel: String) {
        requireActivity().finish()
    }

    override fun buttonRetry(buttonLabel: String) {
        presenter.getTaxAndBrand()
    }

    override fun onTaxAndBrandSuccess(comparationModel: ComparationViewModelRR) {
        this.comparationModel = comparationModel
        if (!comparationModel.listWithRR.isNullOrEmpty()) {
            componentFeeWithRR
                    .setContent(getString(R.string.incomint_fast_cancellation_with_rr_title),
                            comparationModel.listWithRR)
            componentFeeWithRR.visible()
        }
        componentFeeWithoutRR
                .setContent(getString(R.string.incomint_fast_cancellation_without_rr_title),
                        comparationModel.listWithoutRR)
        listener?.showContent()

    }

    override fun onTaxAndBrandError() {
        listener?.showError()
    }

    override fun showLoading() {
        listener?.showLoading()
    }

    private fun gaSendButton(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
            action = listOf(Action.CLICK, Label.BOTAO),
            label = listOf(labelButton, planName ?: "")
        )
    }
}