package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.AvailabilityHelper.Companion.formatDays
import br.com.mobicare.cielo.commons.helpers.AvailabilityHelper.Companion.formatTime
import br.com.mobicare.cielo.commons.helpers.EditTextHelper.Companion.phoneMaskFormatter
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.TrocaMaquinaOpenRequestResumeBinding
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FtScreenSucessBottomSheet
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FtScreenSucessBottomSheet.Companion.TYPEREPLACEMACHINE
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OpenRequestResumeFragment :
    BaseFragment(), EngineNextActionListener, OpenRequestResumeContract.View {

    private var binding: TrocaMaquinaOpenRequestResumeBinding? = null
    private var actionListner: ActivityStepCoordinatorListener? = null

    companion object {

        private const val TAG_BS = "FtScreenSucessBottomSheet"
        fun create(bundle: Bundle?) = OpenRequestResumeFragment().apply {
            this.arguments = bundle
        }
    }

    val presenter: OpenRequestResumePresenter by inject {
        parametersOf(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            this.actionListner = context
            this.actionListner?.onTextChangeButton(getString(R.string.confirmar))
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.actionListner = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = TrocaMaquinaOpenRequestResumeBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            val orderNumber = args.getString(ARG_PARAM_ORDER_NUMBER)
            val serialNumber = args.getString(ARG_PARAM_SERIAL_NUMBER)
            val versionNumber = args.getString(ARG_PARAM_VERSION_MACHINE)
            val feesAndPlansMachine =
                args.getParcelable<TaxaPlanosMachine>(ARG_PARAM_MACHINE_ITEM_CHANGE) ?: return
            val machineAddressInstall =
                args.getParcelable<MachineInstallAddressObj>(ARG_PARAM_ADDRESS_CHOSEN) ?: return
            val personName = args.getString(ARG_PARAM_PERSON_NAME) ?: return
            val personPhoneNumber = args.getString(ARG_PARAM_PERSON_PHONE_NUMBER) ?: return
            val period = args.getParcelable<Availability>(ARG_PARAM_PERIOD) ?: return
            val establishmentName = args.getString(ARG_PARAM_ESTABLISHMENT_NAME) ?: return
            val referencePoint = args.getString(ARG_PARAM_REFERENCE_POINT) ?: return

            presenter.setData(
                versionNumber,
                feesAndPlansMachine,
                machineAddressInstall,
                personName,
                personPhoneNumber,
                period,
                orderNumber,
                serialNumber,
                establishmentName,
                referencePoint
            )
        }
    }

    override fun onClicked() {
        this.presenter.onConfirmButtonClicked()
    }

    override fun retry() {
        this.presenter.onConfirmButtonClicked()
    }

    override fun showLoading() {
        this.actionListner?.onShowLoading()
    }

    override fun hideLoading() {
        this.actionListner?.onHideLoading()
    }

    override fun logout(msg: ErrorMessage) {
        this.actionListner?.onLogout()
    }

    override fun showError(error: ErrorMessage) {
        logException(error)
        this.actionListner?.onShowError(error)
    }

    override fun showRentalMachineErrorMessage() {
    }

    override fun setRentSolicitation(
        machineName: String?,
        logicalNumber: String,
        versionNumber: String
    ) {
        binding?.apply {
            rentSolicitationLayout.visible()
            purchaseSolicitationLayout.gone()
            rentMachineNameText.text = machineName
            rentLogicalNumberText.text = logicalNumber
            rentMatchineVersionText.text = versionNumber
        }
    }

    override fun setPurchaseSolicitation(
        machineName: String?,
        serialNumber: String,
        orderNumber: String
    ) {
        binding?.apply {
            rentSolicitationLayout.gone()
            purchaseSolicitationLayout.visible()
            purchaseMachineNameText.text = machineName
            purchaseSerialNumberText.text = serialNumber
            purchaseOrderNumberText.text = orderNumber
        }
    }

    override fun setDeliveryAddress(
        address: MachineInstallAddressObj,
        period: Availability,
        establishmentName: String,
        referencePoint: String
    ) {
        binding?.includeLayoutDeliveryAddress?.apply {
            enderecoEntregaText.text = address.streetAddress
            bairroText.text = address.neighborhood
            cepText.text = address.zipcode
            cidadeText.text = address.city
            estadoText.text = address.state
            periodoText.text = getString(
                R.string.change_machine_txt_period,
                formatDays(period),
                formatTime(period)
            )
            nomeDaFachadaText.text =
                if (establishmentName.trim().isNotEmpty()) establishmentName else DASH
            pontoDeReferenciaText.text =
                if (referencePoint.trim().isNotEmpty()) referencePoint else DASH
        }
    }

    override fun setContact(personName: String, personPhoneNumber: String) {
        binding?.includeLayoutContactData?.apply {
            nomeContatoText.text = personName
            telefoneText.text = phoneMaskFormatter(personPhoneNumber).formattedText.string
        }
    }

    override fun showSucessfull(protocol: String?, hours: Int?) {
        actionListner?.let {
            logScreenView()
            val ftSuccessBS = FtScreenSucessBottomSheet.newInstanceInstallOrReplaceMachine(
                TYPEREPLACEMACHINE,
                it,
                protocol,
                hours
            )
            ftSuccessBS.show(requireActivity().supportFragmentManager, TAG_BS)
        }
    }

    private fun logScreenView() {
        TechnicalSupportAnalytics.logScreenView(
            TechnicalSupportAnalytics.ScreenView.OPEN_REQUEST_SUCCESS
        )
    }

    private fun logException(error: ErrorMessage) {
        TechnicalSupportAnalytics.logException(
            screenName = TechnicalSupportAnalytics.ScreenView.OPEN_REQUEST,
            errorCode = error.httpStatus.toString(),
            errorMessage = error.errorMessage
        )
    }

}