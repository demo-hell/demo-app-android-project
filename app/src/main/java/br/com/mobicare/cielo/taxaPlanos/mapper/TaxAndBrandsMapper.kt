package br.com.mobicare.cielo.taxaPlanos.mapper

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.extensions.getPercent
import br.com.mobicare.cielo.extensions.toStringAsDaysTo
import br.com.mobicare.cielo.extensions.toStringAsDaysWithPlusSign
import br.com.mobicare.cielo.extensions.toStringReceivableDayWeek
import br.com.mobicare.cielo.meuCadastroNovo.domain.Condition
import br.com.mobicare.cielo.meuCadastroNovo.domain.Product
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.TaxasModelView
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

object TaxAndBrandsMapper {

    private const val VISA = "Visa"
    private const val VISA_CAPS = "VISA"
    private const val CREDIT = "Crédito"
    private const val DEBIT = "Débito"
    private const val INSTALLMENT = "Parcelado"
    private const val CREDIT_NAME = "Crédito À vista"
    private const val DEBIT_NAME = "Débito À vista"
    private const val INSTANT_PAYMENT = "À vista"
    private const val INSTALLMENT_NAME = "Parcelado Loja"
    private const val PARENTHESES_OPEN = " ("
    private const val PARENTHESES_CLOSE = ")"
    private const val SUM_WITH_SPACE = " + "
    const val RATE_MDR = "Taxa (MDR)"
    const val RATE_MDR_RR = "Taxa (MDR + RA)"
    const val RATES_MDR = "Taxas (MDR)"
    const val RATES_MDR_RR = "Taxas (MDR + RA)"
    const val INSTALLMENTS = "Parcelas"
    const val PIX_RATE_TAX = "Tarifa"
    const val PIX = "PIX"
    const val WEEK = "DAY_OF_WEEK"
    const val MONTH = "DAY_OF_MONTH"
    const val DAILY = "DAILY"

    fun mapper(
        taxAndBrandsResponse: List<Solution>
    ): ComparationViewModelRR {
        val listWithoutRecebaRapido = ArrayList<ComponentLayoutFeeAndPlansItem>()
        val listWithRecebaRapido = ArrayList<ComponentLayoutFeeAndPlansItem>()
        val listWithoutRequestedScreen = ArrayList<ComponentLayoutFeeAndPlansItem>()

        taxAndBrandsResponse.forEach { itSolution ->
            itSolution.banks.forEach { itBank ->
                itBank.brands?.forEach { brand ->
                    when (brand.name) {
                        VISA, VISA_CAPS -> {
                            brand.products.forEachIndexed { index, itProduct ->
                                var name = EMPTY
                                when (itProduct.name) {
                                    CREDIT_NAME -> name = CREDIT
                                    DEBIT_NAME -> name = DEBIT
                                    INSTALLMENT_NAME -> name = INSTALLMENT
                                }
                                if (name == CREDIT || name == DEBIT || name == INSTALLMENT) {

                                    itProduct.conditions[ZERO].let { condition ->

                                        condition.flexibleTermPayment?.let {
                                            var withoutRecebaRapido: ComponentLayoutFeeAndPlansItem

                                            var frequency: String

                                            condition.settlementTerm.let {
                                                frequency = "${condition.settlementTerm?.toStringAsDaysTo()} ${condition.flexibleTermPaymentMDR?.getPercent()}"
                                                withoutRecebaRapido = ComponentLayoutFeeAndPlansItem(name,
                                                        condition.settlementTerm?.toStringAsDaysTo() + " ${condition.mdr?.getPercent()}")
                                            }
                                            if (it.frequency.isNullOrEmpty().not()) {
                                                frequency = "${getReceive(condition).second} ${condition.flexibleTermPaymentMDR?.getPercent()}"
                                            }

                                            listWithRecebaRapido.add(ComponentLayoutFeeAndPlansItem(name, frequency))
                                            listWithoutRecebaRapido.add(withoutRecebaRapido)

                                        } ?: run {
                                            listWithoutRecebaRapido.add(ComponentLayoutFeeAndPlansItem(name, Text.DOUBLE_LINE))
                                            listWithRecebaRapido.add(ComponentLayoutFeeAndPlansItem(name, Text.DOUBLE_LINE))

                                            condition.settlementTerm.let {
                                                val withoutRecebaRapidoToRequestedScreen = ComponentLayoutFeeAndPlansItem(name,
                                                        condition.settlementTerm?.toStringAsDaysWithPlusSign())
                                                listWithoutRequestedScreen.add(withoutRecebaRapidoToRequestedScreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(listWithoutRecebaRapido, compareBy { it.labelTitle?.length })
        Collections.sort(listWithRecebaRapido, compareBy { it.labelTitle?.length })
        Collections.sort(listWithoutRequestedScreen, compareBy { it.labelTitle?.length })
        return ComparationViewModelRR(
                listWithoutRecebaRapido,
                listWithRecebaRapido,
                listWithoutRequestedScreen
        )
    }

    fun convert(response: List<Solution>): ArrayList<BandeiraModelView> {
        val listBrandsModel = ArrayList<BandeiraModelView>()

        response.forEach { solution ->
            solution.banks.forEach { bank ->
                bank.brands?.forEach { itBrand ->
                    val taxes = ArrayList<TaxasModelView>()
                    var hasRR = false

                    itBrand.products.forEach { itProduct ->
                        val parceledTaxes = mutableListOf<Pair<String, String>>()
                        var lastProductName = Text.EmptyText

                        itProduct.conditions.forEach { itCondition ->
                            if (itCondition.minimumInstallments != null && itCondition.minimumInstallments > ZERO) {
                                val parceladoLojaValue = groupParceladoLoja(
                                    lastProductName,
                                    itCondition,
                                    itProduct.prazoFlexivel
                                )

                                val installment = parceladoLojaValue[ZERO].second
                                lastProductName =
                                    if (installment.isNotEmpty()) installment else lastProductName
                                parceledTaxes.addAll(parceladoLojaValue)
                            } else {
                                val name = if (itProduct.name.contains(PIX, ignoreCase = true)) INSTANT_PAYMENT else itProduct.name
                                taxes.add(
                                    convertCreditDebitoAVista(
                                        name,
                                        itProduct,
                                        itCondition,
                                        itProduct.prazoFlexivel
                                    )
                                )
                            }

                            if (itProduct.prazoFlexivel &&
                                ((itCondition.rateContractedRR ?: ZERO_DOUBLE) > ZERO_DOUBLE)
                            ) hasRR = true
                        }

                        if (parceledTaxes.isNotEmpty()) {
                            taxes.add(TaxasModelView(itProduct.name, parceledTaxes))
                        }
                    }
                    listBrandsModel.add(BandeiraModelView(itBrand.name, itBrand.imgSource, taxes))
                }
            }
        }

        return listBrandsModel
    }

    private fun convertCreditDebitoAVista(
        name: String,
        product: Product,
        condition: Condition,
        flexibleTerm: Boolean
    ): TaxasModelView {
        val values = ArrayList<Pair<String, String>>()

        values.add(getReceive(condition))

        if (product.name.contains(PIX, ignoreCase = true)) {
            product.pixRateTax?.let {
                if (it > ZERO_DOUBLE) {
                    values.add(Pair(PIX_RATE_TAX, it.toPtBrRealString()))
                }
            }
            val mdr = concatMDRAndRR(condition, flexibleTerm)
            val label = if (flexibleTerm && ((condition.rateContractedRR
                    ?: ZERO_DOUBLE) > ZERO_DOUBLE)
            ) RATE_MDR_RR else RATE_MDR
            values.add(Pair(label, mdr))
        } else {
            values.add(
                Pair(
                    Text.TAX_MINIMUM,
                    (condition.minimumMDRAmmount ?: ZERO_DOUBLE).toPtBrRealString()
                )
            )
            val mdr = concatMDRAndRR(condition, flexibleTerm)
            val label = if (flexibleTerm && ((condition.rateContractedRR
                    ?: ZERO_DOUBLE) > ZERO_DOUBLE)
            ) RATE_MDR_RR else RATE_MDR
            values.add(Pair(label, mdr))
        }

        return TaxasModelView(name, values)
    }

    fun getReceive(condition: Condition): Pair<String, String> {
        return Pair(Text.RECEBIMENTO,
            if (condition.flexibleTerm) {
                var contractedPeriod = Text.SIMPLE_LINE
                condition.flexibleTermPayment?.contractedPeriod?.let { period ->
                    val frequency = condition.flexibleTermPayment.frequency
                    if (frequency.isNullOrEmpty().not()) {
                        val firstFrequency = frequency!![ZERO]
                        contractedPeriod = when (period) {
                            MONTH -> {
                                val dayInterval = when (frequency.size) {
                                    THREE -> {
                                        "$firstFrequency, ${frequency[ONE]} e ${frequency[TWO]}"
                                    }
                                    TWO -> {
                                        "$firstFrequency e ${frequency[ONE]}"
                                    }
                                    else -> {
                                        "$firstFrequency"
                                    }
                                }
                                "$dayInterval ${Text.MONTH}"
                            }
                            WEEK -> {
                                val genderAll = if (firstFrequency in ONE..FIVE) {
                                    Text.ANY
                                } else {
                                    Text.ALL
                                }
                                "$genderAll ${firstFrequency.toStringReceivableDayWeek()}"
                            }
                            DAILY -> {
                                if (firstFrequency > ONE) {
                                    "${Text.IN} $firstFrequency ${Text.DAYS_SHORT}"
                                } else {
                                    "$firstFrequency ${Text.DIA}"
                                }
                            }
                            else -> {
                                Text.SIMPLE_LINE
                            }
                        }
                    }
                }

                contractedPeriod
            } else {
                val dayWord = if (condition.settlementTerm!! > ONE) Text.DAYS_SHORT else Text.DIA
                if (condition.settlementTerm > ONE) {
                    "${Text.IN} ${condition.settlementTerm} $dayWord"
                } else {
                    "${condition.settlementTerm} $dayWord"
                }
            }
        )
    }

    private fun handleParcelado(condition: Condition): String {
        return if (condition.minimumInstallments == condition.maximumInstallments) {
            "${condition.minimumInstallments}${Text.X}"
        } else {
            "${Text.FROM} ${condition.minimumInstallments}${Text.TO} ${condition.maximumInstallments}${Text.X}"
        }
    }

    fun groupParceladoLoja(
        lastProductName: String,
        condition: Condition,
        flexibleTerm: Boolean
    ): ArrayList<Pair<String, String>> {
        val values = ArrayList<Pair<String, String>>()
        val receivableMessage = getReceive(condition)
        if (lastProductName.isEmpty() || lastProductName != receivableMessage.second && lastProductName.contains(
                Text.PERCENT
            ).not()
        ) {
            values.add(receivableMessage)
            values.add(
                Pair(
                    Text.TAX_MINIMUM,
                    (condition.minimumMDRAmmount ?: ZERO_DOUBLE).toPtBrRealString()
                )
            )
            val labelInstallments = if (flexibleTerm && ((condition.rateContractedRR
                    ?: ZERO_DOUBLE) > ZERO_DOUBLE)
            ) RATES_MDR_RR else RATES_MDR
            values.add(Pair(INSTALLMENTS, labelInstallments))
        }
        val mdr = concatMDRAndRR(condition, flexibleTerm)
        values.add(Pair(handleParcelado(condition), mdr))
        return values
    }

    private fun concatMDRAndRR(condition: Condition, flexibleTerm: Boolean): String {
        val rateContractedRR = condition.rateContractedRR ?: ZERO_DOUBLE
        val contractedMdrCommissionRate =
            (condition.contractedMdrCommissionRate ?: ZERO_DOUBLE).toPtBrRealStringWithoutSymbol()
        val mdrContracted = (condition.mdrContracted ?: ZERO_DOUBLE).toPtBrRealStringWithoutSymbol()

        return if (flexibleTerm && (rateContractedRR > ZERO_DOUBLE)) contractedMdrCommissionRate + Text.PERCENT + PARENTHESES_OPEN + mdrContracted + Text.PERCENT + SUM_WITH_SPACE + rateContractedRR.toPtBrRealStringWithoutSymbol() + Text.PERCENT + PARENTHESES_CLOSE
        else contractedMdrCommissionRate + Text.PERCENT
    }

}

@Parcelize
@Keep
class ComparationViewModelRR(
    val listWithoutRR: ArrayList<ComponentLayoutFeeAndPlansItem>,
    val listWithRR: ArrayList<ComponentLayoutFeeAndPlansItem>,
    val listWithoutRequestedScreen: ArrayList<ComponentLayoutFeeAndPlansItem>
) : Parcelable
