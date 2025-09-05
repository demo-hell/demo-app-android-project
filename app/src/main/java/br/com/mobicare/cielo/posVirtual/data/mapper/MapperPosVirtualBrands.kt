package br.com.mobicare.cielo.posVirtual.data.mapper

import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.posVirtual.data.model.response.*
import br.com.mobicare.cielo.posVirtual.domain.model.*
import br.com.mobicare.cielo.posVirtual.domain.model.Brand
import br.com.mobicare.cielo.posVirtual.domain.model.Condition
import br.com.mobicare.cielo.posVirtual.domain.model.Product

object MapperPosVirtualBrands {

    fun mapToBrands(solutionsResponse: List<PosVirtualBrandsResponse>?): Solutions? {
        return solutionsResponse?.let { list ->
            Solutions(
                solutions = list.map {
                    mapSolutions(it)
                }
            )
        }
    }

    private fun mapSolutions(solution: PosVirtualBrandsResponse): Solution {
        return solution.let {
            Solution(
                banks = mapBanks(it.banks),
                name = it.name
            )
        }
    }


    private fun mapBanks(listBanks: List<BankResponse>?): List<Bank>? {
        return listBanks?.let { list ->
            list.map {
                Bank(
                    accountId = it.accountId,
                    name = it.name ?: EMPTY,
                    accountDigit = it.accountDigit,
                    accountNumber = it.accountNumber,
                    accountExt = concatNumberAndDigit(it.accountNumber, it.accountDigit),
                    agencyDigit = it.agencyDigit,
                    agencyNumber = it.agency,
                    agencyExt = concatNumberAndDigit(it.agency, it.agencyDigit),
                    brands = mapBrands(it.brands),
                    code = it.code,
                    digitalAccount = it.digitalAccount,
                    imgSource = it.imgSource,
                    savingsAccount = it.savingsAccount,
                )
            }
        }
    }

    private fun mapBrands(listBrands: List<BrandResponse>?): List<Brand>? {
        return listBrands?.let { list ->
            list.map {
                Brand(
                    code = it.code,
                    imgSource = it.imgSource,
                    name = it.name,
                    products = mapProducts(it.products)
                )
            }
        }
    }

    private fun mapProducts(listProducts: List<ProductResponse>?): List<Product>? {
        return listProducts?.let { list ->
            list.map {
                Product(
                    conditions = mapCondition(it.conditions),
                    name = it.name,
                    flexibleTerm = it.prazoFlexivel,
                    pixType = it.pixType,
                    productCode = it.productCode
                )
            }
        }
    }

    private fun mapCondition(listConditions: List<ConditionResponse>?): List<Condition>? {
        return listConditions?.let { list ->
            list.map {
                Condition(
                    anticipationAllowed = it.anticipationAllowed,
                    flexibleTerm = it.flexibleTerm,
                    flexibleTermPayment = mapFlexibleTermPayment(it.flexibleTermPayment),
                    flexibleTermPaymentFactor = it.flexibleTermPaymentFactor,
                    flexibleTermPaymentMDR = it.flexibleTermPaymentMDR,
                    maximumInstallments = it.maximumInstallments,
                    mdr = it.mdr,
                    minimumInstallments = it.minimumInstallments,
                    minimumMDR = it.minimumMDR,
                    minimumMDRAmount = it.minimumMDRAmmount,
                    settlementTerm = it.settlementTerm,
                    mdrContracted = it.mdrContracted,
                    rateContractedRR = it.rateContractedRR,
                    contractedMdrCommissionRate = it.contractedMdrCommissionRate,
                )
            }
        }
    }

    private fun mapFlexibleTermPayment(flexibleTermPayment: FlexibleTermPaymentResponse?): FlexibleTermPayment? {
        return flexibleTermPayment?.let {
            FlexibleTermPayment(
                contractedPeriod = it.contractedPeriod,
                factor = it.factor,
                frequency = it.frequency,
                mdr = it.mdr
            )
        }
    }

    private fun concatNumberAndDigit(number: String?, digit: String?): String {
        return number?.let { itNumber ->
            itNumber + (digit?.let { itDigit ->
                if (itDigit.isNotEmpty()) SIMPLE_LINE + itDigit
                else EMPTY
            } ?: EMPTY)
        } ?: EMPTY
    }

}