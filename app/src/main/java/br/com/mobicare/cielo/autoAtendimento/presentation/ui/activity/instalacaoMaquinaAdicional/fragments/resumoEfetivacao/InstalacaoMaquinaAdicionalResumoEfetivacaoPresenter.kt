package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getAreaCode
import br.com.mobicare.cielo.commons.utils.getNumberPhone
import br.com.mobicare.cielo.commons.utils.phoneNumber
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone
import br.com.mobicare.cielo.orders.OrdersRepository
import br.com.mobicare.cielo.orders.domain.AddressOrderRequest
import br.com.mobicare.cielo.orders.domain.OrderRequest
import br.com.mobicare.cielo.orders.domain.OrdersResponse

class InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter(
    private val view: InstalacaoMaquinaAdicionalResumoEfetivacaoContract.View,
    private val repository: OrdersRepository
) : InstalacaoMaquinaAdicionalResumoEfetivacaoContract.Presenter {

    private var machine: MachineItemOfferResponse? = null
    private var amount: Int? = null
    private var address: MachineInstallAddressObj? = null
    private var personName: String? = null
    private var personPhoneNumber: String? = null
    private var period: Availability? = null
    private var storefront: String? = null
    private var landmark: String? = null

    override fun setData(
        machineItem: MachineItemOfferResponse,
        amount: Int,
        address: MachineInstallAddressObj,
        personName: String,
        personPhoneNumber: String,
        period: Availability,
        establishmentName: String,
        referencePoint: String
    ) {
        this.machine = machineItem
        this.amount = amount
        this.address = address
        this.personName = personName
        this.personPhoneNumber = personPhoneNumber
        this.period = period
        this.view.setSolicitation(machineItem, amount)
        this.view.setDeliveryAddress(address, period, establishmentName, referencePoint)
        this.view.setContact(personName, personPhoneNumber)
        this.storefront = establishmentName
        this.landmark = referencePoint
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
                storefront,
                landmark
            )

            this.personPhoneNumber?.let { itPhoneNumber ->
                val phoneUnmask = itPhoneNumber.phoneNumber()
                val areaCode = phoneUnmask.getAreaCode()
                val number = phoneUnmask.getNumberPhone()
                val listPhones = listOf(Phone(areaCode, number, TYPE_PHONE_BUSINESS))

                this.personName?.let { itPersonName ->
                    this.period?.let { itPeriod ->
                        this.machine?.let { itMachine ->
                            this.amount?.let { itAmount ->
                                val orderRequest = OrderRequest(
                                    addressOrderRequest,
                                    listPhones,
                                    itPersonName,
                                    itPeriod.code,
                                    itMachine.technology,
                                    itAmount
                                )
                                this.doRequest(orderRequest)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun doRequest(orderRequest: OrderRequest) {
        val token = UserPreferences.getInstance().token

        if (token.isNullOrEmpty()) {
            this.view.logout(ErrorMessage())
            return
        }

        this.repository.postOrders(token, orderRequest,
            object : APICallbackDefault<OrdersResponse, String> {
                override fun onStart() {
                    this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.hideLoading()
                    if (error.logout) {
                        this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.logout(error)
                    } else {
                        this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.showError(
                            error
                        )
                    }
                }

                override fun onSuccess(response: OrdersResponse) {
                    this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.hideLoading()
                    this@InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter.view.showSucessfull(
                        response.id,
                        response.days,
                        amount,
                        machine?.rentalAmount
                    )
                }
            })
    }

    companion object {
        const val TYPE_PHONE_BUSINESS = "COMERCIAL"
    }

}