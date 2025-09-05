package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.AvailabilityHelper
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.machine.domain.Availability
import br.com.mobicare.cielo.machine.domain.OrdersAvailabilityResponse
import br.com.mobicare.cielo.orders.OrdersRepository
import br.com.mobicare.cielo.pix.constants.EMPTY

class InstalacaoMaquinaAdicionalHorarioPresenter(
        private val view: InstalacaoMaquinaAdicionalHorarioContract.View,
        private val repository: OrdersRepository) : InstalacaoMaquinaAdicionalHorarioContract.Presenter {

    private var selectedAvailability: Availability? = null
    private var ordersAvailabilityList: List<Availability> = arrayListOf()
    private var establishmentName: String? = String()
    private var referencePoint: String? = String()

    override fun setInitialData(availability: Availability, establishmentName: String, referencePoint: String) {
        selectedAvailability = availability
        setEstablishmentNameAndReferencePoint(establishmentName, referencePoint)
        view.setInitialData(getFormattedAvailability(availability), establishmentName, referencePoint)
    }

    override fun load() {
        val token = UserPreferences.getInstance().token
        val authorization = Utils.authorization()

        if (token.isNullOrEmpty()) {
            this.view.logout(ErrorMessage())
            return
        }

        this.repository.loadOrdersAvailability(token,
                object : APICallbackDefault<OrdersAvailabilityResponse, String> {
                    override fun onStart() {
                        this@InstalacaoMaquinaAdicionalHorarioPresenter.view.showLoading()
                    }

                    override fun onSuccess(response: OrdersAvailabilityResponse) {
                        this@InstalacaoMaquinaAdicionalHorarioPresenter.ordersAvailabilityList = response.availabilityList
                        this@InstalacaoMaquinaAdicionalHorarioPresenter.view.hideLoading()
                    }

                    override fun onError(error: ErrorMessage) {
                        this@InstalacaoMaquinaAdicionalHorarioPresenter.view.hideLoading()
                        if (error.logout) {
                            this@InstalacaoMaquinaAdicionalHorarioPresenter.view.logout(error)
                        } else {
                            this@InstalacaoMaquinaAdicionalHorarioPresenter.view.showError(error)
                        }
                    }

                })
    }

    override fun onNextButtonClicked(establishmentName: String, referencePoint: String) {
        setEstablishmentNameAndReferencePoint(establishmentName, referencePoint)

        if (this.selectedAvailability == null) {
            this.view.showPeriodError()
            return
        }

        if (this.establishmentName.isNullOrEmpty()) {
            this.view.showEstablishmentNameError()
            return
        }

        this.view.showPeriodError()
        this.view.showEstablishmentNameError()
        this.view.goToNextScreen(this.selectedAvailability, this.establishmentName, this.referencePoint)
    }

    override fun chooseAvailability() {
        val list = this.ordersAvailabilityList.map { getFormattedAvailability(it) }.distinct()
        this.view.showPeriods(list, getFormattedAvailability(selectedAvailability))
    }

    fun onAvailabilitySelected(index: Int, period: String) {
        view.setPeriod(period)
        this.selectedAvailability = this.ordersAvailabilityList[index]
    }

    private fun setEstablishmentNameAndReferencePoint(establishmentName: String, referencePoint: String) {
        this.establishmentName = establishmentName
        this.referencePoint = referencePoint
    }

    private fun getFormattedAvailability(availability: Availability?) = availability?.let {
        "${AvailabilityHelper.formatDays(it)} - das ${AvailabilityHelper.formatTime(it)}"
    } ?: EMPTY

}