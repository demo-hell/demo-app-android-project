package br.com.mobicare.cielo.suporteTecnico.domain.repo

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.ScheduleDataResponse
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

interface RequestTicketSupportRepository {

    suspend fun getMerchant() : CieloDataResult<UserOwnerSupportResponse>

    suspend fun getMerchantEquipaments() : CieloDataResult<TerminalsResponse>

    suspend fun getScheduleAvailability() : CieloDataResult<ScheduleDataResponse>

    suspend fun postOrdersReplacements( request: OpenTicket) : CieloDataResult<OrderReplacementResponse>
}