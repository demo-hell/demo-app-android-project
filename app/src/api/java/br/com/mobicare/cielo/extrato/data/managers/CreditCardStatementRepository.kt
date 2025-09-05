package br.com.mobicare.cielo.extrato.data.managers

import br.com.mobicare.cielo.extrato.data.clients.api.StatementApiDataSource

class CreditCardStatementRepository private
     constructor(val remoteDataSource: StatementApiDataSource):
        StatementRepository {

    override fun statements(initialDt: String, finalDt: String, pageSize: Int, page: Int, merchantId: String?,
                            accessToken: String, proxyCard: String) =
        remoteDataSource.getStatements(initialDt, finalDt, pageSize, page, merchantId, accessToken, proxyCard)


    companion object {
        fun getInstance(remoteDataSource: StatementApiDataSource): CreditCardStatementRepository {
            return CreditCardStatementRepository(remoteDataSource)
        }
    }

}