package br.com.mobicare.cielo.meuCadastro.domains.entities

/**
 * Created by silvia.miranda on 25/04/2017.
 */

class MeuCadastroSolucoesContratadas {

    var name: String? = null
    var quantity: Int = 0
    var price: String? = null
    var description: String? = null

    fun quantidadeItem(): String {
        if (quantity <= 0) {
            return ""
        } else if (quantity == 1) {
            return "1 item"
        } else {
            return quantity.toString() + " itens"
        }
    }

    //    public String getPriceFormatted(){
    //        return Utils.formatValue(price);
    //    }
}
