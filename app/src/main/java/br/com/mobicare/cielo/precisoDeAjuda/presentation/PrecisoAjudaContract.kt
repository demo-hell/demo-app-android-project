package br.com.mobicare.cielo.precisoDeAjuda.presentation

import androidx.annotation.IntegerRes
import br.com.mobicare.cielo.commons.ui.IAttached

/**
 * Created by silvia.miranda on 25/05/2017.
 */
interface  PrecisoAjudaContract {

    interface View : IAttached {
        fun showForgetUserAndStablishment(showCpf: Boolean = false, title: String = "")
        fun showForgetPassword()
        fun showAlert(title: String)
    }
    interface Presenter{
        fun onClickEsqueciEstabelecimento(title: String)
        fun onClickEsqueciUsuario()
        fun onClickEsqueciSenha()
        fun onClickProsseguir(@IntegerRes id: Int, title: String)
    }
}
