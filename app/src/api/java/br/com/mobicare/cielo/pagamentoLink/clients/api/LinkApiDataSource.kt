package br.com.mobicare.cielo.pagamentoLink.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.extensions.ifNullOrBlank
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.*
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSalePeriodicEnum
import io.reactivex.Observable

class LinkApiDataSource(val context: Context) : LinkDataSource {

    private val cieloApiServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    override fun generateLink(token: String, dto: PaymentLinkDTO, quickFilter: QuickFilter?): Observable<CreateLinkBodyResponse> {
        return cieloApiServices.generateLink(token, createBodyRequest(dto, quickFilter))
    }

    private fun createBodyRequest(dto: PaymentLinkDTO, quickFilter: QuickFilter?) =
            when (dto.typeSale) {
                TypeSaleEnum.CHARGE_AMOUNT -> generateRequestForChargeAmount(dto, quickFilter)
                TypeSaleEnum.SEND_PRODUCT -> generateRequestForSendProduct(dto, quickFilter)
                TypeSaleEnum.RECURRENT_SALE -> generateRequestForRecurrentSale(dto, quickFilter)
            }

    private fun generateRequestForChargeAmount(dto: PaymentLinkDTO, quickFilter: QuickFilter?): CreateLinkBodyRequest {
        return CreateLinkBodyRequest(
                type = "DIGITAL",
                name = dto.productName ?: "",
                price = dto.productValue?.toDouble() ?: 0.0,
                shipping = generateShippingForChargeAmount(),
                quantity = quickFilter?.quantity,
                softDescriptor = quickFilter?.softDescriptor,
                expiration = quickFilter?.expiredDate,
                maximumInstallment = quickFilter?.maximumInstallment,
                finalRecurrentExpiration = quickFilter?.finalRecurrentExpiration,
                sku = quickFilter?.sku
        )
    }

    private fun generateRequestForSendProduct(dto: PaymentLinkDTO, quickFilter: QuickFilter?): CreateLinkBodyRequest {
        return CreateLinkBodyRequest(
                type = "ASSET",
                name = dto.productName ?: "",
                price = dto.productValue?.toDouble() ?: 0.0,
                shipping = generateShippingSendProduct(dto),
                weight = dto.weight,
                description = getDescription(dto),
                quantity = quickFilter?.quantity,
                softDescriptor = quickFilter?.softDescriptor,
                expiration = quickFilter?.expiredDate,
                maximumInstallment = quickFilter?.maximumInstallment,
                finalRecurrentExpiration = quickFilter?.finalRecurrentExpiration,
                sku = quickFilter?.sku
        )
    }

    private fun generateRequestForRecurrentSale(dto: PaymentLinkDTO, quickFilter: QuickFilter?): CreateLinkBodyRequest {

        val type = if (quickFilter?.frequency != null) TypeSalePeriodicEnum.valueOf(quickFilter.frequency)
        else TypeSalePeriodicEnum.RECURRENT_MONTHLY

        return CreateLinkBodyRequest(
                type = type.name,
                name = dto.productName ?: "",
                price = dto.productValue?.toDouble() ?: 0.0,
                shipping = generateShippingForChargeAmount(),
                weight = dto.weight,
                description = getDescription(dto),
                softDescriptor = quickFilter?.softDescriptor,
                expiration = quickFilter?.expiredDate,
                finalRecurrentExpiration = quickFilter?.finalRecurrentExpiration,
                sku = quickFilter?.sku
        )
    }

    private fun generateShippingForChargeAmount() = Shipping(type = "WITHOUT_SHIPPING")

    private fun generateShippingSendProduct(dto: PaymentLinkDTO): Shipping =
            when (dto.responsibleDelivery) {
                ResponsibleDeliveryEnum.CORREIOS -> generateShippingForCorreios(dto)
                ResponsibleDeliveryEnum.CUSTOM -> generateShippingForFreight(dto)
                ResponsibleDeliveryEnum.FREE_SHIPPING -> generateShippingFreeShipping(dto)
                else -> generateShippingForLoggi(dto)
            }

    private fun generateShippingForCorreios(dto: PaymentLinkDTO) =
            Shipping(type = "CORREIOS", zipCode = dto.zipCode)

    private fun generateShippingForFreight(dto: PaymentLinkDTO) = Shipping(
        type = "FIXED_AMOUNT",
        name = dto.nameOfFreight.ifNullOrBlank { "Frete Fixo" },
        price = dto.costOfFreight?.toDouble()
    )

    private fun generateShippingFreeShipping(dto: PaymentLinkDTO) =
            Shipping(type = "FREE", name = "Frete Grátis")

    private fun generateShippingForLoggi(dto: PaymentLinkDTO) =
            Shipping(
                    type = "LOGGI",
                    withdrawalDelay = dto.dalayTime,
                    spec = Specification(
                            dimension = Dimension(
                                    length = dto.productDetail?.depth,
                                    height = dto.productDetail?.height,
                                    width = dto.productDetail?.width
                            )
                    ),
                    pickupAddress = Address(
                            type = dto.address?.addressType?.uppercase(),
                            streetAddress = dto.address?.streetAddress,
                            streetAddress2 = if (dto.address?.referencePoint?.isNotEmpty() == true) dto.address?.referencePoint else null,
                            neighborhood = dto.address?.neighborhood,
                            city = dto.address?.city,
                            number = dto.address?.numberAddress,
                            state = dto.address?.state,
                            zipCode = dto.address?.zipcode?.replace("-", ""),
                            contactPhone = dto.phone
                    )
            )

    private fun getDescription(dto: PaymentLinkDTO) =
            when (dto.responsibleDelivery) {
                ResponsibleDeliveryEnum.CUSTOM -> "Frete Fixo"
                else -> null
            }
}