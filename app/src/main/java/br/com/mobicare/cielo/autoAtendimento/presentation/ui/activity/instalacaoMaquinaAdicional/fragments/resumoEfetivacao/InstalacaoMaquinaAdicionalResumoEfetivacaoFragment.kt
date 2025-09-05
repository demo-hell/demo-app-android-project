package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine.getNameMachine
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.AvailabilityHelper
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaAdicionalResumoEfetivacaoFragmentBinding
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FtScreenSucessBottomSheet
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FtScreenSucessBottomSheet.Companion.TYPEINSTALLMACHINE
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InstalacaoMaquinaAdicionalResumoEfetivacaoFragment : BaseFragment(),
    EngineNextActionListener,
    InstalacaoMaquinaAdicionalResumoEfetivacaoContract.View {

    val presenter: InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    private var actionListener: ActivityStepCoordinatorListener? = null

    private var _binding: InstalacaoMaquinaAdicionalResumoEfetivacaoFragmentBinding? = null
    private val binding get() = _binding!!

    private val machineName get() = arguments?.getString(ARG_PARAM_NAME_MACHINE).orEmpty()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            actionListener = context
            actionListener?.onTextChangeButton(getString(R.string.confirmar))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InstalacaoMaquinaAdicionalResumoEfetivacaoFragmentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()
        setData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        actionListener = null
    }

    private fun logScreenView() {
        analytics.logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_CONFIRM_DATA)
    }

    private fun setData() {
        arguments?.let {
            val machineItemOfferResponse = it.parcelable<MachineItemOfferResponse>(ARG_PARAM_MACHINE_ITEM) ?: return
            val amountMachine = it.getInt(ARG_PARAM_AMOUNT_MACHINE)
            val chosenAddress = it.parcelable<MachineInstallAddressObj>(ARG_PARAM_ADDRESS_CHOSEN) ?: return
            val personName = it.getString(ARG_PARAM_PERSON_NAME) ?: return
            val personPhoneNumber = it.getString(ARG_PARAM_PERSON_PHONE_NUMBER) ?: return
            val period = it.parcelable<Availability>(ARG_PARAM_PERIOD) ?: return
            val establishmentName = it.getString(ARG_PARAM_ESTABLISHMENT_NAME) ?: return
            val referencePoint = it.getString(ARG_PARAM_REFERENCE_POINT) ?: return

            presenter.setData(
                machineItemOfferResponse,
                amountMachine,
                chosenAddress,
                personName,
                personPhoneNumber,
                period,
                establishmentName,
                referencePoint
            )
        }
    }

    override fun retry() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(Action.SOLICITAR_MAQUININHA, getNameMachine().orEmpty()),
            label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, FIVE), RETRY)
        )

        presenter.onConfirmButtonClicked()
    }

    override fun onClicked() {
        presenter.onConfirmButtonClicked()

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(Action.SOLICITAR_MAQUININHA, getNameMachine().orEmpty()),
            label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, FIVE), CONTINUAR)
        )
    }

    override fun setSolicitation(machine: MachineItemOfferResponse, amount: Int) {
        binding.apply {
            solMaqNameText.text = machine.title
            solMaqPriceText.text = getString(
                R.string.request_machine_rental_amount_per_month,
                machine.rentalAmount.toPtBrRealString()
            )
            solMaqQuantidade.text = amount.toString()
        }
    }

    override fun setDeliveryAddress(
        address: MachineInstallAddressObj,
        period: Availability,
        establishmentName: String,
        referencePoint: String
    ) {
        binding.includeEnderecoEntrega.apply {
            enderecoEntregaText.text = address.streetAddress
            bairroText.text = address.neighborhood
            cepText.text = address.zipcode
            cidadeText.text = address.city
            estadoText.text = address.state
            periodoText.text = getString(
                R.string.request_machine_period,
                AvailabilityHelper.formatDays(period),
                AvailabilityHelper.formatTime(period)
            )
            nomeDaFachadaText.text = establishmentName
            pontoDeReferenciaText.text = referencePoint
        }
    }

    override fun setContact(personName: String, personPhoneNumber: String) {
        binding.includeDadosContato.apply {
            nomeContatoText.text = personName
            telefoneText.text = EditTextHelper.phoneMaskFormatter(personPhoneNumber).formattedText.string
        }
    }

    override fun hideLoading() {
        actionListener?.onHideLoading()
    }

    override fun showLoading() {
        actionListener?.onShowLoading()
    }

    override fun logout(msg: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.CALLBACK, Action.SOLICITAR_MAQUININHA, machineName),
            label = listOf(ERRO, ERROR_401)
        )
        actionListener?.onLogout()
    }

    override fun showError(error: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.CALLBACK, Action.SOLICITAR_MAQUININHA, machineName),
            label = listOf(ERRO, "${error.httpStatus}")
        )

        analytics.logException(
            screenName = AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_CONFIRM_DATA,
            errorCode = error.httpStatus.toString(),
            errorMessage = error.errorMessage
        )

        actionListener?.onShowError(error)
    }

    override fun showSucessfull(protocol: String?, hours: Int?, amount: Int?, rentalPrice: Double?) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.CALLBACK, Action.SOLICITAR_MAQUININHA, getNameMachine().orEmpty()),
            label = listOf(SUCESSO)
        )

        analytics.run {
            logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_SUCCESS)
            logRequestMachinePurchase(
                machineName = machineName,
                quantity = amount ?: ZERO,
                value = rentalPrice ?: ZERO_DOUBLE
            )
        }

        actionListener?.let {
            val ftSuccessBS = FtScreenSucessBottomSheet.newInstanceInstallOrReplaceMachine(
                TYPEINSTALLMACHINE,
                it,
                protocol,
                hours
            )
            ftSuccessBS.show(parentFragmentManager, SUCCESS_BOTTOM_SHEET_TAG)
        }
    }

    companion object {
        private const val ERROR_401 = "401"
        private const val SUCCESS_BOTTOM_SHEET_TAG = "FtScreenSuccessBottomSheet"
        fun create(bundle: Bundle?) = InstalacaoMaquinaAdicionalResumoEfetivacaoFragment().apply {
            arguments = bundle
        }
    }

}
