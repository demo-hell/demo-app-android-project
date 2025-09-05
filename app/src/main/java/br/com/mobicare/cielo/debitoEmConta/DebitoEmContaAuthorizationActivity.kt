package br.com.mobicare.cielo.debitoEmConta

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Label.SEND
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATIONS
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_TERMS_OF_USE
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import kotlinx.android.synthetic.main.component_dc_checkbox.*
import kotlinx.android.synthetic.main.component_dc_setinha.*
import kotlinx.android.synthetic.main.debito_em_conta_authorization_activity.*
import kotlinx.android.synthetic.main.layout_debito_conta_document.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author Enzo Teles
 * Thursday, Nov 20, 2020
 * */
class DebitoEmContaAuthorizationActivity : BaseLoggedActivity(), MainBottomNavigationContract.View {


    companion object{
        const val OPTIN = "OPTIN"
        const val OPTOUT = "OPTOUT"
        const val GET_EXTRA_DC = "responseDC"
    }

    private val ga4: DebitAccountGA4 by inject()
    var bt: BottomSheetFluiGenericFragment? = null
    private val presenter: MainBottomNavigationPresenter by inject {
        parametersOf(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debito_em_conta_authorization_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_dc))
        var isToggle = FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.DEBITO_EM_CONTA)
        if (isToggle) {
            displayedChild(0)
            presenter.debitoEmContaElegibility()
        } else {
            displayedChild(4)

        }
        initView()
        buttonsClick()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        ga4.logScreenView(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_TERMS_OF_USE)
    }

    override fun onPause() {
        super.onPause()
        presenter.onDestroy()
    }

    /**
     * method to init the widget of the view
     * */
    fun initView() {
        buttonDisable()

        checkBoxTerm.setOnCheckedChangeListener { _, selected ->
            if (selected) {
                buttonEnable()
            } else {
                buttonDisable()
            }
        }

    }


    /**
     * method to manager the click's button
     * */
    fun buttonsClick() {
        btnFollowOfTermDC.setOnClickListener {
            displayedChild(0)
            presenter.sendDebitoContaPermission(OPTIN)
            ga4.click(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_TERMS_OF_USE, SEND)
         }
        errorLayoutBR.configureActionClickListener(View.OnClickListener {
            finish()
        })

        errorToggle.configureActionClickListener(View.OnClickListener {
            finish()
        })

        errorHandlerUrl.configureActionClickListener(View.OnClickListener {
            finish()
        })

        layout_information.setOnClickListener {
            layout_information.isEnabled = false
            Handler().postDelayed({
                //doSomethingHere()
                layout_information.isEnabled = true
            }, 500)
            val ftsucessBS = DebitoEmContaTermoInformationBottomSheet.newInstance()
            ftsucessBS.show(supportFragmentManager, "DebitoEmContaTermoInformationBottomSheet")
        }

    }

    /**
     * method to verify if the button is disable
     * */
    private fun buttonDisable() {
        btnFollowOfTermDC.isEnabled = false
        btnFollowOfTermDC.alpha = 0.7f
    }

    /**
     * method to verify if the button is enable
     * */
    private fun buttonEnable() {
        btnFollowOfTermDC.isEnabled = true
        btnFollowOfTermDC.alpha = 1f
    }

    /**
     * method to manager the view pager of the activity
     * */
    fun displayedChild(value: Int) {
        vf_balcao.displayedChild = value
    }

    /**
     * method to show the success in the authorization
     * */
    fun bannerSuccessAuthorization() {
        bt = bottomSheetGenericFlui(
            getString(R.string.text_debito_conta),
            R.drawable.ic_05,
            getString(R.string.banner_sucess_title_dc),
            getString(R.string.txt_subtitle_chronometer),
            getString(R.string.btn_retornar_painel),
            getString(R.string.meus_recebimentos_dialog_fechar),
            true,
            true,
            false,
            true,
            false,
            false,
            false,
            true,
            false,
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            TxtTitleStyle.TXT_TITLE_BLUE,
            TxtSubTitleStyle.TXT_SUBTITLE_GREEN,
            ButtonBottomStyle.BNT_BOTTOM_WHITE,
            ButtonBottomStyle.BNT_BOTTOM_BLUE,
            false
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    finish()
                }
            }
        }
        bt?.let {
            it.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    /**
     * method to show the error banner of the permission register
     * */
    fun bannerErroAuthorization() {
        displayedChild(1)
        bt = bottomSheetGenericFlui(
            getString(R.string.text_debito_conta),
            R.drawable.ic_01,
            getString(R.string.banner_error_title_dc),
            getString(R.string.banner_error_subtitle_dc),
            getString(R.string.btn_retornar_painel),
            getString(R.string.incomint_fast_cancellation_back_button),
            true,
            true,
            true,
            true,
            false,
            false,
            true,
            true,
            false,
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            TxtTitleStyle.TXT_TITLE_BLUE,
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            ButtonBottomStyle.BNT_BOTTOM_WHITE,
            ButtonBottomStyle.BNT_BOTTOM_BLUE,
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                }
            }
        }
        bt?.let {
            it.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    override fun getContext(): Context {
        return baseContext
    }

    override fun showBannerDebitoEmConta(response: ResponseDebitoContaEligible) {
        displayedChild(1)
        response.document?.let {
            tv_document_dc.text = configureTitle(it)
        }


    }

    override fun errorGeneric(error: ErrorMessage?) {
        progress_balcao?.gone()
        errorLayoutBR?.visible()
        content?.gone()
        ga4.logException(SCREEN_NAME_OTHERS_AUTHORIZATIONS, error)
    }

    override fun showBannerDebitoEmContaActive() {
        bt = bottomSheetGenericFlui(
            getString(R.string.text_debito_conta),
            R.drawable.ic_08,
            getString(R.string.banner_sucess_title_dc),
            getString(R.string.txt_subtitle_chronometer),
            getString(R.string.btn_retornar_painel),
            getString(R.string.meus_recebimentos_dialog_fechar),
            false,
            true,
            false,
            true,
            false,
            false,
            true,
            true,
            false,
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            TxtTitleStyle.TXT_TITLE_BLUE,
            TxtSubTitleStyle.TXT_SUBTITLE_GREEN,
            ButtonBottomStyle.BNT_BOTTOM_WHITE,
            ButtonBottomStyle.BNT_BOTTOM_BLUE,
            false
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    finish()
                }
                override fun onSwipeClosed() {
                    finish()
                }
            }
        }
        bt?.let {
            it.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    override fun showBannerDebitoEmContaWaiting(it: ResponseDebitoContaEligible?) {
        bt = bottomSheetGenericFlui(
            getString(R.string.text_registro_recebiveis),
            R.drawable.ic_12,
            getString(R.string.txt_bn_title_watting),
            getString(R.string.txt_bn_subtitle_watting),
            getString(R.string.btn_retornar_painel),
            getString(R.string.text_close),
            false,
            true,
            true,
            true,
            false,
            false,
            true,
            true,
            false,
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            TxtTitleStyle.TXT_TITLE_BLUE,
            TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            ButtonBottomStyle.BNT_BOTTOM_WHITE,
            ButtonBottomStyle.BNT_BOTTOM_BLUE,
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    finish()
                }
                override fun onSwipeClosed() {
                    finish()
                }
            }
        }
        bt?.let {
            it.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    private fun configureTitle(document: String): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append(
            getString(R.string.document_dc)
                .addSpannable(
                    TextAppearanceSpan(
                        this@DebitoEmContaAuthorizationActivity,
                        R.style.Paragraph_400_bold_14_display_400
                    )
                )
        )
        text.append(" ")

        text.append(
            document
                .addSpannable(
                    TextAppearanceSpan(
                        this@DebitoEmContaAuthorizationActivity,
                        R.style.Paragraph_300_display_400
                    )
                )
        )
        return text
    }

    override fun resultSearchDebitoEmContaActive(it: ResponseDebitoContaEligible) {
        startActivity<DebitoEmContaAuthorizationHistoryActivity>(GET_EXTRA_DC to it)
        finish()
    }

    override fun onLogout() {
        baseLogout()
    }
}