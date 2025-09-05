package br.com.mobicare.cielo.suporteTecnico.domain.repo

import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import io.reactivex.Observable


interface TechnicalSupportRepository {

    fun fetchTechnicalSupportRepository(): Observable<List<SupportItem>>


}