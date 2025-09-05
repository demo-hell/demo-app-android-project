package br.com.mobicare.cielo.home.presentation.produtos.presenter

import br.com.mobicare.cielo.home.presentation.produtos.ProdutosHomeContract
import br.com.mobicare.cielo.home.presentation.produtos.domain.entities.ProdutoObj

/**
 * Created by david on 04/08/17.
 */

class ProdutosHomePresenter(var view: ProdutosHomeContract.View) : ProdutosHomeContract.Presenter {

    override fun callAPI() {

        //TODO: Criar implementacao

        var produtos: ArrayList<ProdutoObj> = ArrayList()

        var prod: ProdutoObj = ProdutoObj()
        prod.name = "Dotz"
        prod.description = "Seu cliente acumula Dotz na máquina da Cielo."
        produtos.add(prod)

        prod = ProdutoObj()
        prod.name = "Smiles"
        prod.description = "Seu cliente acumula Smiles na máquina da Cielo."
        produtos.add(prod)

        prod = ProdutoObj()
        prod.name = "SKY"
        prod.description = "Recargas e pagamentos de fatura direto da máquina da Cielo."
        produtos.add(prod)

        view.loadData(produtos)
    }
}
