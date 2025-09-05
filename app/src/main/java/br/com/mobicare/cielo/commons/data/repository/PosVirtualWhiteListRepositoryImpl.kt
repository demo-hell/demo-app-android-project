package br.com.mobicare.cielo.commons.data.repository

import br.com.mobicare.cielo.commons.data.dataSource.PosVirtualWhiteListDataSource
import br.com.mobicare.cielo.commons.domain.repository.PosVirtualWhiteListRepository

class PosVirtualWhiteListRepositoryImpl(private val dataSource: PosVirtualWhiteListDataSource) :
    PosVirtualWhiteListRepository {

    override fun getPosVirtualWhiteList() = dataSource.getPosVirtualWhiteList()
}