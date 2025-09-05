package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.PIX_CARDBRAND_CODE
import br.com.mobicare.cielo.commons.constants.Text.PIX_PAGO_CONTA_DOMICILIO_STATUS_CODE_18
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusrecebimentosnew.enums.MeusRecebimentosCodigosEnum
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item

const val VALORES_PENDENTES = "ValoresPendentes"
const val RECEBIVEIS_VENDA = "RecebiveisDeVenda"
const val CREDITO_ANTECIPACAO = "CreditoAntecipacao"
const val CANCELAMENTOS = "Cancelamentos"
const val CONTESTACOES = "Contestacoes"
const val COBRANCA_ALUGUEL = "CobrancaDeAluguel"
const val OUTRAS_COBRANCAS = "OutrasCobrancas"
const val VALORES_JA_ANTECIPADOS = "ValoresJaAntecipados"
const val OUTROS_VALORES_RECEBIDOS = "OutrosValoresRecebidos"
const val VALORES_PENDENTES_ACUMULADOS = "ValoresPendentesAcumulados"
const val LIBERACAO_SALDO_RETIDO = "LiberacaoSaldoRetido"

class Codes {
    companion object {
        const val VENDA_CREDITO = 1
        const val DEBITO = 2
        const val PARCELADO = 3
        const val DEBITO_AJUSTE = 4
        const val CREDITO_AJUSTE = 5
        const val CANCELAMENTO_VENDA = 6
        const val REVERSAO_CANCELAMENTO = 7
        const val CHARGEBACK = 8
        const val REVERSAO_CHARGEBACK = 9
        const val ALUGUEL_POS = 10
        const val DEBITO_SESSAO = 11
        const val CREDITO_SESSAO = 12
        const val ESTORNO_DEBITO_SESSAO = 17
        const val ESTORNO_CREDITO_SESSAO = 18
        const val CODE_98 = 98
        const val CODE_99 = 99
        const val VOUCHER = 42
    }
}

class MeusRecebimentosHelper {

    companion object {

        fun getScreenName(id: Int) =
                when (id) {
                    98 -> CREDITO_ANTECIPACAO
                    Codes.DEBITO_AJUSTE -> CANCELAMENTOS
                    Codes.CREDITO_AJUSTE -> CONTESTACOES
                    Codes.CANCELAMENTO_VENDA -> COBRANCA_ALUGUEL
                    Codes.REVERSAO_CANCELAMENTO -> OUTRAS_COBRANCAS
                    88 -> VALORES_JA_ANTECIPADOS
                    Codes.CHARGEBACK -> OUTROS_VALORES_RECEBIDOS
                    656 -> VALORES_PENDENTES
                    658 -> VALORES_PENDENTES_ACUMULADOS
                    Codes.VENDA_CREDITO -> RECEBIVEIS_VENDA
                    96 -> LIBERACAO_SALDO_RETIDO
                    else -> VALORES_PENDENTES
                }

        fun getScreenNameWithSpaces(id: Int) =
                when (id) {
                    98 -> CREDITO_ANTECIPACAO
                    Codes.DEBITO_AJUSTE -> CANCELAMENTOS
                    Codes.CREDITO_AJUSTE -> CONTESTACOES
                    Codes.CANCELAMENTO_VENDA -> "CobrancaDeAluguel"
                    Codes.REVERSAO_CANCELAMENTO -> "OutrasCobrancas"
                    88 -> "Valores Já Antecipados"
                    Codes.CHARGEBACK -> "Outros Valores Recebidos"
                    656 -> "Valores Pendentes"
                    658 -> "Valores Pendentes Acumulados"
                    Codes.VENDA_CREDITO -> "RecebiveisDeVenda"
                    96 -> "Liberacao Saldo Retido"
                    else -> VALORES_PENDENTES
                }

        fun formatedScreenName(): String {
            return ActivityDetector
                    .getActivityDetector()
                    .screenCurrentPath()
                    .replace("/MeusRecebimentosHome", "")
                    .replace("ResumoOperacoes", "MeusRecebimentos")
                    .replace("/MeusRecebimentosDetalhe", "")

        }

        fun generateHashMap(code: Int, item: Item): List<Pair<String, String?>> =
                when (code) {
                    Codes.VENDA_CREDITO, Codes.DEBITO, Codes.PARCELADO, Codes.CANCELAMENTO_VENDA,
                    Codes.REVERSAO_CANCELAMENTO, Codes.CHARGEBACK, Codes.REVERSAO_CHARGEBACK -> generateHashMapByCode1(item)
                    Codes.DEBITO_AJUSTE, Codes.CREDITO_AJUSTE -> generateHashMapByCode4And5(item)
                    Codes.ALUGUEL_POS -> generateHashMapByCode10(item)
                    Codes.DEBITO_SESSAO, Codes.CREDITO_SESSAO, Codes.ESTORNO_DEBITO_SESSAO, Codes.ESTORNO_CREDITO_SESSAO -> generateHashMapByCode11121718(item)
                    88 -> generateHashMapByCode88(item)
                    96 -> generateHashMapByCode96(item)
                    98 -> generateHashMapByCode98(item)
                    99 -> generateHashMapByCode99(item)
                    else -> generateHashMapByCodeDefault(item)
                }

        private fun generateHashMapByCode1(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", item.cardBrand))
                    add(Pair("Previsão de pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCode4And5(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand} ${item.paymentType ?: ""}"))
                    add(Pair(item.bank?.name ?: "", ""))
                    add(Pair("Agência", item.bank?.agency))
                    add(Pair("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}"))
                    add(Pair("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))

                    if (item.saleDate?.isNotEmpty() == true)
                        add(Pair("Data da Venda", DateTimeHelper.convertToDate(item.saleDate, "yyyy-MM-dd", "dd/MM/yyyy")))

                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))

                    if (item.status?.isNotEmpty() == true)
                        add(Pair("Status", item.status))

                    if (item.description?.isNotEmpty() == true)
                        add(Pair("Descrição", item.description))
                }

        private fun generateHashMapByCode10(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand} ${item.paymentType ?: ""}"))
                    add(Pair(item.bank?.name ?: "", ""))
                    add(Pair("Agência", item.bank?.agency))
                    add(Pair("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}"))
                    add(Pair("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Período considerado",
                            "${DateTimeHelper.convertToDate(item.initialDate, "yyyy-MM-dd", "MM/yyyy")}" +
                                    " até ${DateTimeHelper.convertToDate(item.finalDate, "yyyy-MM-dd", "MM/yyyy")}"))
                    add(Pair("Status", item.status))

                    if (item.terminal?.isNotEmpty() == true)
                        add(Pair("Número da máquina", item.terminal))

                    if (item.description?.isNotEmpty() == true)
                        add(Pair("Motivo", item.description))
                }

        private fun generateHashMapByCode11121718(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand}"))
                    add(Pair("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCode88(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "Número da operação:"))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Data de pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCode96(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Data de pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Tipo de Lançamento", item.transactionType))
                    add(Pair("Bandeira", item.cardBrand))
                    add(Pair("Forma de Pagamento", item.paymentType))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCode98(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand}"))
                    add(Pair("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCode99(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand}"))
                    add(Pair("Previsão de pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }

        private fun generateHashMapByCodeDefault(item: Item): List<Pair<String, String?>> =
                ArrayList<Pair<String, String?>>().apply {
                    add(Pair("title", "${item.cardBrand}"))
                    add(Pair("Previsão de pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy")))
                    add(Pair("Estabelecimento", item.merchantId))
                    add(Pair("Quantidade de lançamentos", item.quantity?.toString()))
                    add(Pair("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString()))
                    add(Pair("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString()))
                }
    }
}

class MeusRecebimentosDetalheHelper {
    companion object {

        fun  generateHashMap(code: Int, item: Receivable, context: Context): List<Triple<String, String?,Int?>> =
                when (code) {
                    Codes.VENDA_CREDITO, Codes.DEBITO, Codes.PARCELADO, Codes.CANCELAMENTO_VENDA,
                    Codes.REVERSAO_CANCELAMENTO, Codes.CHARGEBACK, Codes.REVERSAO_CHARGEBACK, Codes.VOUCHER -> generateHashMapByCodeGroup1(item, code,context)
                    Codes.DEBITO_SESSAO, Codes.CREDITO_SESSAO, Codes.ESTORNO_DEBITO_SESSAO, Codes.ESTORNO_CREDITO_SESSAO -> generateHashMapByCodeGroup2(item)
                    Codes.CODE_98 -> generateHashMapByCode98(item)
                    Codes.CODE_99 -> generateHashMapByCode99(item)
                    else -> {
                        if (code >= MeusRecebimentosCodigosEnum.VALORES_PENDENTES.code) {
                            generateHashMapByPendentCode(item)
                        } else {
                            generateHashMapByCodeDefault(item)
                        }
                    }
                }

        private fun generateHashMapByCodeGroup1(item: Receivable, code: Int, context: Context): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    when (code) {
                        Codes.PARCELADO -> add(Triple("title", "${item.cardBrand} ${item.paymentDescription ?: ""}",null))
                        else -> add(Triple("title", "${item.cardBrand} ${item.paymentType ?: ""}",null))
                    }

                    add(Triple(item.bank?.name ?: "", "",null))
                    add(Triple("Agência", item.bank?.agency ?: "",null))
                    add(Triple("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}",null))

                    if(item.cardBrandCode == PIX_CARDBRAND_CODE) {
                        item.transactionPixId?.let { pixId ->
                            add(Triple("ID", pixId,null))
                        }
                    } else {
                        item.id?.let { id->
                            add(Triple("ID", id,null))
                        }
                    }

                    add(Triple("Valor da taxa/tarifa", item.mdrFeeAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Código de autorização", item.authorizationCode,null))
                    add(Triple("Código da venda", item.saleCode,null))
                    add(Triple("Número do cartão", "**** **** ${item.truncatedCardNumber ?: ""}",null))
                    if(item.nsu.isNullOrEmpty().not()){ add(Triple("NSU/DOC", item.nsu,null)) }

                    if (item.terminal?.isNotEmpty() == true)
                        add(Triple("Número da máquina", item.terminal,null))

                    if (item.operationNumber?.isNotEmpty() == true && code != Codes.VOUCHER)
                        add(Triple("Número da operação", item.operationNumber,null))

                    add(Triple("Resumo de operação", item.roNumber,null))
                    add(Triple("Taxa", "${item.mdrFee?.toPtBrRealStringWithoutSymbol1()}%",null))
                    add(Triple("Canal da Venda", item.channel,null))
                    add(Triple("Tipo de Captura", item.entryMode,null))
                    if (item.transactionId?.isNullOrEmpty()?.not() == true){
                        add(Triple("TID - Vendas e-commerce", item.transactionId,null))
                    }
                    add(Triple("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Data da Venda", DateTimeHelper.convertToDate(item.saleDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Estabelecimento", item.merchantId,null))
                    add(Triple("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString(),null))

                    var statusColorCode: Int? = null
                    if(item.cardBrandCode == PIX_CARDBRAND_CODE){
                        when(item.codeFarol){
                            ONE -> statusColorCode = ContextCompat.getColor(context, R.color.green)
                            TWO -> statusColorCode = ContextCompat.getColor(context, R.color.yellow)
                            THREE -> statusColorCode = ContextCompat.getColor(context, R.color.red)
                        }

                        if(item.statusCode != null && item.statusCode == PIX_PAGO_CONTA_DOMICILIO_STATUS_CODE_18){
                            val date = DateTimeHelper.convertToDate(item.dateTransferAccountPix,"yyyy-MM-dd", "dd/MM/yyyy")
                            add(Triple("Data de pagamento na conta Cielo",date,null))
                        }
                    }

                    if(item.status.isNullOrEmpty().not()){ add(Triple("Status", item.status,statusColorCode)) }

                    if (item.description?.isNotEmpty() == true)
                        add(Triple("Descrição", item.description,null))
                }

        private fun generateHashMapByCodeGroup2(item: Receivable): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    add(Triple("title", "${item.cardBrand}",null))

                    if (item.operationNumber?.isNotEmpty() == true)
                        add(Triple("Número da negociação", item.operationNumber,null))

                    add(Triple("Data da negociação", DateTimeHelper.convertToDate(item.operationDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Data da Venda", DateTimeHelper.convertToDate(item.saleDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Estabelecimento", item.merchantId,null))
                    add(Triple("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Status", item.status,null))

                    if (item.description?.isNotEmpty() == true)
                        add(Triple("Descrição", item.description,null))
                }

        private fun generateHashMapByPendentCode(item: Receivable): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    add(Triple("title", "${item.cardBrand} ${item.paymentType ?: ""}",null))
                    add(Triple(item.bank?.name ?: "", "",null))
                    add(Triple("Agência", item.bank?.agency ?: "",null))
                    add(Triple("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}",null))
                    item.totalDebtAmount?.let {
                        add(Triple("Valor total", it.toPtBrWithNegativeRealString(),null))
                    }
                    item.chargedAmount?.let {
                        add(Triple("Valor cobrado", it.toPtBrWithNegativeRealString(),null))
                    }
                    item.pendingAmount?.let {
                        add(Triple("Valor pendente", it.toPtBrWithNegativeRealString(),null))
                    }
                }

        private fun generateHashMapByCode98(item: Receivable): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    add(Triple("title", "${item.cardBrand} ${item.paymentType ?: ""}",null))
                    add(Triple(item.bank?.name ?: "", "",null))
                    add(Triple("Agência", item.bank?.agency ?: "",null))
                    add(Triple("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}",null))
                    add(
                        Triple("Valor da taxa/tarifa", item.mdrFeeAmount?.toPtBrWithNegativeRealString()
                            ?: "-",null)
                    )

                    if (item.roNumber?.isNotEmpty() == true)
                        add(Triple("Resumo da operação", item.roNumber,null))

                    if (item.operationNumber?.isNotEmpty() == true)
                        add(Triple("Número da operação", item.operationNumber,null))

                    add(Triple("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Estabelecimento", item.merchantId,null))
                    add(Triple("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Status", item.status,null))

                    if (item.description?.isNotEmpty() == true)
                        add(Triple("Descrição", item.description,null))

                }

        private fun generateHashMapByCode99(item: Receivable): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    add(Triple("title", "${item.cardBrand} ${item.paymentType ?: ""}",null))
                    add(Triple(item.bank?.name ?: "", "",null))
                    add(Triple("Agência", item.bank?.agency ?: "",null))
                    add(Triple("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}",null))

                    if (item.roNumber?.isNotEmpty() == true)
                        add(Triple("Resumo da operação", item.roNumber,null))

                    if (item.operationNumber?.isNotEmpty() == true)
                        add(Triple("Número da operação", item.operationNumber,null))

                    add(Triple("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Estabelecimento", item.merchantId,null))
                    add(Triple("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Status", item.status,null))

                    if (item.description?.isNotEmpty() == true)
                        add(Triple("Descrição", item.description,null))

                }

        private fun generateHashMapByCodeDefault(item: Receivable): List<Triple<String, String?,Int?>> =
                ArrayList<Triple<String, String?,Int?>>().apply {
                    add(Triple("title", "${item.cardBrand} ${item.paymentType ?: ""}",null))
                    add(Triple(item.bank?.name ?: "", "",null))
                    add(Triple("Agência", item.bank?.agency ?: "",null))
                    add(Triple("Conta", "${item.bank?.account}${item.bank?.accountDigit?.let { "-$it" } ?: ""}",null))
                    add(Triple("Data de Pagamento", DateTimeHelper.convertToDate(item.paymentDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Data da Venda", DateTimeHelper.convertToDate(item.saleDate, "yyyy-MM-dd", "dd/MM/yyyy"),null))
                    add(Triple("Estabelecimento", item.merchantId,null))
                    add(Triple("Valor bruto", item.grossAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Valor líquido", item.netAmount?.toPtBrWithNegativeRealString(),null))
                    add(Triple("Status", item.status,null))

                    if (item.description?.isNotEmpty() == true)
                        add(Triple("Descrição", item.description,null))
                }
    }
}