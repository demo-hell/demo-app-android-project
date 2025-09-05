package br.com.mobicare.cielo.esqueciSenha.data.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPasswordResponse
import io.reactivex.Observable

/**
 * Created by benhur.souza on 11/04/2017.
 */

class EsqueciSenhaNewAPIDataSource(context: Context) {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun recoveryPassword(data: RecoveryPassword, akamaiSensorData: String?): Observable<RecoveryPasswordResponse> {
        return api.recoveryPassword(data, akamaiSensorData)
    }

    companion object {

        private var instance: EsqueciSenhaNewAPIDataSource? = null

        fun getInstance(context: Context): EsqueciSenhaNewAPIDataSource {
            if (instance == null) {
                instance = EsqueciSenhaNewAPIDataSource(context)
            }

            return instance as EsqueciSenhaNewAPIDataSource
        }
    }
}
