package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ChangeOrRenewShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.openFinance.domain.repository.ChangeOrRenewShareRemoteRepository

class ChangeOrRenewShareRepositoryImpl(private val dataSource: ChangeOrRenewShareDataSource) :
    ChangeOrRenewShareRemoteRepository {
    override suspend fun changeOrRenewShare(changeOrRenewShareRequest: ChangeOrRenewShareRequest):
            CieloDataResult<ChangeOrRenewShare> {
        return dataSource.changeOrRenewShare(changeOrRenewShareRequest)
    }
}