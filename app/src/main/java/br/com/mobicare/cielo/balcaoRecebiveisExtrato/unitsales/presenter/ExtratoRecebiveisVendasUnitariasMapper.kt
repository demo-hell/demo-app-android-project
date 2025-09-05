package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasItemUseCase
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasItems
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasUseCase
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol

object ExtratoRecebiveisVendasUnitariasMapper {

    fun mapper(context: Context, response: ExtratoRecebiveisVendasUnitariasResponse): ExtratoRecebiveisVendasUnitariasUseCase {
        val items: MutableList<ExtratoRecebiveisVendasUnitariasItems> = ArrayList()
        items.addAll(response.items)

        items.forEach {
            val contentList: MutableList<ExtratoRecebiveisVendasItemUseCase> = ArrayList()

            val negotiationDate = DataCustomNew()
            negotiationDate.setDateFromAPI(it.negotiationDate)
            val originalReceivableDate = DataCustomNew()
            originalReceivableDate.setDateFromAPI(it.originalReceivableDate)

            val negotiationDateLabel = ExtratoRecebiveisVendasItemUseCase("Data da negociação", negotiationDate.formatBRDate())
            contentList.add(negotiationDateLabel)
            val paymentDate = ExtratoRecebiveisVendasItemUseCase("Data de pagamento original", originalReceivableDate.formatBRDate())
            contentList.add(paymentDate)
            if (it.identificationNumber.length > 11) {
                contentList.add(ExtratoRecebiveisVendasItemUseCase("CPF/CNPJ", it.identificationNumber
                        .addMaskCPForCNPJ(context.getString(R.string.mask_cnpj_step4))))
            } else {
                contentList.add(ExtratoRecebiveisVendasItemUseCase("CPF/CNPJ", it.identificationNumber
                        .addMaskCPForCNPJ(context.getString(R.string.mask_cpf_step4))))
            }
            val acquirer = ExtratoRecebiveisVendasItemUseCase("Instituição financeira negociada", it.acquirer)
            contentList.add(acquirer)
            val cardBrand = ExtratoRecebiveisVendasItemUseCase("Bandeira", it.cardBrand)
            contentList.add(cardBrand)
            val paymentMethod = ExtratoRecebiveisVendasItemUseCase("Arranjo de pagamento", it.paymentMethod)
            contentList.add(paymentMethod)
            val grossAmount = ExtratoRecebiveisVendasItemUseCase("Valor bruto", it.grossAmount.toPtBrRealString())
            contentList.add(grossAmount)
            val netAmount = ExtratoRecebiveisVendasItemUseCase("Valor líquido", it.netAmount.toPtBrRealString())
            contentList.add(netAmount)
            val effectiveFee = ExtratoRecebiveisVendasItemUseCase("Taxa efetiva", "${it.effectiveFee.toPtBrRealStringWithoutSymbol()}%")
            contentList.add(effectiveFee)

            it.contentList = ArrayList()
            it. contentList.addAll(contentList)
        }
        return ExtratoRecebiveisVendasUnitariasUseCase(response.summary, items)
    }
}