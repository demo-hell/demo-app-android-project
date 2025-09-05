package br.com.mobicare.cielo.home.utils

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import io.reactivex.Observable

class BrandsRepository(private val api: CieloAPIServices) {
    fun loadAllBrands(token: String): Observable<List<Solution>> = api.loadBrands(token)
}