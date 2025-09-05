package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

open class OpenRequestMachineViewState(
        val isLoading: Boolean,
        val isShowSerialNumber: Boolean,
        val isShowOrderNumber: Boolean,
        val isShowVersionNumber: Boolean,
        val isShowLinkForRentalMachine: Boolean,
        val isShowLinkForBoughtMachine: Boolean,
        val isShowCannotChangeMachine: Boolean,
        val errorMessage: ErrorMessage? = null,
        val terminalsResponse: TerminalsResponse? = null,
        val isNextButtonEnabled: Boolean = false)

class DefaultViewState : OpenRequestMachineViewState(
        false,
        false,
        false,
        false,
        false,
        false,
        false)

class LoadingViewState : OpenRequestMachineViewState(
                true,
                false,
                false,
                false,
                false,
                false,
                 false)

class ShowBoughtViewState(terminalsResponse: TerminalsResponse?) : OpenRequestMachineViewState(
        false,
        true,
        true,
        false,
        false,
        true,
        false,
        terminalsResponse = terminalsResponse)

class ShowRentalViewState(terminalsResponse: TerminalsResponse?) : OpenRequestMachineViewState(
        false,
        false,
        false,
        true,
        true,
        false,
        false,
        terminalsResponse = terminalsResponse)

class ShowCannotChangeMachineViewState : OpenRequestMachineViewState(
        false,
        false,
        false,
        false,
        false,
        false,
        true)

class ShowErrorMachineViewState(errorMessage: ErrorMessage) : OpenRequestMachineViewState(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        errorMessage = errorMessage)

class EnableNextButtonMachiveViewState(state: OpenRequestMachineViewState) : OpenRequestMachineViewState(
        state.isLoading,
        state.isShowSerialNumber,
        state.isShowOrderNumber,
        state.isShowVersionNumber,
        state.isShowLinkForRentalMachine,
        state.isShowLinkForBoughtMachine,
        state.isShowCannotChangeMachine,
        isNextButtonEnabled = true)

class DisableNextButtonMachiveViewState(state: OpenRequestMachineViewState) : OpenRequestMachineViewState(
        state.isLoading,
        state.isShowSerialNumber,
        state.isShowOrderNumber,
        state.isShowVersionNumber,
        state.isShowLinkForRentalMachine,
        state.isShowLinkForBoughtMachine,
        state.isShowCannotChangeMachine,
        isNextButtonEnabled = false)


