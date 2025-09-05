package br.com.mobicare.cielo.recebaRapido.cancellation.selectreason

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.recebaRapido.cancellation.CancelationRRActionListener
import br.com.mobicare.cielo.recebaRapido.cancellation.CancellationRRListener
import br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen.CancelattionRecebaRapidoComparationFragment.Companion.EXTRA_TAX_COMPARATION
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_PLAN
import br.com.mobicare.cielo.taxaPlanos.mapper.ComparationViewModelRR
import kotlinx.android.synthetic.main.fragment_receba_rapido_cancellation_reason.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class RecebaRapidoCancellationReasonFragment : BaseFragment(), CancelationRRActionListener,
        RecebaRapidoCancellationReasonView {

    private var listener: CancellationRRListener? = null
    private val presenter: RecebaRapidoCancellationReasonPresenterImpl by inject {
        parametersOf(this)
    }
    private val ga4: RAGA4 by inject()

    private var planName: String? = ""
    private var reasonToGA = ""
    private var comparationModel: ComparationViewModelRR? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receba_rapido_cancellation_reason, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        planName = arguments?.getString(TAXA_PLANOS_PLAN)
            comparationModel = arguments?.getParcelable(EXTRA_TAX_COMPARATION)

        if (requireActivity() is CancellationRRListener) {
            listener = requireActivity() as CancellationRRListener
            listener?.enableFirstButton(false)
            listener?.setTextButtonDone(getString(R.string.incomint_fast_cancellation_done_button))
            listener?.setTextButtonFinish(getString(R.string.incomint_fast_cancellation_back_button))
            listener?.setActionListener(this)
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<AppCompatRadioButton>(checkedId)
            if (radioButton.isChecked) {
                reasonToGA = radioButton.text.toString()
                gaSelectionReason(reasonToGA)
                listener?.enableFirstButton(true)
            }
        }

        val text = SpannableString(HtmlCompat
                .fromHtml(getString(R.string.incomint_fast_cancellation_reason_info), HtmlCompat.FROM_HTML_MODE_LEGACY))
        textViewInfo.setText(text, TextView.BufferType.SPANNABLE)
    }

    override fun callButtonDone(buttonLabel: String) {
        gaSendReasonAndButton(buttonLabel)
        presenter.callDeleteRecebaRapido()
    }

    override fun callButtonFinish(buttonLabel: String) {
        requireActivity().onBackPressed()
    }

    override fun buttonRetry(buttonLabel: String) {
        gaSendReasonAndButton(buttonLabel)
        presenter.callDeleteRecebaRapido()
    }

    override fun showLoading() {
        listener?.showLoading()
    }

    override fun onCancellationSuccess() {
        gaSendCallbackGenerateLink(null)
        ga4.logRACancel(reasonToGA)
        listener?.showContent()
        findNavController().navigate(RecebaRapidoCancellationReasonFragmentDirections
                .actionRecebaRapidoCancellationReasonFragmentToCancellationRequestedFragment(planName, comparationModel))
    }

    override fun onCacelltionError(error: ErrorMessage?) {
        gaSendCallbackGenerateLink(error)
        ga4.logException(
            screenName = RAGA4.SCREEN_VIEW_RA_CANCEL,
            error = error
        )
        listener?.showError()
    }

    private fun gaSelectionReason(reason: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
            action = listOf(Action.SELECAO, Label.MOTIVO_DO_CANCELAMENTO),
            label = listOf(reason, planName ?: "")
        )
    }

    private fun gaSendReasonAndButton(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
            action = listOf(Action.CLICK, Label.BOTAO),
            label = listOf(labelButton, reasonToGA, planName ?: "")
        )
    }

    private fun gaSendCallbackGenerateLink(error: ErrorMessage?) {
        val labelList = ArrayList<String>()
        if (error != null) {
            labelList.add("Erro")
            labelList.add(error.errorMessage)
            labelList.add(error.httpStatus.toString())
        } else {
            labelList.add("Sucesso")
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
            action = listOf(Action.CALLBACK, Category.CANCELAR_RR, reasonToGA, planName ?: ""),
            label = labelList
        )
    }
}