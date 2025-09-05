//package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments
//
//import android.app.Activity
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
//import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
//import br.com.mobicare.cielo.commons.ui.BaseFragment
//import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
//import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
//import br.com.mobicare.cielo.injection.Injection
//import br.com.mobicare.cielo.main.presentation.ui.activities.MainActivity
//import br.com.mobicare.cielo.meuCadastro.domains.entities.*
//import br.com.mobicare.cielo.meuCadastro.presetantion.presenter.MeuCadastroPresenter
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroContract
//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters.MeuCadastroAdapter
//import kotlinx.android.synthetic.main.content_error.*
//import kotlinx.android.synthetic.main.meu_cadastro_fragment.*
//
//class MeuCadastroFragmentAtual : BaseFragment(), MeuCadastroContract.View {
//
//    companion object {
//        var bandeiras: CardBrandFees? = null
//        var meuCadastroObj: MeuCadastroObj? = null
//    }
//
//    lateinit var presenterCad: MeuCadastroPresenter
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//
//        managerFeatureToggleCardBrandFee()
//
//        return inflater.inflate(R.layout.meu_cadastro_fragment, container, false)
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenterCad.callAPI()
//    }
//
//    /**  feature toggle taxas e bandeiras **/
//    fun managerFeatureToggleCardBrandFee() {
//        var isToShow: Boolean = FeatureTogglePreference.instance
//                .getFeatureTogle(FeatureTogglePreference.TAXAS_BANDEIRAS)
//        if (!isToShow) {
//            hideBandeirasHabilitadas()
//        }
//    }
//
//    // métodos para sobrescrita
//    override fun hideContent() {
//
//    }
//
//    override fun showContent() {
//        if (isAttached()) {
//            relativeMyRegisterError.visibility = View.GONE
//            pb_meu_cadastro.visibility = View.GONE
//            tabs_meu_cad.visibility = View.VISIBLE
//            viewpager_meu_cad.visibility = View.VISIBLE
//        }
//    }
//
//    override fun  showProgress() {
//
//        if (isAttached()) {
//            relativeMyRegisterError.visibility = View.GONE
//            pb_meu_cadastro.visibility = View.VISIBLE
//            viewpager_meu_cad.visibility = View.GONE
//            tabs_meu_cad.visibility = View.GONE
//        }
//    }
//
//    override fun hideProgress() {
//        if (isAttached()) {
//            relativeMyRegisterError.visibility = View.GONE
//            pb_meu_cadastro.visibility = View.GONE
//            viewpager_meu_cad.visibility = View.VISIBLE
//            tabs_meu_cad.visibility = View.VISIBLE
//        }
//    }
//
//    override fun showError(error: ErrorMessage?) {
//        if (isAttached()) {
//            relativeMyRegisterError.visibility = View.VISIBLE
//            img_error.visibility = View.GONE
//            text_view_error_msg.text = error.message
//            container_error.visibility = View.VISIBLE
//            button_error_try.text = getString(R.string.tentar_novamente)
//            onClickTryAgain()
//        }
//    }
//
//    private fun onClickTryAgain() {
//        presenterCad = MeuCadastroPresenter(this, Injection
//                .provideMeuCadastroRepository(this.activity as Activity),
//                UserPreferences.getInstance().isConvivenciaUser)
//
//
//        button_error_try.setOnClickListener {
//
//            if (isAttached()) {
//                container_error.visibility = View.GONE
//            }
//
//            presenterCad.callAPI()
//        }
//    }
//
//
//    override fun loadDadosEstabelecimento(meuCadastroObj: MeuCadastroObj) {
//        MeuCadastroFragmentAtual.meuCadastroObj = meuCadastroObj
//        startViewPage()
//    }
//
//
//    override fun loadBandeirasHabilitadas(bandeiras: CardBrandFees) {
//        MeuCadastroFragmentAtual.bandeiras = bandeiras
//        startViewPage()
//    }
//
//    private fun startViewPage() {
//        if (isAttached()) {
//            val fragmentAdapter = MeuCadastroAdapter(requireFragmentManager(), requireActivity(), meuCadastroObj, bandeiras, presenterCad)
//            viewpager_meu_cad.adapter = fragmentAdapter
//            tabs_meu_cad.setupWithViewPager(viewpager_meu_cad)
//        }
//    }
//
//    override fun hideBandeirasHabilitadas() {
//        if(isAttached()) {
//            container_error.visibility = View.VISIBLE
//            pb_meu_cadastro.visibility = View.GONE
//            tabs_meu_cad.visibility = View.GONE
//            onClickTryAgain()
//        }
//    }
//
//    override fun logout(error: String) {
//
//        if (isAttached()) {
//            AlertDialogCustom.Builder(this.context, getString(R.string.ga_meu_cadastro))
//                    .setTitle(R.string.ga_meu_cadastro)
//                    .setMessage(error)
//                    .setBtnRight(getString(R.string.ok))
//                    .setCancelable(false)
//                    .setOnclickListenerRight {
//                        val main = activity as MainActivity?
//                        main?.logout()
//                    }
//                    .show()
//        }
//    }
//
//    override fun context(): Context {
//        return requireActivity().baseContext
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        bandeiras = null
//        meuCadastroObj = null
//    }
//}