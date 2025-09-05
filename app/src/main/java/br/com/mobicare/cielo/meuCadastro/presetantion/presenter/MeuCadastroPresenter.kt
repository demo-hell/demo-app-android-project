//package br.com.mobicare.cielo.meuCadastro.presetantion.presenter
//
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.meuCadastro.data.clients.api.APICallbackPassword
//import br.com.mobicare.cielo.meuCadastro.data.clients.managers.MeuCadastroRepository
//import br.com.mobicare.cielo.meuCadastro.domains.entities.*
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroCallback
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaEmpresaContract
////import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaEmpresaFragment
//import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
//import com.google.common.base.Preconditions.checkNotNull
//
//class MeuCadastroPresenter(mView: MeuCadastroContract.View, repository: MeuCadastroRepository, var isConvivenciaUser: Boolean)
//    : MeuCadastroContract.Presenter, MeuCadastroCallback {
//
//    private val mView: MeuCadastroContract.View
//    private val repository: MeuCadastroRepository
//    private var meuCadastroObj: MeuCadastroObj? = null
//    init {
//        this.repository = checkNotNull(repository, "Repository não pode ser null.")
//        this.mView = checkNotNull<MeuCadastroContract.View>(mView, "View não pode ser null.")
//        mView.hideContent()
//    }
//
//    override fun callAPI() {
//        repository.getMeuCadastro(this)
////        repository.getBrands(this)
//    }
//
//    override fun getChangePassword(body: BodyChangePassword, callback: MinhaEmpresaContract.View) {
//        repository.getChangePassword(body , object : APICallbackPassword {
//            override fun onError(error: String) {
//                callback.onChancePasswordError(error)
//                callback.onError()
//            }
//
//            override fun onSuccess() {
//                callback.onChancePasswordSuccess()
//                callback.onSucess()
//            }
//
//            override fun onErrorAuthentication(error: String) {
//                callback.onChancePasswordError(error)
//                callback.onErrorAuthentication()
//            }
//        })
//    }
//
//    /**
//     * Verifica se existe bandeiras habilitadas e gerencia se exibe ou esconde
//     */
//
//    override fun onSuccessBrands(response: CardBrandFees) {
//       managerBandeirasHabilitadas(response)
//    }
//
//    fun managerBandeirasHabilitadas(bandeiras: CardBrandFees) {
//        mView.loadBandeirasHabilitadas(bandeiras)
//    }
//
//
//    /************** Callback da API  *************/
//
//    override fun onStart() {
//        mView.showProgress()
//    }
//
//    override fun onError(error: ErrorMessage) {
//        mView.hideProgress()
//
//        if (error.logout) {
//            mView.logout(error.message)
//        } else {
//            mView.showError(error)
//        }
//    }
//
//    override fun onErrorBrands(error: ErrorMessage) {
//        if (mView.isAttached()) {
//            mView.hideBandeirasHabilitadas()
//           /* if (error.logout) {
//                mView.logout(error.message)
//            }*/
//        }
//    }
//
//    override fun onFinish() {
//        //mView.hideProgress()
//    }
//
//    override fun onSuccess(response: MeuCadastroObj) {
//        if (mView.isAttached()) {
//            mView.hideProgress()
//
//            meuCadastroObj = response
//            mView.showContent()
//
//            mView.loadDadosEstabelecimento(response)
//        }
//    }
//
//
//    override fun onLoadContactAddress(status: String?) {
//        if (status != null) {
//            val endContato = meuCadastroObj?.getEndereco(MeuCadastroEndereco.Tipo.CONTATO)
//            endContato?.status = status
//
//        }
//    }
//
//    override fun onLoadPhysicalAddress(status: String?) {
//        if (status != null) {
//            val endFisico = meuCadastroObj?.getEndereco(MeuCadastroEndereco.Tipo.FISICO)
//            endFisico?.status = status
//        }
//    }
//}
