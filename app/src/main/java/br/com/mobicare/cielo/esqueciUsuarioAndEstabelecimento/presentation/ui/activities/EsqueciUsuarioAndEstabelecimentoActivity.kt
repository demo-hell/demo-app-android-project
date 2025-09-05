package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceRadioButtonView
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.UserNameObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.presenter.EsqueciUsuarioAndEstabelecimentoPresenter
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoContract
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.injection.Injection
import br.com.mobicare.cielo.precisoDeAjuda.presentation.ui.activities.PrecisoAjudaActivity
import kotlinx.android.synthetic.main.activity_esqueci_usuario.*
import kotlinx.android.synthetic.main.alert_esqueci_usuario.*
import kotlinx.android.synthetic.main.content_esqueci_usuario.*


class EsqueciUsuarioAndEstabelecimentoActivity : BaseActivity(),
        EsqueciUsuarioAndEstabelecimentoContract.View {

    private var isEstablishment = false
    private var showCPF = false
    private var mPresenter: EsqueciUsuarioAndEstabelecimentoContract.Presenter? = null
    private var extraEC: String? = null
    private var screenNameResId = 0
    /*private var SCREEN_NAME_ESQUECI_USUARIO = "PrecisaDeAjuda/EsqueciOUsuario"
    private var SCREEN_NAME_ESTABLISHMENT_PJ = "PrecisaDeAjuda/EsqueciONEstabelecimentoPessoaJuridica"
    private var SCREEN_NAME_ESTABLISHMENT_PF = "PrecisaDeAjuda/EsqueciONEstabelecimentoPessoaFisica"*/

    companion object {

        const val TOOLBAR_TITLE = "br.com.cielo.toolbarTitle"
    }

    private var toolbarTitle:  String? = null
    get() = intent.extras?.getString(TOOLBAR_TITLE)

    var screen : String = ""

    private var backTo : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_usuario)

        setupToolbar(toolbarForgetUser as Toolbar, toolbarTitle ?: "")

        if (intent != null && intent.extras != null) {
            isEstablishment = intent.extras!!.getBoolean(PrecisoAjudaActivity.ESTABLISHMENT, false)
            showCPF = intent.extras!!.getBoolean(PrecisoAjudaActivity.CPF, false)
            extraEC = intent.getStringExtra(PrecisoAjudaActivity.FIELD_EC)
            screen = intent.getStringExtra(PrecisoAjudaActivity.SCREEN_NAME) ?: ""
            backTo = intent.getStringExtra(PrecisoAjudaActivity.BACK_TO) ?: ""
        }

        this.onBackButtonListener = object: OnBackButtonListener {
            override fun onBackTouched() {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPICON),
                    action = listOf("${screen}/${getScreenName()}"),
                    label = listOf(
                        String.format(
                            Label.VOLTAR_PARA,
                            if (backTo.isEmpty()) "PrecisoDeAjuda" else backTo
                        )
                    )
                )

                finish()
            }
        }

        this.animationListener = object: AnimationListener {

            override fun whenClose() {
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }

        }

        screenNameResId = if(showCPF) R.string.ga_esqueci_ec_pf  else R.string.ga_esqueci_ec_pj

        if (!isEstablishment && extraEC != null && extraEC!!.isNotBlank()) {
            edit_text_esqueci_field.setText(extraEC)
        }

        edit_text_esqueci_field.setOnClickListener{
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPTEXTFIELD),
                action = listOf("${screen}/${getScreenName()}"),
                label = listOf(text_input_esqueci_usuario_ec.hint.toString())
            )
        }

        Analytics.trackScreenView(
            screenName = getScreenName(),
            screenClass = this.javaClass
        )

        mPresenter = EsqueciUsuarioAndEstabelecimentoPresenter(this ,this,
                Injection.provideEsqueciUsuarioRepository(this), isEstablishment, showCPF)
        button_esqueci_usuario_enviar.setOnClickListener {
            mPresenter?.callAPI(edit_text_esqueci_field.text.toString())
        }

        mPresenter?.checkLabels()
    }

    fun getScreenName() : String{
        return if (isEstablishment){
            if (showCPF){
                SCREEN_NAME_ESTABLISHMENT_PF
            } else {
                SCREEN_NAME_ESTABLISHMENT_PJ
            }
        } else {
            SCREEN_NAME_ESQUECI_USUARIO
        }
    }

    override fun managerField(hintId: Int) {
        text_input_esqueci_usuario_ec.hint = getString(hintId)
    }

    override fun addMask(maskId: Int) {
        val cpfOfCnpjMaskWatcher = edit_text_esqueci_field
                .getMask(getString(maskId), edit_text_esqueci_field)

        edit_text_esqueci_field.addTextChangedListener(cpfOfCnpjMaskWatcher)
    }

    override fun showProgress() {
        progress_esqueci_usuario_loading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_esqueci_usuario_loading.visibility = View.GONE
    }

    // EXIBE A LISTA NO ON SUCESS ESQUECI ESTABELECIMENTO
    override fun onSuccess(screenName: String, @StringRes titleId: Int, list: Array<String>?, @StringRes btnId: Int) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_esqueci_usuario)
        dialog.setTitle("")

        dialog.custom_dialog_title.text = getString(titleId)
        dialog.custom_dialog_button_right.text = getString(btnId)

        val params = WindowManager.LayoutParams()
        params.copyFrom(dialog.window!!.attributes)
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        var i = 1
        list?.forEach {
            val rbn = layoutInflater.inflate(R.layout.custom_radio_buttom, null) as TypefaceRadioButtonView
            rbn.id = i + 1000
            rbn.text = it
            rbn.isChecked = (i == 1)
            rbn.invalidate()
            rbn.setOnClickListener {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPRADIO),
                    action = listOf("${screen}/${getScreenName()}"),
                    label = listOf("Radio | Numero do Estabelecimento")
                )
            }
            dialog.radio_group_list.addView(rbn)
            i++
        }

        dialog.custom_dialog_button_right.setOnClickListener {
            mPresenter?.chooseItem(dialog.radio_group_list.checkedRadioButtonId % 1000 - 1)
            dialog.dismiss()
        }

        dialog.custom_dialog_button_cancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.attributes = params
    }

    // EXIBE A LISTA NO ON SUCESS ESQUECI USUARIO
    override fun onUserSuccess(screenName: String, titleId: Int, list: Array<UserNameObj>?, btnRightId: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_esqueci_usuario)
        dialog.setTitle("")

        dialog.custom_dialog_title.text = getString(titleId)
        dialog.custom_dialog_button_right.text = getString(btnRightId)

        val params = WindowManager.LayoutParams()
        params.copyFrom(dialog.window!!.attributes)
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        var i = 1
        list?.forEach {
            val rbn = layoutInflater.inflate(R.layout.custom_radio_buttom, null) as TypefaceRadioButtonView
            rbn.id = it.id
            rbn.text = it.userName
            rbn.isChecked = (i == 1)
            rbn.invalidate()
            rbn.setOnClickListener{
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPRADIO),
                    action = listOf("${screen}/${getScreenName()}"),
                    label = listOf("Radio | Usuário Disponível")
                )
            }
            dialog.radio_group_list.addView(rbn)
            i++
        }

        dialog.custom_dialog_button_right.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf("${screen}/${getScreenName()}"),
                label = listOf(dialog.custom_dialog_button_right.text.toString())
            )
            mPresenter?.chooseItem(dialog.radio_group_list.checkedRadioButtonId, edit_text_esqueci_field.text.toString())
            dialog.dismiss()
        }

        dialog.custom_dialog_button_cancelar.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                action = listOf("${screen}/${getScreenName()}"),
                label = listOf(dialog.custom_dialog_button_cancelar.text.toString())
            )
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.attributes = params
    }

    override fun showMessageError(msg: String?, @StringRes msgId: Int, listener: View.OnClickListener?) {
        progress_esqueci_usuario_loading.visibility = View.GONE

        AlertDialogCustom.Builder(this, getString(R.string.ajuda_title) + " > " + getString(screenNameResId))
                .setMessage(msg, msgId)
                .setBtnRight(getString(android.R.string.ok))
                .setOnclickListenerRight {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                        action = listOf("${screen}/${getScreenName()}"),
                        label = listOf("Erro ao enviar para cielo OK")
                    )
                }
                .setOnclickListenerRight(listener)
                .show()
    }

    override fun showMessageSucess(msg: String?, @StringRes msgId: Int, listener: View.OnClickListener?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.MENSAGEM_SUCESSO),
            action = listOf("${screen}/${getScreenName()}".toLowerCasePTBR()),
            label = listOf("usuario enviado para cielo")
        )

        progress_esqueci_usuario_loading.visibility = View.GONE

        AlertDialogCustom.Builder(this, getString(R.string.ajuda_title) + " > " + getString(screenNameResId))
                .setMessage(msg, msgId)
                .setBtnRight(getString(android.R.string.ok))
                .setOnclickListenerRight {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                        action = listOf("${screen}/${getScreenName()}"),
                        label = listOf("Usuario Enviado para Cielo OK")
                    )
                }
                .setOnclickListenerRight(listener)
                .show()
    }

    override fun showMessageUser(msg: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.MENSAGEM_SUCESSO),
            action = listOf("${screen}/${getScreenName()}".toLowerCasePTBR()),
            label = listOf("usuario enviado para cielo")
        )

        progress_esqueci_usuario_loading.visibility = View.GONE

        AlertDialogCustom.Builder(this, getString(R.string.ajuda_title) + " > " + getString(screenNameResId))
                .setMessage(msg)
                .setBtnRight(getString(android.R.string.ok))
                .setOnclickListenerRight {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
                        action = listOf("${screen}/${getScreenName()}"),
                        label = listOf("Usuario Enviado para Cielo OK")
                    )
                    this.changeActivity(false)
                }
                .show()
    }

    override fun changeLabels(descriptionId: Int) {
        textview_preciso_ajuda_description.text = getString(descriptionId).toString()
    }

    override fun showLocalError(@StringRes error: Int) {
        AlertDialogCustom.Builder(this, getString(R.string.ajuda_title) + " > " + getString(screenNameResId))
                .setMessage(getString(error))
                .setBtnRight(getString(android.R.string.ok))
                .show()
    }

    override fun changeActivity(ajuda: Boolean?) {
//        var intent = Intent(this@EsqueciUsuarioAndEstabelecimentoActivity,
//                MainActivity::class.java)
//        intent.putExtra(MainActivity.EC_NUMBER_FROM_HELP,
//                UserPreferences.getInstance().numeroEC)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        if (ajuda!!) {
//            intent = Intent(this@EsqueciUsuarioAndEstabelecimentoActivity, PrecisoAjudaActivity::class.java)
//        }
//        startActivity(intent)
        this.finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun saveEC(ec: String?) {
//        val intent = Intent(NewLoginFragment.UPDATE_USER_EC)
//        intent.putExtra(NewLoginFragment.EXTRA_EC_NUMBER, ec)
//        UserPreferences.getInstance().keepEC(ec)
//        UserPreferences.getInstance().saveKeep(true)
    }

}
