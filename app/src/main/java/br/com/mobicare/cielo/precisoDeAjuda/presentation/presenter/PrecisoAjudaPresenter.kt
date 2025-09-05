package br.com.mobicare.cielo.precisoDeAjuda.presentation.presenter

import androidx.core.util.Preconditions
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.precisoDeAjuda.presentation.PrecisoAjudaContract

/**
 * Created by silvia.miranda on 25/05/2017.
 */
class PrecisoAjudaPresenter(private var mView: PrecisoAjudaContract.View, showAlert: Boolean) : PrecisoAjudaContract.Presenter {

    init {
        this.mView = Preconditions.checkNotNull(mView, "View não pode ser null.")
        if (showAlert && mView.isAttached()) {
            mView.showAlert("")
        }
    }

    override fun onClickEsqueciEstabelecimento(title: String) {
        if (mView.isAttached()) {
            mView.showAlert(title)
        }
    }

    override fun onClickEsqueciUsuario() {
        if (mView.isAttached()) {
//            mView.changeActivity(EsqueciUsuarioAndEstabelecimentoActivity::class.java, isEstablishiment = false)
            mView.showForgetUserAndStablishment(true)
        }
    }

    override fun onClickEsqueciSenha() {
        if (mView.isAttached()) {
//            mView.changeActivity(EsqueciSenhaActivity::class.java, isEstablishiment = false)
            mView.showForgetPassword()
        }
    }

    override fun onClickProsseguir(id: Int, title: String) {
        if(mView.isAttached()) {
            when (id) {
                R.id.radio_button_ajuda_alert_pessoa_fisica -> mView.showForgetUserAndStablishment(true, title)
                R.id.radio_button_ajuda_alert_pessoa_juridica -> mView.showForgetUserAndStablishment(false, title)
            }
        }
    }
}