package br.com.mobicare.cielo.mySales

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef.CANCELADA
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.SaleCardBrand
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.CardBrandsBO
import br.com.mobicare.cielo.mySales.data.model.bo.HomeCardSummarySaleBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.recebaMais.domain.OwnerAddress

object MySalesFactory {
    const val ACCESS_TOKEN_MOCK = "123456"

    //region - generic objects
    private val genericSummary =
        Summary(
            totalQuantity = 1,
            totalAmount = 1000.0,
            totalNetAmount = null,
        )

    private val emptySummary =
        Summary(
            totalQuantity = 0,
            totalAmount = 0.0,
            totalNetAmount = null,
        )

    private val genericOwnerAddress =
        OwnerAddress(
            addressTypes = listOf(Any()),
            city = EMPTY,
            id = EMPTY,
            neighborhood = EMPTY,
            state = EMPTY,
            streetAddress = EMPTY,
            streetAddress2 = EMPTY,
            types = listOf(Any()),
            zipCode = EMPTY,
            number = EMPTY,
        )

    val genericSale =
        Sale(
            id = null,
            amount = 1000.0,
            cardBrandDescription = "HIPERCARD",
            terminal = EMPTY,
            paymentSolutionType = null,
            cardNumber = EMPTY,
            paymentType = EMPTY,
            cardBrand = EMPTY,
            date = EMPTY,
            nsu = EMPTY,
            status = EMPTY,
            authorizationCode = EMPTY,
            tid = EMPTY,
            paymentSolutionCode = 1,
            administrationFee = null,
            authorizationDate = EMPTY,
            cardBrandCode = null,
            channel = EMPTY,
            grossAmount = null,
            installments = null,
            mdrFee = null,
            mdrFeeAmount = null,
            merchantId = "CIELO",
            netAmount = null,
            paymentDate = EMPTY,
            paymentNode = 1L,
            paymentScheduleDate = EMPTY,
            paymentTypeCode = EMPTY,
            saleDate = EMPTY,
            saleGrossAmount = null,
            statusCode = null,
            transactionPixId = null,
            transactionId = null,
            truncatedCardNumber = null,
        )

    private val saleHistory =
        SaleHistory(
            merchantId = null,
            paymentNode = null,
            cnpjRoot = null,
            date = null,
            saleDate = null,
            saleCode = null,
            saleAuthorizationCode = null,
            saleGrossAmount = null,
            paymentType = null,
            paymentTypeCode = null,
            productType = null,
            cardBrand = EMPTY,
            cardBrandCode = EMPTY,
            amount = null,
            netAmount = null,
            grossAmount = null,
            nsu = EMPTY,
            orderNumber = null,
            transactionId = EMPTY,
            status = EMPTY,
            averageAmount = null,
            quantity = null,
            percentage = null,
            productTypeCode = EMPTY,
        )

    private val listOfCardBrands =
        listOf(
            CardBrand(code = ONE, name = "Test"),
            CardBrand(code = ONE, name = "Test"),
            CardBrand(code = ONE, name = "Test"),
            CardBrand(code = ONE, name = "Test"),
        )

    private val listOfPaymentTypes =
        listOf(
            PaymentType(value = "Test", name = "Test"),
            PaymentType(value = "Test", name = "Test"),
            PaymentType(value = "Test", name = "Test"),
        )

    private val listOfSaleCardBrands =
        listOf(
            SaleCardBrand(name = "Test", code = "1"),
            SaleCardBrand(name = "Test", code = "1"),
            SaleCardBrand(name = "Test", code = "1"),
            SaleCardBrand(name = "Test", code = "1"),
        )

    private val pagination =
        Pagination(
            pageNumber = 1L,
            pageSize = TWENTY_FIVE,
            totalElements = ONE,
            firstPage = true,
            lastPage = true,
            numPages = 1,
        )

    val estabelecimentoObj =
        EstabelecimentoObj(
            ec = EMPTY,
            tradeName = EMPTY,
            cnpj = EMPTY,
            hierarchyLevel = EMPTY,
            hierarchyLevelDescription = EMPTY,
        )

    val estabelecimentoObj2 =
        EstabelecimentoObj(
            ec = "CIELO",
            tradeName = EMPTY,
            cnpj = EMPTY,
            hierarchyLevel = EMPTY,
            hierarchyLevelDescription = EMPTY,
        )

    val quickFilter: QuickFilter =
        QuickFilter
            .Builder()
            .initialDate("2023-01-08")
            .finalDate("2023-01-08")
            .build()

    val canceledSaleQuickFilter =
        QuickFilter
            .Builder()
            .initialDate("2023-01-08")
            .finalDate("2023-01-08")
            .status(listOf(CANCELADA))
            .build()

    //endregion

    //region - generic BOs for responses

    private val cardBrandsBO = CardBrandsBO(listOfCardBrands)

    val genericSummarySalesBO =
        SummarySalesBO(
            summary = genericSummary,
            pagination = pagination,
            items = listOf(genericSale, genericSale),
        )

    val genericCanceledSummarySalesBO =
        CanceledSummarySalesBO(
            summary = genericSummary,
            pagination = pagination,
            items = listOf(CanceledSale(), CanceledSale()).toMutableList(),
        )

    val genericEmptyCanceledSummarySalesBO =
        CanceledSummarySalesBO(
            summary = genericSummary,
            pagination = pagination,
            items = mutableListOf(),
        )

    private val emptySummarySalesBO =
        SummarySalesBO(
            summary = emptySummary,
            pagination = null,
            items = listOf(),
        )

    val genericHomeCardSummarySaleBO =
        HomeCardSummarySaleBO(
            summary = genericSummary,
            lastSale = genericSale,
        )

    val genericResultSummarySalesHistoryBO =
        ResultSummarySalesHistoryBO(
            summary = emptySummary,
            pagination = null,
            items = null,
        )

    val resultSummarySaleHistoryBO =
        ResultSummarySalesHistoryBO(
            summary = emptySummary,
            pagination = null,
            items = listOf(saleHistory, saleHistory, saleHistory),
        )

    val genericSaleMerchantBO =
        SalesMerchantBO(
            address = genericOwnerAddress,
            companyName = EMPTY,
            cnpj = EMPTY,
        )

    val genericCardBrandsBO =
        CardBrandsBO(
            cardBrands = null,
        )

    val genericPaymentTypesBO =
        ResultPaymentTypesBO(
            cardBrands = null,
            paymentTypes = null,
        )

    val impersonateBO =
        ImpersonateResponse(
            accessToken = EMPTY,
            refreshToken = EMPTY,
            expiresIn = ONE,
            tokenType = EMPTY,
        )

    val saleMerchantBO =
        SalesMerchantBO(
            address = genericOwnerAddress,
            cnpj = EMPTY,
            companyName = "CIELO",
        )

    val resultPaymentTypesBO =
        ResultPaymentTypesBO(
            cardBrands = listOfSaleCardBrands,
            paymentTypes = listOfPaymentTypes,
        )

    //endregion

    //region - generic params
    val mySalesHomeParams =
        GetSalesDataParams(
            accessToken = EMPTY,
            authorization = EMPTY,
            pageSize = TWENTY_FIVE,
            page = null,
            quickFilter = quickFilter,
        )

    val canceledSaleParams =
        GetCanceledSalesParams(
            accessToken = EMPTY,
            pageSize = TWENTY_FIVE,
            page = 1L,
            quickFilter = quickFilter,
        )

    val getSalesHistoryParams =
        GetSalesHistoryParams(
            accessToken = EMPTY,
            authorization = EMPTY,
            quickFilter = quickFilter,
            type = EMPTY,
        )

    val getSaleMerchantParams =
        GetMerchantParams(
            access_token = EMPTY,
            authorization = EMPTY,
        )

    val genericSalesAndCardBrandFiltersParams =
        GetBrandsSalesFiltersParams(
            authorization = EMPTY,
            accessToken = EMPTY,
            quickFilter = quickFilter,
        )

    val merchantParams =
        GetMerchantParams(
            access_token = EMPTY,
            authorization = "TESTING",
        )

    //endregion

    val userObj: UserObj = UserObj()

    val genericAPIError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    val emptyResult = CieloDataResult.Empty()

    val summarySalesAPISuccess = CieloDataResult.Success(genericSummarySalesBO)
    val homeCardSummaryAPISuccess = CieloDataResult.Success(genericHomeCardSummarySaleBO)
    val saleHistoryAPISuccess = CieloDataResult.Success(resultSummarySaleHistoryBO)
    val saleMerchantAPISuccess = CieloDataResult.Success(genericSaleMerchantBO)

    val filterCardBrandAPISuccess = CieloDataResult.Success(genericCardBrandsBO)
    val filteredPaymentTypesAndCanceledSellsAPISuccess = CieloDataResult.Success(genericPaymentTypesBO)

    val genericCanceledAPISuccess = CieloDataResult.Success(genericCanceledSummarySalesBO)
    val genericEmptySummarySalesBO = CieloDataResult.Success(genericEmptyCanceledSummarySalesBO)

    val impersonateSuccess = CieloDataResult.Success(impersonateBO)
    val merchantSuccess = CieloDataResult.Success(saleMerchantBO)

    val cardBrandsFilterSuccess = CieloDataResult.Success(cardBrandsBO)
    val paymentTypeFilterSuccess = CieloDataResult.Success(resultPaymentTypesBO)
}
