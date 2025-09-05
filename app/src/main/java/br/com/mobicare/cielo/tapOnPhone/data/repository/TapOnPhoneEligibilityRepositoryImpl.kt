package br.com.mobicare.cielo.tapOnPhone.data.repository

import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneEligibilityDataSource
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneEligibilityRepository

class TapOnPhoneEligibilityRepositoryImpl(private val dataSource: RemoteTapOnPhoneEligibilityDataSource) :
    TapOnPhoneEligibilityRepository {

    override fun getEligibilityStatus() = dataSource.getEligibilityStatus()
}