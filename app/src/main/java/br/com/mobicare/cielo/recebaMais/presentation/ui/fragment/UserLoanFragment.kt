package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.Keep
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ERROR_401
import br.com.mobicare.cielo.commons.constants.SUCCESS_200
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.databinding.FragmentUserLoanBinding
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.recebaMais.*
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.domains.entities.Contract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter.Companion.UNKNOWN_STATUS
import br.com.mobicare.cielo.recebaMais.presentation.ui.activity.FluxoNavegacaoRecebaMaisActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Keep
class UserLoanFragment : BaseFragment(),
    UserLoanSimulationFragment.OnStateListener, UserLoanContract.View.UserLoanScreen,
    ActivityStepCoordinatorListener {

    var onLogoutListener: LogoutListener? = null

    val offer: Offer? by lazy {
        arguments?.getParcelable(RECEBA_MAIS_OFFER)
    }

    private val isUserFromBanner: Boolean by lazy {
        (arguments?.getBoolean(RECEBA_MAIS_USER_FROM_BANNER, false)) ?: false
    }

    private val userLoanPresenter: UserLoanPresenter by inject {
        parametersOf(this)
    }

    private var binding: FragmentUserLoanBinding? = null

    companion object {
        const val RECEBA_MAIS_OFFER = "br.com.cielo.recebaMais.userLoanFragment.offer"
        const val RECEBA_MAIS_USER_FROM_BANNER =
            "br.com.cielo.recebaMais.userLoanFragment.isUserFromBanner"

        fun create(
            offer: Offer,
            isUserLoanFromBanner: Boolean = false
        ): UserLoanFragment {
            val userLoanFragment = UserLoanFragment()
            userLoanFragment.addArgument(RECEBA_MAIS_OFFER, offer)
            userLoanFragment.addArgument(RECEBA_MAIS_USER_FROM_BANNER, isUserLoanFromBanner)
            return userLoanFragment
        }

        var isCheck: Boolean = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentUserLoanBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        userLoanPresenter.fetchContracts(isUserFromBanner)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_receba_mais_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_help) {
            sendGaHelpSelected()
            HelpMainActivity.create(
                requireActivity(),
                getString(R.string.text_rm_help_title),
                RM_HELP_ID
            )
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addLoanSimulationFragment(offerFromListResponse: Offer? = null) {
        if (isAttached()) {
            addUserLoanFragmentWithOffer(offer ?: offerFromListResponse)
        }
    }

    private fun addUserLoanFragmentWithOffer(paramOffer: Offer?) {
        val userLoanSimulationFragment = UserLoanSimulationFragment
            .create(paramOffer).apply {
                this.onStateListener = this@UserLoanFragment
            }

        userLoanSimulationFragment.addInFrame(childFragmentManager, R.id.frameLoanSimulationContent)
        binding?.linearLoanSimulationTitle?.visible()
        binding?.textLoanSimulationDescription?.visible()
    }

    private fun addLoanSummaryFragment(contracts: List<Contract>) {
        var isContracted = false
        contracts.forEach { contract ->
            if (contract.status == UserLoanPresenter.CONTRACTED_STATUS)
                isContracted = true
        }

        if (isContracted) {
            showSummaryContract()
        } else {
            when (contracts[ZERO].status) {
                UserLoanPresenter.ERROR_STATUS -> showNotApproved()
                UserLoanPresenter.PENDING_STATUS -> showWaitingForApproval(contracts[ZERO])
                UserLoanPresenter.INACTIVE_STATUS, UNKNOWN_STATUS -> showNetworkError()
            }
        }
    }


    override fun onShowLoading() {
        if (isAttached()) {
            binding?.apply {
                scrollUserLoanSimulation.gone()
                frameUserLoanProgress.root.visible()
            }
        }
    }

    override fun onHideLoading() {
        if (isAttached()) {
            binding?.apply {
                frameUserLoanProgress.root.gone()
                scrollUserLoanSimulation.visible()
            }
        }
    }

    override fun showSimulation() {
        addLoanSimulationFragment()
    }

    override fun showSimulationWithResponseOffer(offer: Offer?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Category.APP_CIELO, Action.ELEGIBILIDADE),
            label = listOf(SUCESSO, SUCCESS_200)
        )

        addUserLoanFragmentWithOffer(offer)
    }

    override fun showNotApproved() {
        binding?.apply {
            linearLoanSimulationTitle.gone()
            textLoanSimulationDescription.gone()
            MyCreditoNaoAprovadoFragment
                .create()
                .addInFrame(childFragmentManager, R.id.frameLoanSimulationContent)

            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
                action = listOf(Category.APP_CIELO, Action.ELEGIBILIDADE),
                label = listOf(ERRO, ERROR_401)
            )
        }
    }

    override fun showSummaryContract() {
        requireActivity().startActivity<FluxoNavegacaoRecebaMaisActivity>()
        requireActivity().finish()
    }

    override fun showWaitingForApproval(contract: Contract) {
        MyAguardandoAprovacaoFragment
            .create(contract, this)
            .addInFrame(childFragmentManager, R.id.frameLoanSimulationContent)
    }

    override fun showPendingContract(contracts: List<Contract>) {
        hideLoading()
        if (isAttached()) {
            isCheck = true
            binding?.apply {
                linearLoanSimulationTitle.gone()
                textLoanSimulationDescription.gone()
            }
            addLoanSummaryFragment(contracts)
        }
    }

    override fun showLoading() {
        onShowLoading()
    }

    override fun hideLoading() {
        onHideLoading()
    }

    override fun unauthorized() {
        sendGaExpired()
        onLogoutListener?.onLogout()
    }

    private fun sendGaExpired() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_SIMULE_AGORA_CATEGORY),
            action = listOf(GA_RM_RECEBA_MAIS),
            label = listOf(Label.MENSAGEM, GA_RM_SESSAO_EXPIRADA, ERROR_401)
        )
    }

    private fun sendGaHelpSelected() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.HEADER),
            label = listOf(Label.TOOLTIP)
        )
    }

    override fun showNetworkError() {
        if (isAttached()) {
            binding?.apply {
                scrollUserLoanSimulation.gone()
                errorHandlerCieloUserLoan.apply {
                    visible()
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                    cieloErrorTitle = getString(R.string.text_title_generic_error)
                    cieloErrorMessage = getString(R.string.text_message_generic_error)

                    errorButton?.setText(getString(R.string.text_try_again_label))
                    errorButton?.setOnClickListener {
                        gone()
                        showLoading()
                        userLoanPresenter.fetchContracts(isUserFromBanner)
                    }
                }
            }
        }
    }

    override fun showErrorHandler(errorMessage: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                scrollUserLoanSimulation.gone()
                errorHandlerCieloUserLoan.apply {
                    visible()
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                    setMessageError(errorMessage.errorCode, errorMessage.errorMessage)
                    configureActionClickListener {
                        LocalBroadcastManager.getInstance(requireActivity())
                            .sendBroadcast(Intent(MainBottomNavigationActivity.IMPERSONATE_USER_ACTION))
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    override fun showContractErrorHandler(errorMessage: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                scrollUserLoanSimulation.gone()
                errorHandlerCieloUserLoan.apply {
                    visible()
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                    setMessageError(errorMessage.errorCode, errorMessage.errorMessage)
                    configureActionClickListener {
                        sendGaButtonHiring()
                        hideErrorHandlerView()
                        userLoanPresenter.listOffers(UserPreferences.getInstance().token)
                    }
                }
            }
        }
    }

    private fun sendGaButtonHiring() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(GA_RM_RECEBA_MAIS_ACTION),
            label = listOf(Label.BOTAO, BUTTON_CONTRATAR)
        )
    }

    private fun hideErrorHandlerView() {
        if (isAttached())
            binding?.errorHandlerCieloUserLoan?.gone()
    }

    override fun showContractInternalError() {
        if (isAttached()) {
            binding?.apply {
                scrollUserLoanSimulation.gone()
                errorHandlerCieloUserLoan.apply {
                    visible()
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                    configureActionClickListener {
                        if (isAttached())
                            requireActivity().onBackPressed()
                    }
                }
            }
        }
    }
}