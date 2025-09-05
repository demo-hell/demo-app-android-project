package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaDefaultObj
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.Manager
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.PhoneSupport
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.OnClickPhoneListener
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters.ContentTelefonesAdapter
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.presentation.utils.WebviewActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.MenuItemLayout
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.createForgetUserDialog
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.activities.EsqueciUsuarioAndEstabelecimentoActivity
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.precisoDeAjuda.presentation.ui.activities.PrecisoAjudaActivity
import br.com.mobicare.cielo.suporteTecnico.ui.activity.TechnicalSupportActivity
import kotlinx.android.synthetic.main.central_ajuda_fragment.*
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.content_gestor_comercial.view.*
import kotlinx.android.synthetic.main.content_header_ajuda.*
import kotlinx.android.synthetic.main.content_telefones_uteis.view.*
import kotlinx.android.synthetic.main.layout_menu_item.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CentralAjudaFragment : BaseFragment(), CentralAjudaContract.View, OnClickPhoneListener {

    private val mPresenter: CentralAjudaContract.Presenter by inject { parametersOf(this) }

    private var SCREEN_NAME = "CentralDeAjuda"

    var screen: String? = null
    var merchantId: String? = null

    var isEstablishment: Boolean? = false

    companion object {
        @JvmField
        val ESTABLISHMENT = "establishment"

        @JvmField
        val CPF = "cpf"
        const val PRECISA_AJUDA = "PrecisaDeAjuda"
        const val SCREEN_NAME = "SCREEN_NAME"
        const val MERCHANT_ID = "merchantId"


        fun create(screenPath: String, merchantId: String = ""): CentralAjudaFragment {

            val centralAjudaFragment = CentralAjudaFragment()

            val params = Bundle()
            params.putString(SCREEN_NAME, screenPath)
            params.putString(MERCHANT_ID, merchantId)

            centralAjudaFragment.arguments = params

            return centralAjudaFragment
        }

    }


    private val technicalSupportEnabled
        get() = FeatureTogglePreference.instance
            .getFeatureTogle(FeatureTogglePreference.SUPORTE_TECNICO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.central_ajuda_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideGestor()
        mPresenter.callAPI()

        arguments?.apply {
            isEstablishment = this.getBoolean(ESTABLISHMENT, false)
            screen = this.getString(CentralAjudaFragment.SCREEN_NAME)
            merchantId = this.getString(MERCHANT_ID)
        }

        Analytics.trackScreenView(
            screenName = getString(R.string.label_ga_central_ajuda),
            screenClass = this.javaClass
        )
    }

    override fun showContent(ajuda: CentralAjudaObj) {
        loadPhoneSupport(ajuda.phonesSupport)
        loadHeader(ajuda)
        managerGestor(ajuda.manager)
        scrollToBegin()

        layout_central_ajuda.visibility = View.VISIBLE

        if (technicalSupportEnabled) {
            showTechnicalSupport()
        } else {
            hideTechnicalSupport()
        }

    }

    fun managerGestor(manager: Manager?) {
        if (manager == null) {
            hideGestor()
        } else {
            showGestor(manager)
        }
    }

    private fun showTechnicalSupport() {

        if (menuItemLayoutTechnicalSupport.visibility != View.VISIBLE) {
            menuItemLayoutTechnicalSupport.visibility = View.VISIBLE
        }
    }

    private fun hideTechnicalSupport() {
        menuItemLayoutTechnicalSupport.visibility = View.GONE
    }

    override fun hideContent() {
        layout_central_ajuda.visibility = View.GONE
    }

    override fun showProgress() {
        progress_central_ajuda_loading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_central_ajuda_loading.visibility = View.GONE
    }

    override fun loadPhoneSupport(phones: List<PhoneSupport>) {
        content_central_ajuda_telefones_uteis.recycler_view_ajuda_telefones_uteis.layoutManager =
            LinearLayoutManager(requireContext())
        content_central_ajuda_telefones_uteis.recycler_view_ajuda_telefones_uteis.adapter =
            ContentTelefonesAdapter(requireContext(), this, phones)
    }

    override fun showGestor(manager: Manager) {
        content_central_ajuda_gestor_comercial.layout_central_ajuda_gestor.visibility = View.VISIBLE
        content_central_ajuda_gestor_comercial.textview_ajuda_gestor_nome.text = manager.name
        content_central_ajuda_gestor_comercial.textview_ajuda_gestor_telefone.text =
            manager.phoneFormatted
        content_central_ajuda_gestor_comercial.textview_ajuda_gestor_email.text =
            manager.emailFormatted
        content_central_ajuda_gestor_comercial.button_central_ajuda_email.setOnClickListener {

//            val gaLabel = arrayOf(getString(R.string.central_ajuda_meu_gestor),
//                    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, button_central_ajuda_email.text.toString()))
//                    .joinToString(" | ")

//            Analytics.trackEvent(
//                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
//                action = listOf(Action.CENTRAL_DE_AJUDA),
//                label = listOf(gaLabel)
//            )

            sendEmailGestor(manager)
        }

        content_central_ajuda_gestor_comercial.button_central_ajuda_ligar.setOnClickListener {

//            val gaLabel = arrayOf(getString(R.string.central_ajuda_meu_gestor),
//                    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,
//                            button_central_ajuda_ligar.text.toString()))
//                    .joinToString(" | ")

//            Analytics.trackEvent(
//                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
//                action = listOf(Action.CENTRAL_DE_AJUDA),
//                label = listOf(gaLabel)
//            )

            callGestor(manager)
        }
    }

    fun sendEmailGestor(manager: Manager) {
        AlertDialogCustom.Builder(requireContext(), requireContext().getString(R.string.ga_ajuda))
            .setTitle(getString(R.string.ajuda_msg_enviar_email))
            .setMessage("${manager.email}?")
            .setBtnRight(getString(R.string.ok))
            .setBtnLeft(getString(R.string.cancelar))
            .setTaguear(false)
            .setOnclickListenerRight { Utils.sendEmail(requireActivity(), manager.email) }
            .setOnclickListenerLeft(View.OnClickListener { return@OnClickListener })
            .show()
    }

    fun callGestor(manager: Manager) {
        AlertDialogCustom.Builder(context, getString(R.string.ga_ajuda))
            .setTitle(getString(R.string.ajuda_msg_ligar))
            .setMessage("${manager.phone}?")
            .setBtnRight(getString(R.string.ok))
            .setBtnLeft(getString(R.string.cancelar))
            .setOnclickListenerRight { Utils.callPhone(requireActivity(), manager.phone) }
            .setOnclickListenerLeft(View.OnClickListener { return@OnClickListener })
            .show()
    }

    override fun hideGestor() {
        content_central_ajuda_gestor_comercial.layout_central_ajuda_gestor.visibility = View.GONE
    }

    fun configureMenuItem(
        menuItemLayout: MenuItemLayout,
        centralAjudaObj: CentralAjudaDefaultObj,
        onClickListener: View.OnClickListener
    ) {

        menuItemLayout.visibility = View.VISIBLE
        menuItemLayout.textMenuContent.text = centralAjudaObj.title
        menuItemLayout.relativeMenuContent.setOnClickListener(onClickListener)
    }

    override fun loadHeader(obj: CentralAjudaObj) {

        obj.merchant?.let { merchant ->
            configureMenuItem(menuItemMerchant, merchant) {
                sendGaButton(menuItemMerchant.textMenuContent.text.toString())
                showAlert(menuItemMerchant.textMenuContent.text.toString())
            }
        }

        obj.user?.let { user ->
            configureMenuItem(menuItemUser, user) {
                sendGaButton(menuItemUser.textMenuContent.text.toString())
                startForgetUserAndEstablishiment()
            }
        }

        obj.faq?.let {
            configureMenuItem(menuItemGeneralQuestions, it) {
                sendGaButton(menuItemGeneralQuestions.textMenuContent.text.toString())

                if (Utils.isNetworkAvailable(requireActivity())) {
                    showWebview(obj.faq.title, obj.faq.value)
                } else {
                    requireContext().showMessage(
                        getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title)
                    )
                }

            }
        }

        obj.technicalSupport?.let {
            configureMenuItem(
                menuItemLayoutTechnicalSupport, it
            ) {
                sendGaButton(menuItemLayoutTechnicalSupport.textMenuContent.text.toString())
                if (Utils.isNetworkAvailable(requireActivity())) {

                    val startIntent = Intent(activity, TechnicalSupportActivity::class.java)
                    startActivity(startIntent)
                } else {
                    requireContext().showMessage(
                        getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title)
                    )
                }
            }
        }
    }

    private fun startForgetUserAndEstablishiment() {
        requireActivity().startActivity<EsqueciUsuarioAndEstabelecimentoActivity>(
            PrecisoAjudaActivity.ESTABLISHMENT to false,
            PrecisoAjudaActivity.SCREEN_NAME to screen,
            PrecisoAjudaActivity.FIELD_EC to UserPreferences
                .getInstance().numeroEC,
            PrecisoAjudaActivity.TOOLBAR_TITLE to menuItemUser.textMenuContent
                .text.toString(),
            PrecisoAjudaActivity.BACK_TO to screenPath(),
            PrecisoAjudaActivity.FIELD_USUARIO to
                    UserPreferences.getInstance().userName,
            PrecisoAjudaActivity.CPF to true
        )
    }

    private fun screenPath() = "$screen/$PRECISA_AJUDA"


    override fun showError(error: String) {
        hideContent()
        linearHelpCenterError.visibility = View.VISIBLE
        text_view_error_msg.text = error
        onClickTryAgain()
    }


    private fun onClickTryAgain() {
        button_error_try.setOnClickListener {
            this.hideContent()
            linearHelpCenterError.visibility = View.GONE
            mPresenter.callAPI()
        }
    }

    override fun onClickPhone(gaScreenName: String?, gaLabel: String?, phone: String?) {
        //requireActivity().tapLabel(screen,arrayOf(gaScreenName, gaLabel, phone).joinToString(" | "))
        phone?.let { sendGaPhone(it) }
        Utils.callPhone(requireActivity(), phone)
    }

    private fun showWebview(title: String?, url: String?) {
        val intent = Intent(activity, WebviewActivity::class.java)
        intent.putExtra(WebviewActivity.SCREEN_NAME_FIREBASE, "/duvidas-gerais")
        intent.putExtra(WebviewActivity.TITLE, title)
        intent.putExtra(WebviewActivity.URL, url)
        intent.putExtra(WebviewActivity.SCREEN_NAME, screenPath())
        startActivity(intent)
    }

    fun showAlert(title: String) {
        requireActivity().createForgetUserDialog(screenPath()) {
            configureHelpScreenContent(it, title)
        }
    }

    private fun configureHelpScreenContent(personType: Int, title: String) {
        when (personType) {
            R.id.radio_button_ajuda_alert_pessoa_fisica -> showForgetNaturalUserAndEstablishiment(
                title
            )
            R.id.radio_button_ajuda_alert_pessoa_juridica -> showForgetLegalUserAndEstablishment(
                title
            )
        }
    }

    override fun showForgetNaturalUserAndEstablishiment(title: String) {
        if (this.isAttached()) {
            requireActivity().startActivity<EsqueciUsuarioAndEstabelecimentoActivity>(
                PrecisoAjudaActivity.ESTABLISHMENT to true,
                PrecisoAjudaActivity.CPF to true,
                PrecisoAjudaActivity.SCREEN_NAME to screen,
                PrecisoAjudaActivity.TOOLBAR_TITLE to title,
                PrecisoAjudaActivity.FIELD_EC to UserPreferences.getInstance().numeroEC,
                PrecisoAjudaActivity.BACK_TO to screenPath(),
                PrecisoAjudaActivity.FIELD_USUARIO to UserPreferences.getInstance().userName
            )
        }
    }

    override fun showForgetLegalUserAndEstablishment(title: String) {
        if (this.isAttached()) {
            requireActivity().startActivity<EsqueciUsuarioAndEstabelecimentoActivity>(
                PrecisoAjudaActivity.ESTABLISHMENT to true,
                PrecisoAjudaActivity.CPF to false,
                PrecisoAjudaActivity.TOOLBAR_TITLE to title,
                PrecisoAjudaActivity.BACK_TO to screenPath(),
                PrecisoAjudaActivity.FIELD_EC to UserPreferences.getInstance().numeroEC,
                PrecisoAjudaActivity.SCREEN_NAME to screen
            )
        }
    }

    fun scrollToBegin() {
        scroll_central_ajuda.fullScroll(View.FOCUS_UP)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }


    //region GaFirebase

    private fun sendGaButton(label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "central de ajuda"),
            action = listOf(Action.BOTAO),
            label = listOf(label)
        )
    }

    private fun sendGaPhone(label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "central de ajuda"),
            action = listOf("telefones"),
            label = listOf(label)
        )
    }

    //endregion
}
