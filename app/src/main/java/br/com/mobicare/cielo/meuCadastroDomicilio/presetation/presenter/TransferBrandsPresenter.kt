//package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.presenter
//
//import android.view.View
//import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
//import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
//import br.com.mobicare.cielo.commons.data.utils.APIUtils
//import br.com.mobicare.cielo.commons.presentation.CommonPresenter
//import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
//import br.com.mobicare.cielo.commons.utils.LogWrapperUtil
//import br.com.mobicare.cielo.commons.utils.Utils
//import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
//import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.DomicioBancarioContract
//import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity
//import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
//import io.reactivex.disposables.CompositeDisposable
//
//class TransferBrandsPresenter (val mView: DomicioBancarioContract.View,
//                               val mRepository: MeuCadastroContract.MeuCadastroRepository,
//                               val api: CieloAPIServices){
//
//    var viewDB: View?= null
//    lateinit var listenerDB: FlagTransferEngineActivity
//    val compositeDisposable = CompositeDisposable()
//
//
//
//    /**
//     * método para transferir bandeiras para um banco
//     * */
//    fun transferOfBrands(transferFlag : FlagTransferRequest) {
//
//        val token: String? = UserPreferences.getInstance().token
//        val authorization = Utils.authorization()
//
//        token?.let {
//            mRepository.tranferOfBrands(transferFlag, authorization, it, api, compositeDisposable,{
//                mView.hideProgress(viewDB, listenerDB)
//                LogWrapperUtil.info("============ code ${it.code()} ")
//                when(it.code()){
//                    400->mView.errorServer(viewDB, listenerDB)
//                    420->mView.sucessProgress()
//                    200->mView.transfBrandsSucess()
//                    in 500..503->mView.errorServer(viewDB, listenerDB)
//                    in 401..403-> mView.logout()
//                }
//            },{
//                mView.hideProgress(viewDB, listenerDB)
//                val errorMessage = APIUtils.convertToErro(it)
//                when(errorMessage.httpStatus){
//                    401-> mView.logout()
//                    else-> mView.errorServer(viewDB, listenerDB)
//                }
//            })
//        }
//
//    }
//
//    fun onCleared() {
//        compositeDisposable.clear()
//    }
//}