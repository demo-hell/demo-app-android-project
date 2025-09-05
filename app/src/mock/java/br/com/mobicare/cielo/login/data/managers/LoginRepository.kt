package br.com.mobicare.cielo.login.data.managers

import android.content.Context

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference

/**
 * Created by benhur.souza on 06/04/2017.
 */

open class LoginRepository(private val mContext: Context) {

    fun login(ec: String, user_name: String, psw: String, callback: APICallbackDefault<LoginObj?, String>) {
        val obj = ReaderMock.getAccount(mContext)

        callback.onStart()
        callback.onSuccess(obj)

        if(obj != null) {
            MenuPreference.instance.saveLoginObj(mContext, obj)
            UserPreferences.getInstance().saveToken(mContext, obj.token)
        }

        callback.onFinish()
    }

    companion object {

        fun getInstance(context: Context): LoginRepository {
            return LoginRepository(context)
        }
    }
}
