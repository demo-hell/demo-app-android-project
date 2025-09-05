package br.com.mobicare.cielo.balcaoRecebiveis

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.PARTNER_AUTHORIZATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_OPTIN_PARTNER_AUTHORIZATION
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SEND
import br.com.mobicare.cielo.balcaoRecebiveis.fragment.BalcaoRecebiveisBottomSheetNotKnowledge
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.data.entity.CpfUserAuthorization
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import kotlinx.android.synthetic.main.authorization_activity.btnFollowOfTerm
import kotlinx.android.synthetic.main.authorization_activity.errorHandlerUrl
import kotlinx.android.synthetic.main.authorization_activity.errorLayoutBR
import kotlinx.android.synthetic.main.authorization_activity.errorToggle
import kotlinx.android.synthetic.main.authorization_activity.vf_balcao
import kotlinx.android.synthetic.main.component_br_setinha.layout_not_knowledge
import kotlinx.android.synthetic.main.component_sanfona_cpf.iv_setinha_down
import kotlinx.android.synthetic.main.component_sanfona_cpf.rv_list_cpf_bc
import kotlinx.android.synthetic.main.component_sanfona_cpf.setinha_down
import kotlinx.android.synthetic.main.item_componente_sanfona_cpf.checkBoxSaveCpf
import kotlinx.android.synthetic.main.item_componente_sanfona_cpf.view.checkBoxSaveCpf
import kotlinx.android.synthetic.main.item_componente_sanfona_cpf.view.cpf_vinculado
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author Enzo Teles
 * Thursday, Set 17, 2020
 * */
class AuthorizationActivity : BaseLoggedActivity(), MainBottomNavigationContract.View {


    var cnpj: String? = null
    var bt: BottomSheetFluiGenericFragment? = null
    private val presenter: MainBottomNavigationPresenter by inject {
        parametersOf(this, this)
    }
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    var isClickSetinhaDown = false

    var accountCheckbox = 0
    var termCheckbox = 0
    val listUsercpf = ArrayList<CpfUserAuthorization>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authorization_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_bs_balcao))
        if (FeatureTogglePreference.instance.isActivate(FeatureTogglePreference.BALCAO_RECEBIVEIS)) {
            displayedChild(0)
            presenter.balcaoRecebiveisPermissionRegister()
        } else {
            displayedChild(4)

        }
        initView()
        buttonsClick()
    }

    override fun onResume() {
        super.onResume()
        trackPartnerAuthorizationScreenView()
        presenter.onResume()
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

        checkBoxSaveCpf.setOnCheckedChangeListener { _, selected ->
            if (selected) {
                termCheckbox += 1
                buttonEnable()
            } else {
                termCheckbox -= 1
                buttonDisable()
            }
        }

    }

    /**
     * method to populate the adapter
     * */
    fun populateAdapter() {
        cnpj?.let {

            val cnpjMask = cnpjMaskFormatter(it).formattedText.string

            var cnpj = CpfUserAuthorization(cnpjMask)

            val listCpf = listOf(cnpj)

            rv_list_cpf_bc.layoutManager = LinearLayoutManager(this.baseContext)
            rv_list_cpf_bc?.setHasFixedSize(true)
            val adapter = DefaultViewListAdapter(listCpf, R.layout.item_componente_sanfona_cpf)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<CpfUserAuthorization> {
                override fun onBind(item: CpfUserAuthorization, holder: DefaultViewHolderKotlin) {
                    holder.mView.cpf_vinculado.text = item.cpf
                    holder.mView.checkBoxSaveCpf.isChecked = true
                    accountCheckbox += 1
                    holder.mView.checkBoxSaveCpf.setOnCheckedChangeListener { _, selected ->
                        if (selected) {
                            listUsercpf.add(item)
                            accountCheckbox += 1
                            buttonEnable()
                        } else {
                            listUsercpf.remove(item)
                            accountCheckbox -= 1
                            buttonDisable()
                        }
                    }

                }
            })
            adapter.onItemClickListener =
                object : DefaultViewListAdapter.OnItemClickListener<CpfUserAuthorization> {
                    override fun onItemClick(item: CpfUserAuthorization) {

                    }
                }

            rv_list_cpf_bc.adapter = adapter
        }
    }

    private fun trackPartnerAuthorizationScreenView() {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_OPTIN_PARTNER_AUTHORIZATION
        )
    }

    private fun trackPartnerAuthorizationClick() {
        arvAnalytics.logClick(
            screenName = SCREEN_VIEW_ARV_OPTIN_PARTNER_AUTHORIZATION,
            contentName = SEND,
            contentComponent = PARTNER_AUTHORIZATION
        )
    }

    /**
     * method to manager the click's button
     * */
    private fun buttonsClick() {
        listOpen()
        setinha_down.setOnClickListener {
            if (isClickSetinhaDown.not()) {
                listOpen()
            } else {
                listClose()
            }
        }

        btnFollowOfTerm.setOnClickListener {
            trackPartnerAuthorizationClick()
            sendPermissionRegister()
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

        layout_not_knowledge.setOnClickListener {
            layout_not_knowledge.isEnabled = false
            Handler().postDelayed({
                layout_not_knowledge.isEnabled = true
            }, 500)
            val ftsucessBS = BalcaoRecebiveisBottomSheetNotKnowledge.newInstance()
            ftsucessBS.show(supportFragmentManager, "BalcaoRecebiveisBottomSheetNotKnowledge")
        }

    }

    private fun sendPermissionRegister() {
        displayedChild(0)
        presenter.sendPermisionRegister()
    }

    /**
     * method to verify if the list is close
     * */
    private fun listClose() {
        rv_list_cpf_bc.visibility = View.GONE
        isClickSetinhaDown = false
        iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_down)
    }

    /**
     * method to verify if the list is open
     * */
    private fun listOpen() {
        rv_list_cpf_bc.visibility = View.VISIBLE
        isClickSetinhaDown = true
        iv_setinha_down.setBackgroundResource(R.drawable.ic_setinha_up)
    }

    /**
     * method to verify if the button is disable
     * */
    private fun buttonDisable() {
        if (termCheckbox == 0 || accountCheckbox == 0) {
            btnFollowOfTerm.isEnabled = false
            btnFollowOfTerm.alpha = 0.7f
        }
    }

    /**
     * method to verify if the button is enable
     * */
    private fun buttonEnable() {
        if (termCheckbox != 0 && accountCheckbox != 0) {
            btnFollowOfTerm.isEnabled = true
            btnFollowOfTerm.alpha = 1f
        }
    }

    /**
     * method to manager the view pager of the activity
     * */
    fun displayedChild(value: Int) {
        vf_balcao.displayedChild = value
    }

    override fun onUserInformationsResponse(userInformations: MeResponse?, isImpersonate: Boolean) {
        //not implement
    }

    override fun callOnboardFirstAccess() {
        //not implement
    }

    override fun getContext(): Context {
        //not implement
        return baseContext
    }

    override fun loadBottomNavigationItem(itemIndex: Int) {
        //not implement
    }

    override fun showCancelOnboarding() {
        //not implement
    }

    /**
     * method to show the activity if user is elegible
     * */
    override fun showBannerBalcaoRecebiveis() {
        presenter.balcaoRecebiveisPermissionRegister()
    }

    override fun showMfaOnboarding() {
        //not implement
    }

    /**
     * method to show the banner of the error
     * */
    override fun bannerBalcaoRecebiveisNotElegivel() {
        displayedChild(2)
    }

    /**
     * method to put mask in the cpf field
     * */
    fun cnpjMaskFormatter(inputCpf: String): Mask.Result {
        val cnpjMask = Mask(CNPJ_MASK_FORMAT)
        return cnpjMask.apply(CustomCaretString.forward(inputCpf))
    }

    /**
     * method to show the success banner of the permission register
     * */
    override fun sucessPermissionRegister() {
        bt = bottomSheetGenericFlui(
            getString(R.string.text_registro_recebiveis),
            R.drawable.ic_08,
            getString(R.string.banner_sucess_title),
            getString(R.string.txt_subtitle_chronometer),
            getString(R.string.btn_retornar_painel),
            getString(R.string.meus_recebimentos_dialog_fechar),
            statusSubTitle = false,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_GREEN,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE
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
                supportFragmentManager, getString(R.string.bottom_sheet_generic)
            )
        }
    }

    /**
     * method to show the error banner of the permission register
     * */
    override fun errorPermissionRegister() {
        displayedChild(1)
        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_07,
            getString(R.string.text_title_generic_error),
            getString(R.string.text_message_generic_error),
            EMPTY,
            getString(R.string.text_try_again_label),
            statusBtnClose = false,
            statusBtnFirst = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    sendPermissionRegister()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))

    }

    override fun errorGeneric(error: ErrorMessage?) {
        displayedChild(1)
        bottomSheetGenericFlui(
            getString(R.string.text_registro_recebiveis),
            R.drawable.ic_07,
            getString(R.string.banner_error_title),
            getString(R.string.banner_error_subtitle),
            getString(R.string.btn_retornar_painel),
            getString(R.string.incomint_fast_cancellation_back_button),
            statusBtnClose = false,
            statusBtnFirst = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                }
            }
        }.show(
            supportFragmentManager, getString(R.string.bottom_sheet_generic)
        )

    }

    override fun getDataPermissionRegister(it: MerchantResponseRegisterGet) {
        it.document?.let {
            cnpj = it
        }
        populateAdapter()
        displayedChild(1)

    }

    override fun erroUrlEligible(error: ErrorMessage?) {
        displayedChild(3)
    }

    override fun getAuthorizationHistory(it: MerchantResponseRegisterGet?) {
        startActivity<AuthorizationHistoryActivity>("merchantGet" to it)
        finish()
    }

    override fun bannerDebitoContaEligible() {
        //not implement
    }

    override fun onLogout() {
        baseLogout()
    }
}