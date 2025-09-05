package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.machine.domain.Availability

interface InstalacaoMaquinaAdicionalHorarioContract {

    interface View : IAttached {
        fun goToNextScreen(availability: Availability?, establishmentName: String?, referencePoint: String?)
        fun setPeriod(period: String)
        fun setInitialData(period: String, establishmentName: String, referencePoint: String)
        fun showLoading()
        fun hideLoading()
        fun logout(msg: ErrorMessage)
        fun showError(error: ErrorMessage)
        fun showPeriods(periods: List<String>, selectedPeriod: String?)
        fun showPeriodError()
        fun showEstablishmentNameError()
    }

    interface Presenter {
        fun load()
        fun setInitialData(availability: Availability, establishmentName: String, referencePoint: String)
        fun onNextButtonClicked(establishmentName: String, referencePoint: String)
        fun chooseAvailability()
    }

}