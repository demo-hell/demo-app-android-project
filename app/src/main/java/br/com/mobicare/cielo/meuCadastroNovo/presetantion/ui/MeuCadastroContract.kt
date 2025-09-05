package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui

import android.view.View
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.ContactPreference
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PcdType
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TimeOfDay
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.PaymentAccountsDomicile
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

interface MeuCadastroContract {

    interface DadosEstabelecimentoView {
        fun error()
        fun showProgress()
        fun hideProgress()
        fun logout()
        fun showEstabelecimento(estabelecimento: MCMerchantResponse)
    }

    interface DadosUsuarioView {
        fun showProgress() {}
        fun hideProgress() {}
        fun showProgressScreen() {}
        fun hideProgressScreen() {}
        fun showUser(user: MeResponse?)
        fun showAdditionalInfo(
            typeOfCommunication: ArrayList<TypeOfCommunication>,
            contactPreference: ContactPreference?,
            timeOfDay: TimeOfDay?,
            pcdType: PcdType?
        )
        fun error() {}
        fun logout() {}
    }

    interface DadosContaView {
        fun showBrands(lstSolution: List<Solution>, view: View)
        fun error()
        fun showProgress()
        fun hideProgress(view: View)
        fun logout()
    }

    interface MeuCadastroRepository {
        fun loadBrands(token: String, compositeDisp: CompositeDisposable, callback: (List<Solution>) -> Unit, callbackError: (Throwable) -> Unit)
        fun transferOfBrands(transferFlag: FlagTransferRequest, token: String, otpCode: String, compositeDisp: CompositeDisposable, callback: (Response<Void>) -> Unit, callbackError: (Throwable) -> Unit)
        fun loadMerchant(token: String): Observable<MCMerchantResponse>
        fun loadMe(token: String, compositeDisp: CompositeDisposable, callback: (MeResponse) -> Unit, callbackError: (Throwable) -> Unit)
        fun getChangePassword(token: String, body: BodyChangePassword): Observable<Response<Void>>
        fun getDomiciles(protocol: String?, status: String?, page:Int?,  pageSize:Int?): Observable<PaymentAccountsDomicile>
        fun getAdditionalInfo(): Observable<GetUserAdditionalInfo>
    }
}