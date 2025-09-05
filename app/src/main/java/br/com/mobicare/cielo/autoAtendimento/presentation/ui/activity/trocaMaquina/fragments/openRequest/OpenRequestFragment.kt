package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import br.com.mobicare.cielo.autoAtendimento.domain.model.EstablishmentSelectedObj
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.CALL_OPENING_MACHINE
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.ERROR_ADDRESS
import br.com.mobicare.cielo.changeEc.activity.MainImpersonateBottomSheetDialog
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_ESTABLISHMENT
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.TrocaMaquinaOpenRequestFragmentBinding
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class OpenRequestFragment : BaseFragment(), OpenRequestContract.View, EngineNextActionListener {

    private var _binding: TrocaMaquinaOpenRequestFragmentBinding? = null
    private val binding get() = _binding!!

    private var actionJorneyListner: ActivityStepCoordinatorListener? = null
    private var actionEventListener: BaseView? = null

    val presenter: OpenRequestPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(bundle: Bundle?) = OpenRequestFragment().apply {
            this.arguments = bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TrocaMaquinaOpenRequestFragmentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionJorneyListner?.enableNextButton(true)
        showLoading()
        configureSpacer()
        configureListeners()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun configureListeners() {
        binding.textTitleChange.setOnClickListener {
            val dialog = MainImpersonateBottomSheetDialog()
            dialog.setListener(object: MainImpersonateBottomSheetDialog.MainImpersonateBottomSheetDialogListener {
                override fun onImpersonated() {
                    presenter.loadEstablishment(MenuPreference.instance.getLoginObj())
                }
            })
            dialog.show(requireFragmentManager(), MainImpersonateBottomSheetDialog::class.java.simpleName)
        }
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
        presenter.loadEstablishment(MenuPreference.instance.getLoginObj())

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            this.actionJorneyListner = context
            this.actionJorneyListner?.setButtonName("Abrir solicitação")
        }
        if (context is BaseView) {
            this.actionEventListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.actionJorneyListner = null
        this.actionEventListener = null
    }

    private fun configureSpacer() {
        this.view?.parent?.let {
            if (it is FrameLayout) {
                it.parent?.let { itLayout ->
                    if (itLayout is ScrollView) {
                        itLayout.post {
                            binding.constraintMain.apply {
                                val lp = layoutParams
                                if (lp.height < itLayout.height) {
                                    lp.height = itLayout.height
                                    layoutParams = lp
                                    requestLayout()
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    //region OpenRequestContract.View

    override fun showLoading() {
        actionEventListener?.showLoading()
    }

    override fun hideLoading() {
        actionEventListener?.hideLoading()
    }
    override fun showEstablishment(establishment: EstabelecimentoObj) {
        binding.textDescriptionTradeName.text = establishment.tradeName
        hideLoading()
    }

    override fun showError(error: ErrorMessage?) {
        actionEventListener?.showError(error)
    }

    override fun logout(msg: ErrorMessage?) {
        actionEventListener?.logout(msg)
    }

    override fun onNextStep(establishment: EstablishmentSelectedObj) {
        logOpenRequestButtonClick()
        this.actionJorneyListner?.onNextStep(false, Bundle().apply {
            putParcelable(ARG_PARAM_ESTABLISHMENT, establishment)
        })
    }
    //endregion

    //region EngineNextActionListener
    override fun onClicked() {
        GA4.logWarningDisplayContent(CALL_OPENING_MACHINE, ERROR_ADDRESS)
        presenter.onNextButtonClicked()
    }
    //endregion

    private fun logScreenView() {
        TechnicalSupportAnalytics.logScreenView(TechnicalSupportAnalytics.ScreenView.OPEN_REQUEST)
    }

    private fun logOpenRequestButtonClick() {
        TechnicalSupportAnalytics.logOpenRequestClick()
    }

}