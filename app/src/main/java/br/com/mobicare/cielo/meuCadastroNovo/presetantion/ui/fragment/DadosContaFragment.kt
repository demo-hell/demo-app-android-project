package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.ui.MfaBaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosContaPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.TrocaDomicilioSolicitacoesActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.DadosContaAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.dialog.DetailBrandBottomSheet
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationBottomSheet
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.dados_conta.*
import kotlinx.android.synthetic.main.dados_conta.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4

/**
 * Created by Enzo Teles
 */

const val CAROUSEL = "carrossel"
const val DETAIL_BRANDS = "detail_brands"
const val MODAL = "modal"
const val ACCOUNTS = "contas"
const val MY_REGISTER = "meu-cadastro"
const val ESTABLISHMENT_ANALYTICS = "estabelecimento"

@SuppressLint("ValidFragment")
class DadosContaFragment : MfaBaseFragment(),
    MeuCadastroContract.DadosContaView {

    val presenter: DadosContaPresenter by inject {
        parametersOf(this)
    }

    var onTransferBrandListener: OnTransferBrandListener? = null
    var listener: ListenerCadastroScreen? = null
    var listBank = ArrayList<Bank>()
    var lstSolution: List<Solution>? = null
    var elegibility = false
    var isMFA = false

    lateinit var dadosContaAdapter: DadosContaAdapter

    interface OnTransferBrandListener {
        fun transferBrand(listBanks: List<Bank>, currentBank: Bank)
    }

    companion object {
        fun newInstance(
            listener: ListenerCadastroScreen,
            onTransferBrandListener: OnTransferBrandListener
        ) =
            DadosContaFragment().apply {
                this.listener = listener
                this.onTransferBrandListener = onTransferBrandListener
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.dados_conta, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.INCLUIR_DOMICILIO_BANCARIO)
        )
            bt_add_account.visible() else bt_add_account.gone()

        initAccount(view)
        setupButtonError(view)
        setupButtonContractTrack()
    }

    private fun initAccount(view: View) {
        listener?.showMask()
        showProgress()
        presenter.loadDadosAccount(view)
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = requireActivity(),
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }

    override fun onResume() {
        super.onResume()
        presenterMfa.onResume()
        logScreenView()
    }

    /**
     * método que pega o objeto clicado do recyclerview
     * */
    private fun itemMeuCadastroDomicilioBancario(bank: Bank) {
        gaSendInteraction()
        val detailsBrandBottomSheet = DetailBrandBottomSheet.newInstance(bank)
        detailsBrandBottomSheet.show(
            childFragmentManager,
            DETAIL_BRANDS
        )

    }

    private fun gaSendInteraction() {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MY_REGISTER),
                action = listOf(ACCOUNTS, MODAL),
                label = listOf(Label.BOTAO, CAROUSEL)
            )
        }
    }

    private fun setupButtonError(view: View) {
        errorLayout.configureActionClickListener {
            if (isMFA)
                presenterMfa.load()
            else
                initAccount(view)
        }
    }

    private fun setupButtonContractTrack() {
        contract_track.setOnClickListener {
            requireActivity().startActivity<TrocaDomicilioSolicitacoesActivity>()
        }
    }

    override fun showBrands(lstSolution: List<Solution>, view: View) {
        listBank.addAll(lstSolution[0].banks)
        view.bt_add_account.setOnClickListener {
            presenterMfa.load()
            listener?.showMask()
        }
        listener?.hideMask()
        hideProgress(view)
        this.lstSolution = lstSolution
        lstSolution.let {

            val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(
                requireContext(),
                1,
                androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL,
                false
            )

            dadosContaAdapter = DadosContaAdapter(
                lstSolution[0].banks,
                this::itemMeuCadastroDomicilioBancario
            ) { lBank, bank ->
                onTransferBrandListener?.transferBrand(lBank, bank)
            }

            with(view.dc_rv_banks) {
                isVerticalScrollBarEnabled = false
                adapter = dadosContaAdapter
                layoutManager = gridLayoutManager
                LinearSnapHelper().attachToRecyclerView(this)
            }
        }
    }

    override fun error() {
        isMFA = false
        setupShowError()
    }

    private fun setupShowError() {
        constraint_view?.gone()
        frameProgress_dc?.gone()
        errorLayout?.visible()
        listener?.hideMask()
    }

    override fun showProgress() {
        constraint_view.invisible()
        frameProgress_dc.gone()
        errorLayout.gone()
    }

    override fun hideProgress(view: View) {
        view.constraint_view.visible()
        view.frameProgress_dc.gone()
        view.errorLayout.gone()
    }

    override fun logout() {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun onPause() {
        super.onPause()
        presenterMfa.onPause()
        presenter.onCleared()
        listener?.hideMask()
    }

    private fun mfaReady() {
        elegibility = true
        if (isAttached()) {
            listener?.callAccountEngine(listBank, elegibility)
        }
    }

    private fun genericErrorMfa(error: ErrorMessage?) {
        isMFA = true
        requireActivity().genericError(
            error = error,
            onFirstAction = {
                presenterMfa.load()
            },
            onSecondAction = {
                requireActivity().moveToHome()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            isErrorMFA = true
        )
    }

    //##############################################################################################
    //                          INTERFACES MFA PRESENTER
    //##############################################################################################

    override fun showLoading(isShow: Boolean) {
        if (isShow) listener?.showMask()
        else listener?.hideMask()
    }

    override fun showError(error: ErrorMessage) {
        isMFA = true
        setupShowError()
    }

    override fun showTokenGenerator() {
        mfaReady()
    }

    override fun showNotEligible() {
        frameProgress_dc.gone()
        bottomSheetGeneric(
            "",
            R.drawable.ic_generic_error_image,
            getString(R.string.text_funcionality_dont_free_title),
            getString(R.string.text_funcionality_dont_free_subtitle),
            getString(R.string.text_lgpd_saiba_mais),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    startHelpCenter()
                    dismiss()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showMFAStatusPending() {
        frameProgress_dc.gone()
        bottomSheetGeneric(
            "",
            R.drawable.ic_37,
            getString(R.string.text_mfa_status_pending_title),
            getString(R.string.text_mfa_status_pending_subtitle),
            getString(R.string.ok),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    dismiss()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showMFAStatusErrorPennyDrop() {
        frameProgress_dc.gone()
        bottomSheetGeneric(
            "",
            R.drawable.ic_42,
            getString(R.string.text_mfa_status_error_penny_drop_title),
            getString(R.string.text_mfa_status_error_penny_drop_subtitle),
            getString(R.string.text_lgpd_saiba_mais),
            false,
            statusBtnOk = true,
            statusViewLine = false,
            isResizeToolbar = true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnOk(dialog: Dialog) {
                    startHelpCenter()
                    dismiss()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun startHelpCenter() {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    override fun showOnboarding() {
        listener?.callStatusError()
    }

    override fun showMerchantOnboard(status: String?) {
        listener?.callStatusError()
    }

    override fun callPutValuesValidate() {
        listener?.callStatusError()
    }

    override fun callBlockedForAttempt() {
        listener?.callStatusError()
    }

    override fun callTokenReconfiguration() {
        listener?.callStatusError()
    }

    override fun bottomSheetConfiguringMfaDismiss() {
        listener?.hideMask()
    }

    override fun showDifferentDevice() {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, isResend = true
        ).show(childFragmentManager, tag)
    }

    override fun showUserWithP2(type: EnrollmentType) {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, type = type.name, isResend = false
        ).show(childFragmentManager, tag)
    }

    override fun onErrorResendPennyDrop(error: ErrorMessage?) {
        isMFA = true
        requireActivity().genericError(
            error = error,
            onFirstAction = {
                presenterMfa.resendPennyDrop()
            },
            onSecondAction = {
                requireActivity().moveToHome()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            isErrorMFA = true
        )
    }

    override fun showUserNeedToFinishP2(error: ErrorMessage?) {
        requireActivity().finishP2(
            onFirstAction = {
                requireActivity().moveToHome()
            },
            onSecondAction = {
                showOnboardingID()
            },
            onSwipeAction = {
                requireActivity().moveToHome()
            },
            error
        )
    }

    override fun onShowSuccessConfiguringMfa(isShowMessage: Boolean) {
        if (isShowMessage)
            requireActivity().successConfiguringMfa {
                mfaReady()
            }
        else
            mfaReady()
    }

    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        genericErrorMfa(error)
    }

    private fun logScreenView() {
        if (isAttached()) {
            ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT)
        }
    }
}