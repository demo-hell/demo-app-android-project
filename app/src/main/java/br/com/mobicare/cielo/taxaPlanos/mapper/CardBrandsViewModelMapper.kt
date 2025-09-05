package br.com.mobicare.cielo.taxaPlanos.mapper

import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands
import br.com.mobicare.cielo.meuCadastro.domains.entities.Products
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.TaxasModelView

class CardBrandsViewModelMapper {

    /**
     * Converte um objeto que representa bandeiras para um objeto modelo de uma view específica
     *
     * @param cardBrands objeto de entrada que representa uma bandeira vindo de um serviço
     *
     */
    fun convert(cardBrands: CardBrands): BandeiraModelView {

        val brandsByPaymentType =
            cardBrands.products.distinctBy { it.name }
        val brandsByPaymentInstallmentsId = cardBrands.products.groupBy { it.paymentInstallments }

        val tempArrayList = ArrayList<TaxasModelView>()
        val mutableArrayList = convertToPairList(brandsByPaymentInstallmentsId)

        tempArrayList.addAll(brandsByPaymentType.map {
            TaxasModelView(it.name, mutableArrayList)
        }.toMutableList())

        return BandeiraModelView(
            cardBrands.name,
            cardBrands.imageURL,
            taxas= tempArrayList
        )
    }

    private fun convertToPairList(brandsByPaymentInstallmentsId: Map<String, List<Products>>):
            ArrayList<Pair<String, String>> {
        val mutableArrayList = ArrayList<Pair<String, String>>()
        val pairList = brandsByPaymentInstallmentsId.flatMap { installmentEntry ->
            installmentEntry.value.distinctBy { prodList ->
                prodList.installmentsText
            }
        }.filter { productToFilter ->
            productToFilter.installmentsText != "none"
        }.map { resultProductToPair ->
            Pair(resultProductToPair.installmentsText, resultProductToPair.fee)
        }

        mutableArrayList.addAll(pairList)
        return mutableArrayList
    }

}