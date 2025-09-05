package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine.getNameMachine
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaAdicionalHorarioFragmentBinding
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InstalacaoMaquinaAdicionalHorarioFragment : BaseFragment(),
    InstalacaoMaquinaAdicionalHorarioContract.View, EngineNextActionListener {

    val presenter: InstalacaoMaquinaAdicionalHorarioPresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    private var binding: InstalacaoMaquinaAdicionalHorarioFragmentBinding? = null
    private var actionListener: ActivityStepCoordinatorListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            actionListener = context
            actionListener?.onTextChangeButton(getString(R.string.continuar))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InstalacaoMaquinaAdicionalHorarioFragmentBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()
        configureListeners()
        setInitialData()
        presenter.load()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        actionListener = null
    }

    private fun logScreenView() {
        analytics.logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_PERIOD)
    }

    private fun setInitialData() {
        arguments?.let { args ->
            args.parcelable<Availability>(ARG_PARAM_PERIOD)?.let { period ->
                val establishmentName = args.getString(ARG_PARAM_ESTABLISHMENT_NAME, EMPTY)
                val referencePoint = args.getString(ARG_PARAM_REFERENCE_POINT, EMPTY)
                presenter.setInitialData(period, establishmentName, referencePoint)
            }
        }
    }

    private fun configureListeners() {
        binding?.tiePeriodo?.setOnClickListener {
            presenter.chooseAvailability()
        }
    }

    override fun goToNextScreen(availability: Availability?, establishmentName: String?, referencePoint: String?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.SOLICITAR_MAQUININHA, getNameMachine().orEmpty()),
            label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, FOUR), CONTINUAR)
        )
        actionListener?.onNextStep(false, Bundle().apply {
            putParcelable(ARG_PARAM_PERIOD, availability)
            putString(ARG_PARAM_NAME_MACHINE, getNameMachine() ?: EMPTY_STRING)
            putString(ARG_PARAM_ESTABLISHMENT_NAME, establishmentName ?: EMPTY_STRING)
            putString(ARG_PARAM_REFERENCE_POINT, referencePoint ?: EMPTY_STRING)
        })
    }

    override fun setPeriod(period: String) {
        binding?.apply {
            tiePeriodo.hint = getString(R.string.hint_periodo)
            tiePeriodo.setText(period)
        }
    }

    override fun setInitialData(period: String, establishmentName: String, referencePoint: String) {
        setPeriod(period)
        binding?.apply {
            tieNomeDaFachada.setText(establishmentName)
            tiePontoDeReferencia.setText(referencePoint)
        }
    }

    override fun onClicked() {
        binding?.apply {
            presenter.onNextButtonClicked(
                tieNomeDaFachada.text.toString(),
                tiePontoDeReferencia.text.toString()
            )
        }
    }

    override fun hideLoading() {
        actionListener?.onHideLoading()
    }

    override fun showLoading() {
        actionListener?.onShowLoading()
    }

    override fun showPeriods(periods: List<String>, selectedPeriod: String?) {
        val pickerBS =
            PickerBottomSheetFragment.newInstance(this.getString(R.string.hint_periodo), periods, selectedPeriod = selectedPeriod)
                .apply {
                    onItemSelectedListener = object : PickerBottomSheetFragment.OnItemSelectedListener {
                        override fun onSelected(selectedItem: Int) {
                            val selectedInstallment = periods[selectedItem]
                            selectedInstallment.run {
                                presenter.onAvailabilitySelected(selectedItem, this)
                            }
                        }
                    }
                }
        pickerBS.show(parentFragmentManager, PICKER_BS)
    }

    override fun logout(msg: ErrorMessage) {
        actionListener?.onLogout()
    }

    override fun showError(error: ErrorMessage) {
        analytics.logException(
            screenName = AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_PERIOD,
            errorCode = error.httpStatus.toString(),
            errorMessage = error.errorMessage
        )
        actionListener?.onShowError(error)
    }

    override fun retry() = presenter.load()

    override fun showPeriodError() {
        binding?.tilPeriodo?.error = getString(R.string.please_select_time)
    }

    override fun showEstablishmentNameError() {
        binding?.tilNomeDaFachada?.error = getString(R.string.please_put_the_establishment_name)
    }

    companion object {
        private const val PICKER_BS = "pickerBS"
        fun create(bundle: Bundle?) = InstalacaoMaquinaAdicionalHorarioFragment().apply {
            this.arguments = bundle
        }
    }

}
