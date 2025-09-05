package br.com.mobicare.cielo.precisoDeAjuda.presentation.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.activities.EsqueciUsuarioAndEstabelecimentoActivity
import br.com.mobicare.cielo.precisoDeAjuda.presentation.PrecisoAjudaContract
import br.com.mobicare.cielo.precisoDeAjuda.presentation.presenter.PrecisoAjudaPresenter
import kotlinx.android.synthetic.main.activity_preciso_ajuda.*
import kotlinx.android.synthetic.main.content_preciso_ajuda.*
import org.jetbrains.anko.startActivity


class PrecisoAjudaActivity : BaseActivity(), PrecisoAjudaContract.View {

    var presenter: PrecisoAjudaPresenter? = null
    var screen: String = ""

    companion object {
        @JvmField
        val ESTABLISHMENT = "establishment"

        const val TOOLBAR_TITLE = "br.com.cielo.toolbarTitle"

        @JvmField
        val CPF = "cpf"
        @JvmField
        val FIELD_EC = "field_ec"
        @JvmField
        val FIELD_USUARIO = "field_usuario"

        const val BACK_TO = "back.to"
        const val PRECISA_AJUDA = "PrecisaDeAjuda"
        const val SCREEN_NAME = "SCREEN_NAME"

        const val USER_TYPE_INPUT = "login.user.type.input"
    }

    var extraEC: String? = ""
    var extraUsuario: String? = ""
    var toolbarTitle: String? = null
    get() = intent.getStringExtra(PrecisoAjudaActivity.TOOLBAR_TITLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_preciso_ajuda)
        var isEstablishment = false

        if (intent != null && intent.extras != null) {
            isEstablishment = intent.extras!!.getBoolean(PrecisoAjudaActivity.ESTABLISHMENT, false)
            extraEC = intent.getStringExtra(PrecisoAjudaActivity.FIELD_EC)
            extraUsuario = intent.getStringExtra(PrecisoAjudaActivity.FIELD_USUARIO)
        }

        intent.getStringExtra(PrecisoAjudaActivity.SCREEN_NAME)?.let {
            screen = it
        }

        presenter = PrecisoAjudaPresenter(this, isEstablishment)

        setupToolbar(toolbarNeedHelp as Toolbar, toolbarTitle ?: "")

        this.onBackButtonListener = object: OnBackButtonListener {
            override fun onBackTouched() {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPICON),
                    action = listOf(screenPath()),
                    label = listOf(String.format(Label.VOLTAR_PARA, "Inicio"))
                )
                this@PrecisoAjudaActivity.finish()
            }
        }

        preciso_ajuda_esqueci_estabelecimento.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPCARD),
                action = listOf(screenPath()),
                label = listOf(textview_ajuda_esqueci_estabelecimento.text.toString())
            )
            presenter!!.onClickEsqueciEstabelecimento(textview_ajuda_esqueci_estabelecimento.text.toString())
        }
        preciso_ajuda_esqueci_senha.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPCARD),
                action = listOf(screenPath()),
                label = listOf(textview_ajuda_esqueci_senha.text.toString())
            )
            presenter!!.onClickEsqueciSenha()
        }
        preciso_ajuda_esqueci_usuario.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPCARD),
                action = listOf(screenPath()),
                label = listOf(textview_ajuda_esqueci_usuario.text.toString())
            )
            presenter!!.onClickEsqueciUsuario()
        }

        Analytics.trackScreenView(
            screenName = screenPath(),
            screenClass = this.javaClass
        )
    }

    private fun screenPath() = "$screen/$PRECISA_AJUDA"

    override fun showForgetUserAndStablishment(showCpf: Boolean, title: String) {

        startActivity<EsqueciUsuarioAndEstabelecimentoActivity>(
                PrecisoAjudaActivity.ESTABLISHMENT to false,
                PrecisoAjudaActivity.CPF to showCpf,
                PrecisoAjudaActivity.SCREEN_NAME to screen,
                PrecisoAjudaActivity.TOOLBAR_TITLE to title,
                PrecisoAjudaActivity.BACK_TO to screenPath(),
                PrecisoAjudaActivity.FIELD_EC to extraEC,
                PrecisoAjudaActivity.FIELD_USUARIO to extraUsuario)
    }

    override fun showForgetPassword() {
//        startActivity<EsqueciSenhaActivity>(
//                PrecisoAjudaActivity.ESTABLISHMENT to false,
//                PrecisoAjudaActivity.CPF to false,
//                PrecisoAjudaActivity.SCREEN_NAME to screen)
    }

    override fun showAlert(title: String) {
        createForgetUserDialog(screenPath()) {
            presenter?.onClickProsseguir(it, title)
        }
    }

}
