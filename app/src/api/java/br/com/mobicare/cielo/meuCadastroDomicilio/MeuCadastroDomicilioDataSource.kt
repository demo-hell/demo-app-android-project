package br.com.mobicare.cielo.meuCadastroDomicilio

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response

class MeuCadastroDomicilioDataSource(context: Context) {
    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun transferAccount(
        addFlag: AccountTransferRequest,
        token: String,
        otpGenerated: String? = null
    ) : Observable<Response<Void>> {
        return api.domicilioTransferAccount(addFlag, token, otpGenerated)
    }

    fun transferOfBrands(
        flagBrands: FlagTransferRequest,
        token: String,
        otpCode: String
    ): Observable<Response<Void>> {
        return api.transferOfBrands(token, otpCode, flagBrands)
    }
}