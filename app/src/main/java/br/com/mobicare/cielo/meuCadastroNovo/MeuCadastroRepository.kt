package br.com.mobicare.cielo.meuCadastroNovo

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MeuCadastroRepository(val api: CieloAPIServices) : MeuCadastroContract.MeuCadastroRepository {

    /**
     * método para carregar as brands da api
     * @params token api compositeDisp callback
     * */
    override fun loadBrands(token: String, compositeDisp: CompositeDisposable, callback: (List<Solution>) -> Unit,
                            callbackError: (Throwable) -> Unit) {
        compositeDisp.add(api.loadBrands(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ solutions ->
                    callback(solutions)
                }, { err ->
                    callbackError(err)
                })
        )
    }

    /**
     * método para carregar o merchant da api
     * @params token api compositeDisp callback
     * */
    override fun loadMerchant(token: String) = api.loadMerchant(token)

    /**
     * método para carregar o usuário da api
     * @params token api compositeDisp callback
     * */
    override fun loadMe(token: String, compositeDisp: CompositeDisposable, callback: (MeResponse) -> Unit,
                        callbackError: (Throwable) -> Unit) {
        compositeDisp.add(api.loadMe(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ user ->
                    callback(user)
                }, { err ->
                    callbackError(err)
                })
        )

    }

    /**
     * método para mudar a senha do usuário na api
     * @param token api composite body callback
     * */
    override fun getChangePassword(token: String, body: BodyChangePassword): Observable<Response<Void>> {
        return api.getChangePassword(token, Utils.authorization(), body)
    }

    override fun getDomiciles(protocol: String?, status: String?, page: Int?, pageSize: Int?) = api.getDomiciles(protocol, status, page, pageSize)

    override fun getAdditionalInfo() = api.getUserAdditionalInfo()

    override fun transferOfBrands(
        transferFlag: FlagTransferRequest,
        token: String,
        otpCode: String,
        compositeDisp: CompositeDisposable,
        callback: (Response<Void>) -> Unit,
        callbackError: (Throwable) -> Unit
    ) {
        compositeDisp.add(
            api.transferOfBrands(token, otpCode, transferFlag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    callback(it)
                }, { err ->
                    callbackError(err)
                })
        )
    }
}