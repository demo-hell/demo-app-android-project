package br.com.mobicare.cielo.tapOnPhone.domain.repository

import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import io.reactivex.Observable

interface TapOnPhoneEligibilityRepository {
    fun getEligibilityStatus(): Observable<TapOnPhoneEligibilityResponse>
}