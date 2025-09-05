package br.com.mobicare.cielo.recebaMais.presentation.ui.dialog

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.recebaMais.BUTTON_DETALHES
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS_SUCCESS_SCREEN
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.Contract
import br.com.mobicare.cielo.recebaMais.presentation.ui.MyDataContract
import br.com.mobicare.cielo.recebaMais.presentation.ui.presenter.UserLoanDataPresenter
import kotlinx.android.synthetic.main.fragment_receba_mais_success.*
import kotlinx.android.synthetic.main.toolbar_main.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class RecebaMaisSuccessDialog internal constructor(): DialogFragment(), MyDataContract.View  {

    private val presenter: UserLoanDataPresenter by inject { parametersOf(this) }

    private var openRecebaMais: (() -> Unit)? = null
    companion object {
        fun create(openRecebaMais: () -> Unit) : RecebaMaisSuccessDialog {
            return RecebaMaisSuccessDialog().apply {
                this.openRecebaMais = openRecebaMais
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        presenter.setView(this)
        return inflater.inflate(R.layout.fragment_receba_mais_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        configureButtonSeeCredit()
        configureButtonClose()

        toolbarUserLoanSuccess?.textToolbarMainTitle?.text =
                getString(R.string.text_receba_mais_title)

        sendGaScreenView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen)
    }

    override fun onStart() {
        super.onStart()

        dialog?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT

            this.window?.setLayout(width, height)
            window?.attributes?.windowAnimations = R.style.dialog_animation
        }
    }

    private fun configureButtonSeeCredit() {
        errorHandlerCieloUserLoanSuccess?.errorButton?.setOnClickListener {
            sendGaSeeMyCredit()
            presenter.resumoContract()
            this.dismiss()
        }
    }

    private fun configureButtonClose() {
        errorHandlerCieloUserLoanSuccess?.errorButton?.setOnClickListener {
            openRecebaMais?.invoke()
            dismiss()
        }
    }

//    override fun start() {
//        super.start()
//        val value = arguments!!.getString(VALUE_NUMBER)
//        textSuccessDescription_01.text =  configureSubtitle(value)
//    }

    private fun configureSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append(getString(R.string.rm_ss_text_1_1)
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextWhite16spTitleValue_500)))

        text.append(" ")

        text.append(getString(R.string.rm_ss_text_1_2).addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextWhite16spTitleValue_900)))

        text.append(" ")

        text.append(getString(R.string.rm_ss_text_1_3)
                .addSpannable(TextAppearanceSpan(requireActivity(), R.style.TextWhite16spTitleValue_500)))

        return text
    }

    override fun merchantSuccess(email: String, phone: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun banksSuccess(banksResponse: BanksResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAttached(): Boolean {
        return isAdded && activity != null && view != null
    }

    override fun sucessSummary(contracts: List<Contract>) {

        if(!isAdded){
            openRecebaMais?.invoke()
            dismiss()
            //MyResumeFragment.create(contracts[0]).addWithAnimation(requireActivity().supportFragmentManager, R.id.frameFormContentInput, false)
        }
    }

    //region Event Ga

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_RECEBA_MAIS_SUCCESS_SCREEN,
            screenClass = this.javaClass
        )
    }

    private fun sendGaSeeMyCredit() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.MODAL),
            label = listOf(Label.BOTAO, BUTTON_DETALHES)
        )
    }

    //endregion

}