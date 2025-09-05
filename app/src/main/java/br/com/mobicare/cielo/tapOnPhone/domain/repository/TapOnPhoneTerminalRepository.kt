package br.com.mobicare.cielo.tapOnPhone.domain.repository

import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalRequest
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import io.reactivex.Observable
import retrofit2.Response

interface TapOnPhoneTerminalRepository {
    fun activateTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest): Observable<Response<Void>>
    fun createTerminal(deviceFingerPrint: TapOnPhoneTerminalRequest): Observable<TapOnPhoneTerminalResponse>
    fun getTerminalInfo(
        token: String,
        deviceFingerPrint: TapOnPhoneTerminalRequest
    ): Observable<TapOnPhoneTerminalResponse>
}