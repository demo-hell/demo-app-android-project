package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui

import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.UserNameObj


interface EsqueciUsuarioAndEstabelecimentoContract {

    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showMessageError(msg: String? = null, @IntegerRes msgId: Int = -1, listener: android.view.View.OnClickListener? = null)
        fun showMessageSucess(msg: String? = null, @StringRes msgId: Int = -1, listener: android.view.View.OnClickListener? = null)
        fun showLocalError(@StringRes error: Int)
        fun onSuccess(screenName: String, @StringRes titleId: Int, list: Array<String>?, @StringRes btnRightId: Int)
        fun onUserSuccess(screenName: String, @StringRes titleId: Int, list: Array<UserNameObj>?, @StringRes btnRightId: Int)
        fun changeLabels(@StringRes descriptionId: Int)
        fun managerField(@StringRes hintId: Int)
        fun addMask(@StringRes maskId: Int = -1)
        fun changeActivity(ajuda: Boolean?)
        fun saveEC(ec: String?)
        fun showMessageUser(msg: String)
    }

    interface Presenter {
        fun callAPI(params: String)
        fun checkLabels()
        fun chooseItem(position: Int, ecUser: String? = null)
    }
}
