package br.com.mobicare.cielo.recebaMais.presentation.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.extensions.hasIndex
import br.com.mobicare.cielo.main.presentation.getScreenDimension
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS_BOTTOM_SHEET_SCREEN
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.presentation.ui.MyDataContract
import br.com.mobicare.cielo.recebaMais.presentation.ui.presenter.UserLoanDataPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_common_fullscreen.*
import kotlinx.android.synthetic.main.layout_modal_receba_mais.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class RecebaMaisBottomSheetFragment : BottomSheetDialogFragment(), MyDataContract.View {

    private val offer: Offer? by lazy {
        arguments?.getParcelable<Offer?>(RECEBA_MAIS_OFFER)
    }

    private val presenter: UserLoanDataPresenter by inject { parametersOf(this) }

    var creditSimulationListener: CreditSimulationListener? = null

    interface CreditSimulationListener {
        fun onStartCreditSimulation(offer: Offer?)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_modal_receba_mais, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        presenter.setView(this)
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
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

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserPreferences.getInstance().saveRecebaMaisChecked(true)

        cb_dinheiro_uteis.isEnabled = false;
        cb_pagamento_recebiveis.isEnabled = false;
        cb_taxas_diferenciados.isEnabled = false;

        offer?.run {

            txt_welcome_title.text = title
            txt_welcome_subtitle.text = configureSubtitle()

            if (steps.hasIndex(0)){
                cb_dinheiro_uteis.text = steps!![0]
            }

            if (steps.hasIndex(1)){
                cb_pagamento_recebiveis.text = steps!![1]
            }

            if (steps.hasIndex(2)){
                cb_taxas_diferenciados.text = steps!![2]
            }
        }

        btn_rm_close.setOnClickListener {
            dismiss()
        }

        btn_rm_ok.setOnClickListener {

            gaEventButton()
            offer?.run {

                creditSimulationListener?.onStartCreditSimulation(this)

                this.id?.let { id ->
                    presenter.keepInterestOffer(id)
                }
                dismiss()
            }

        }

        val sizeScreen = requireActivity().getScreenDimension()
        when {
            (sizeScreen[1]!!.toInt() in 800..1183) -> {
                txt_welcome_title.textSize = 14f
                txt_welcome_subtitle.textSize = 14f
            }
        }

        Analytics.trackScreenView(
            screenName = GA_RM_RECEBA_MAIS_BOTTOM_SHEET_SCREEN,
            screenClass = this.javaClass
        )
    }

    companion object {

        const val RECEBA_MAIS_OFFER = "br.com.cielo.recebaMais.offer"

        fun newInstance(offer: Offer): RecebaMaisBottomSheetFragment {

            val recebaMaisBottomSheetFragment = RecebaMaisBottomSheetFragment()
            recebaMaisBottomSheetFragment.addArgument(RECEBA_MAIS_OFFER, offer)

            return recebaMaisBottomSheetFragment
        }
    }

    private fun configureSubtitle(): SpannableStringBuilder {


        val text = SpannableStringBuilder()
        val subtitle = offer?.description

        if (subtitle.isNullOrEmpty().not()) {
            val offset: Int = subtitle?.indexOf("R$") ?: 0

            val firstPart= subtitle?.substring(0, offset)
            val valor= subtitle?.substring(offset, subtitle.length)

            text.append(firstPart?.addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextGrayLight13sp500_rb)))
            text.append(valor?.addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextGrayLight13sp500_bold_rb)))
        }

        return text
    }

    override fun isAttached(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun gaEventButton(){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.MODAL),
            label = listOf(Label.BOTAO, getString(R.string.ga_simular_agora))
        )
    }
}