package br.com.mobicare.cielo.meuCadastroDomicilio

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class MeuCadastroDomicilioRespository(private val remoteDataSource: MeuCadastroDomicilioDataSource) :
    DisposableDefault {
    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    fun transferAccount(
        addFlag: AccountTransferRequest,
        token: String,
        otpGenerated: String? = null,
        callback: APICallbackDefault<Response<Void>, String>
    ) {
        compositeDisp.add(remoteDataSource.transferAccount(addFlag, token, otpGenerated)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                if (it.code() in 200..204)
                    callback.onSuccess(it)
                else
                    callback.onError(APIUtils.convertToErro(it))
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    fun transferOfBrands(
        flagBrands: FlagTransferRequest,
        token: String,
        otpCode: String,
        callback: APICallbackDefault<Response<Void>, String>
    ) {
        compositeDisp.add(remoteDataSource.transferOfBrands(flagBrands, token, otpCode)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                    callback.onSuccess(it)
                else
                    callback.onError(APIUtils.convertToErro(it))
            }, {
                callback.onError(APIUtils.convertToErro(it))
            })
        )
    }

}