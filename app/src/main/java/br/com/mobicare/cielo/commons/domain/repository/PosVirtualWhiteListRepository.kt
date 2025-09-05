package br.com.mobicare.cielo.commons.domain.repository

import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse
import io.reactivex.Observable

interface PosVirtualWhiteListRepository {
    fun getPosVirtualWhiteList(): Observable<PosVirtualWhiteListResponse>
}