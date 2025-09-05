package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines

import android.os.Handler
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import kotlin.properties.Delegates

class OpenRequestMachinesPresenter(
        private val mView: OpenRequestMachinesContract.View,
        private val mRepository: MachineRepository) : OpenRequestMachinesContract.Presenter {

    override var taxaPlanosMachineSeleted: TaxaPlanosMachine? = null

    private var terminalsResponse: TerminalsResponse? = null

    private var currentState by Delegates.observable<OpenRequestMachineViewState>(DefaultViewState(), {_, oldState, newState ->
        if (newState != oldState) {
            this.mView.renderState(newState)
        }
    })

    override fun onCleared() {
        mRepository.disposable()
    }
    override fun onNextButtonClicked(versionMachine: String?, serialNumber: String?, orderNumber: String?) {
        taxaPlanosMachineSeleted?.let {
            mView.onNextStep(versionMachine, serialNumber, orderNumber, it)
        }
    }

    override fun loadMerchantSolutionsEquipments() {
        val token: String = UserPreferences.getInstance().token ?: ""

        if (token.isEmpty()) {
            mView.logout(ErrorMessage())
            return
        }

        mRepository.loadMerchantSolutionsEquipments(token,
                object : APICallbackDefault<TerminalsResponse, String> {
                    override fun onStart() {
                        super.onStart()
                        this@OpenRequestMachinesPresenter.currentState = LoadingViewState()
                    }

                    override fun onError(error: ErrorMessage) {
                        when {
                            error.logout -> mView.logout(error)
                            else -> /*mView.showError(error) */ {
                                this@OpenRequestMachinesPresenter.currentState = ShowErrorMachineViewState(error)
                            }
                        }
                    }

                    override fun onSuccess(response: TerminalsResponse) {
                        showMachines(response)
                    }
                })
    }

    private fun loadMockData() {
        val listTaxaPlanosMachine : List<TaxaPlanosMachine> =  listOf(
                TaxaPlanosMachine("VX 510 6MB PCI", "09998243",
                        "2", 100.0, "VX 510 POSWEB M06", "VX 510 6MB PCI",
                    "VX 510 6MB PCI",  "WIFI", true),
                TaxaPlanosMachine("I5100", "25719601",
                        "4", 100.0, "I5100", "I5100",
                    "I5100", "WIFI", true),
                TaxaPlanosMachine("ECOMMERCE", "10009879",
                        "7", 100.0, "ECOMMERCE", "ECOMMERCE",
                    "ECOMMERCE","WIFI", true),
                TaxaPlanosMachine("POS IWL281 192MB GPRS CTLS", "09998243",
                        "8", 100.0, "COMERCIO ELETRONICO", "I 5100 2+4 POSWEB V32",
                    "POS IWL281", "GPRS", true),
                TaxaPlanosMachine("VX 510 6MB PCI", "20191330",
                        "2", 100.0, "VX 510", "VX 510 POSWEB M06 PCI",
                    "VX 510 6MB PCI","DIAL", false)
        )
        this.currentState = LoadingViewState()
        val response = TerminalsResponse(false, listTaxaPlanosMachine)
        Handler().postDelayed({
            showMachines(response)
        }, 2000)
    }

    private fun showMachines(response: TerminalsResponse) {
        this.terminalsResponse = response
        if (response.rentalEquipments != null) {
            if (response.rentalEquipments) {
                this@OpenRequestMachinesPresenter.currentState = ShowRentalViewState(response)
            }
            else {
                this@OpenRequestMachinesPresenter.currentState = ShowBoughtViewState(response)
            }
        }
        else {
            this@OpenRequestMachinesPresenter.currentState = ShowRentalViewState(response)
        }
    }

    override fun onVersionNumberChanged(versionNumberText: String) {
        this.terminalsResponse?.let {
            if (versionNumberText.isNullOrEmpty() && this.taxaPlanosMachineSeleted != null) {
                this.currentState = DisableNextButtonMachiveViewState(ShowRentalViewState(null))
            }
            else {
                this.currentState = EnableNextButtonMachiveViewState(ShowRentalViewState(null))
            }
        }
    }

    override fun onOrderAndSerialNumberChanged(orderNumberText: String, serialNumberText: String) {
        this.terminalsResponse?.let {
            if (orderNumberText.isNotBlank() && serialNumberText.isNotBlank() && this.taxaPlanosMachineSeleted != null) {
                this.currentState = EnableNextButtonMachiveViewState(ShowBoughtViewState(null))
            }
            else {
                this.currentState = DisableNextButtonMachiveViewState(ShowBoughtViewState(null))
            }
        }
    }

    override fun onTaxaPlanosMarchineSelected(item: TaxaPlanosMachine, orderNumberText: String, serialNumberText: String, versionNumberText: String) {
        this.taxaPlanosMachineSeleted = item
        this.terminalsResponse?.let {
            if (item.replacementAllowed == true) {
                if (it.rentalEquipments == true) {
                    this.onVersionNumberChanged(versionNumberText)
                }
                else {
                    this.onOrderAndSerialNumberChanged(orderNumberText, serialNumberText)
                }
            }
            else {
                this.currentState = ShowCannotChangeMachineViewState()
            }
        }
    }

}