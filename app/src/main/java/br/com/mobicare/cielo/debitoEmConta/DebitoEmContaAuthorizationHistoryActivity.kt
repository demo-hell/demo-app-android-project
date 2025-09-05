package br.com.mobicare.cielo.debitoEmConta

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.debitoEmConta.DebitoEmContaAuthorizationActivity.Companion.GET_EXTRA_DC
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.REMOVE_AUTHORIZATION
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATIONS
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE_SUCCESS
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.merchant.domain.entity.ACTIVE
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import kotlinx.android.synthetic.main.debito_em_conta_authorization_history.*
import kotlinx.android.synthetic.main.item_estabelecimento_detail.*
import kotlinx.android.synthetic.main.layout_botao_remove_e_informacao_dc.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author Enzo Teles
 * Sunday, Nov 25, 2020
 * */
class DebitoEmContaAuthorizationHistoryActivity : BaseLoggedActivity(), MainBottomNavigationContract.View {

    val ga4: DebitAccountGA4 by inject()
    var bt: BottomSheetFluiGenericFragment? = null
    private val presenter: MainBottomNavigationPresenter by inject {
        parametersOf(this, this)
    }

    companion object{
        const val REMOVIDO = "Removido"
        const val CONCLUIDO = "Concluído"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debito_em_conta_authorization_history)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_dc))
        initView()
    }

    fun initView(){
        displayedChild(1)
        intent?.extras?.let {
            populateCard(it.getParcelable(GET_EXTRA_DC))
        }
        layout_botao_remove_dc.setOnClickListener {

            ga4.click(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT, REMOVE_AUTHORIZATION)
            CieloAskQuestionDialogFragment
                .Builder()
                .title(getString(R.string.dialog_title_dc))
                .message(getString(R.string.dialog_msg_dc))
                .cancelTextButton(getString(R.string.btn_cancelar))
                .positiveTextButton(getString(R.string.btn_remover))
                .build().let {
                    it.onCancelButtonClickListener = View.OnClickListener {
                        //dimmiss
                    }
                    it.onPositiveButtonClickListener = View.OnClickListener {
                        displayedChild(0)
                        presenter.sendDebitoContaPermission(DebitoEmContaAuthorizationActivity.OPTOUT)
                    }
                    it.show(this.supportFragmentManager, CieloAskQuestionDialogFragment::class.java.simpleName)
                    ga4.logScreenView(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE)
                }
        }
    }

     private fun populateCard(responseDC: ResponseDebitoContaEligible?){

         responseDC?.document?.let { document ->
             val cpfOrCpnj = Utils.unmask(document)
             if(cpfOrCpnj.length > 13){
                 txt_document_dc_value.text = addMaskCPForCNPJ(
                     cpfOrCpnj,
                     getString(R.string.mask_cnpj_step4)
                 )
             }else{
                 txt_document_dc_value.text = addMaskCPForCNPJ(
                     cpfOrCpnj,
                     getString(R.string.mask_cpf_step4)
                 )
             }
         } ?: run {
             txt_document_dc_value.text = "-"
         }

         when(responseDC?.status){
             ACTIVE -> {
                 txt_status_dc_value.text = CONCLUIDO
                 txt_status_dc_value.setTextColor(ContextCompat.getColor(this@DebitoEmContaAuthorizationHistoryActivity, R.color.color_009e55))
             }
             else -> {
                 txt_status_dc_value.text = REMOVIDO
                 txt_status_dc_value.setTextColor(ContextCompat.getColor(this@DebitoEmContaAuthorizationHistoryActivity, R.color.red_DC392A))
             }
         }
    }

    /**
     * method to manager the view fliper of the activity
     * */
    fun displayedChild(value: Int) {
        vf_history_dc.displayedChild = value
    }

    override fun getContext(): Context {
        return baseContext
    }


    override fun erroUrlEligible(errorMessage: ErrorMessage?) {
        displayedChild(3)
        ga4.logException(SCREEN_NAME_OTHERS_AUTHORIZATIONS, errorMessage)
    }

    override fun showBannerDebitoEmContaActive() {
        ga4.logScreenView(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT_REMOVE_SUCCESS)
        bt = bottomSheetGenericFlui(
            getString(R.string.text_debito_conta),
            R.drawable.ic_08,
            getString(R.string.banner_sucess_remocao_title_dc),
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

    override fun onLogout() {
        baseLogout()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_NAME_OTHERS_AUTHORIZATION_DEBIT_ACCOUNT)
    }
}