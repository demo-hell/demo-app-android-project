package br.com.mobicare.cielo.suporteTecnico.data.repository

import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.RequestTicketSupportDataSource
import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository

class RequestTicketSupportRepositoryImpl(
    private val dataSource: RequestTicketSupportDataSource
): RequestTicketSupportRepository {

    override suspend fun getMerchant() =
        dataSource.getMerchant()

    override suspend fun getMerchantEquipaments() =
        dataSource.merchantSolutionsEquipments()

    override suspend fun getScheduleAvailability() =
        dataSource.getScheduleAvailability()

    override suspend fun postOrdersReplacements(request: OpenTicket) =
        dataSource.postOrdersReplacements(request)
}