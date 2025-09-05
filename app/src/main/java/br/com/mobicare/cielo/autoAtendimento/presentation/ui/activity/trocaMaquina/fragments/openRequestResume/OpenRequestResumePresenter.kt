package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume

import android.util.Log
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.phoneNumber
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone
import br.com.mobicare.cielo.orders.OrdersRepository
import br.com.mobicare.cielo.orders.domain.AddressOrderRequest
import br.com.mobicare.cielo.orders.domain.OrderReplacementRequest
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine

class OpenRequestResumePresenter(
        private val view: OpenRequestResumeContract.View,
        private val repository: OrdersRepository) : OpenRequestResumeContract.Presenter{

    private var versionMachine: String? = ""
    private var machine: TaxaPlanosMachine? = null
    private var address: MachineInstallAddressObj? = null
    private var personName: String? = null
    private var personPhoneNumber: String? = null
    private var period: Availability? = null
    private var orderNumber: String? = null
    private var serialNumber: String? = null
    private var establishmentName: String? = null
    private var referencePoint: String? = null

    override fun setData(
        versionMachine: String?,
        machineItem: TaxaPlanosMachine,
        address: MachineInstallAddressObj,
        personName: String,
        personPhoneNumber: String,
        period: Availability,
        orderNumber: String?,
        serialNumber: String?,
        establishmentName: String,
        referencePoint: String
    ) {
        this.machine = machineItem
        this.address = address
        this.personName = personName
        this.personPhoneNumber = personPhoneNumber
        this.period = period
        this.versionMachine = versionMachine
        this.orderNumber = orderNumber
        this.serialNumber = serialNumber
        this.establishmentName = establishmentName
        this.referencePoint = referencePoint
        this.view.setDeliveryAddress(address, period, establishmentName, referencePoint)
        this.view.setContact(personName, personPhoneNumber)

        versionMachine?.let {
            this.view.setRentSolicitation(machineItem.name, "${machineItem.logicalNumber}${machineItem.logicalNumberDigit}", it)
        }

        orderNumber?.let { itOrderNumber ->
            serialNumber?.let { itSerialNumber ->
                this.view.setPurchaseSolicitation(machineItem.name, itSerialNumber, itOrderNumber)
            }
        }
    }

    override fun onConfirmButtonClicked() {
        this.address?.let { itAddress ->
            val addressOrderRequest = AddressOrderRequest(
                    itAddress.streetAddress,
                    itAddress.referencePoint,
                    itAddress.neighborhood,
                    itAddress.numberAddress,
                    itAddress.city,
                    itAddress.state,
                    itAddress.zipcode,
                    establishmentName,
                    referencePoint
            )

            val phones = arrayListOf<Phone>()
            this.personPhoneNumber?.let { itPhoneNumber ->
                val phoneUnmask = itPhoneNumber.phoneNumber()
                val areaCode = phoneUnmask.substring(0, 2)
                val number = phoneUnmask.substring(3)
                val phone = Phone(areaCode, number, "CELLPHONE")
                phones.add(phone)
                phones.add(phone)
            }

            this.personName?.let { itPersonName ->
                this.period?.let { itPeriod ->
                    this.machine?.let { itMachine ->
                        val orderRequest = OrderReplacementRequest(
                                addressOrderRequest,
                                phones,
                                itPersonName,
                                itPeriod.code,
                                itMachine.logicalNumber,
                                itMachine.logicalNumberDigit,
                                this.versionMachine,
                                itMachine.technology,
                                serialNumber = this.serialNumber,
                                orderId = this.orderNumber)
                        this.doRequest(orderRequest)
                    }
                }

            }
        }
    }

    private fun doRequest(orderRequest : OrderReplacementRequest) {
        val token = UserPreferences.getInstance().token

        if (token.isNullOrEmpty()) {
            this.view.logout(ErrorMessage())
            return
        }

        this.repository.postOrdersReplacements(token, orderRequest,
                object : APICallbackDefault<OrderReplacementResponse, String> {
                    override fun onStart() {
                        this@OpenRequestResumePresenter.view.showLoading()
                    }

                    override fun onError(error: ErrorMessage) {
                        this@OpenRequestResumePresenter.view.hideLoading()
                        if (error.logout) {
                            this@OpenRequestResumePresenter.view.logout(error)
                        } else {
                            this@OpenRequestResumePresenter.view.showError(error)
                        }
                    }

                    override fun onSuccess(response: OrderReplacementResponse) {
                        this@OpenRequestResumePresenter.view.hideLoading()
                        this@OpenRequestResumePresenter.view.showSucessfull(response.id, response.hours)
                    }
                })
    }

}