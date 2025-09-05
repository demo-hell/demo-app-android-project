package br.com.mobicare.cielo.commons.data.mapper

import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.me.MeResponse

object MapperEstablishmentObj {

    fun mapToEstablishment(meResponse: MeResponse): EstabelecimentoObj {
        return EstabelecimentoObj(
            meResponse.activeMerchant.id,
            meResponse.activeMerchant.tradingName,
            meResponse.activeMerchant.cnpj?.number,
            meResponse.activeMerchant.hierarchyLevel
        )
    }

}