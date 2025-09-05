package br.com.mobicare.cielo.dirf

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import io.reactivex.disposables.CompositeDisposable

class DirfInteractor (private val api: CieloAPIServices): DirfContract.Interactor{

    private var compositeDisp = CompositeDisposable()

    /**
     * method to clear disposable
     * */
    override fun cleanDisposable() {
        compositeDisp.clear()
    }


    override fun callDirf(
        year: Int,
        cnpj: String,
        companyName: String,
        owner: String,
        cpf: String,
        type: String,
        callback: APICallbackDefault<DirfResponse, String>
    ) {
        compositeDisp.add(api.callDirf(year, cnpj, companyName, owner, cpf, type, )
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )
    }

    override fun callDirfPDFOrExcel(year: Int, type: String?, callback: APICallbackDefault<DirfResponse, String>) {

        compositeDisp.add(api.callDirfPDFOrExcel(year, type)
            .configureIoAndMainThread()
            .subscribe({
                callback.onSuccess(it)
            }, {
                val errorMessage = APIUtils.convertToErro(it)
                callback.onError(errorMessage)
            })
        )

    }

    override fun callApi(callback: APICallbackDefault<MCMerchantResponse, String>) {

        val token: String? = UserPreferences.getInstance().token
        token?.let {
            compositeDisp.add(api.loadMerchant(it)
                .configureIoAndMainThread()
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
            )
        }

    }
}