package br.com.mobicare.cielo.mySales.presentation.utils

import br.com.mobicare.cielo.R
import org.androidannotations.annotations.res.ColorRes


const val ITEM_CANCEL_TUTORIAL_ARGS = "ITEM_CANCEL_TUTORIAL_ARGS"
const val IS_SALE_TODAY_ARGS = "IS_SALE_TODAY_ARGS"
const val MINHAS_VENDAS_FILTER = "MinhasVendasFilterBottomSheetFragment"

const val brand = "bandeira"
const val PIX = "PIX"
const val HIERARCHYTYPE_NODE = "NODE"

object ScreenShotsConstants {
    const val FILE_PROVIDER_URI_PACKAGE = "br.com.mobicare.cielo.fileprovider"
    const val SCREENSHOT_DIR = "images"
    const val SCREENSHOT_FILE = "images/image.png"
}

object SaleDetailField {

    const val SALE_DATE = "Data de venda"
    const val AUTHORIZATION_DATE = "Data de autorização"
    const val TID = "TID"
    const val FLAG = "Bandeira"
    const val SALE_VALUE = "Valor da venda"
    const val NET_VALUE = "Valor líquido"
    const val RATE = "Taxa"
    const val PAYMENT_METHODS = "Forma de pagamento"
    const val PAYMENT_FORECAST = "Previsão de pagamento"
    const val SALES_CHANNEL = "Canal da venda"
    const val NOT_FOUND = "NOT_FOUND"
    const val WITHOUT_BALANCE = "WITHOUT_BALANCE"
    const val INELEGIBLE_SALE = "INELEGIBLE_SALE"
    const val ID = "ID"
    const val NSU_DOC = "NSU/DOC"
    const val CAPTURE_TYPE = "Tipo de captura"

}


enum class SalesStatementStatus(val value: Int, @ColorRes val textColor: Int, val isShow: Boolean, val isEnabled: Boolean) {
    APROVADA(1, R.color.success_400,true,true),
    NEGADA(2, R.color.red,false,false),
    DESFEITA(4, R.color.gray_light,false,false),
    ERRO(5, R.color.gray_light,false,false),
    CANCELADA(8,R.color.gray_light,false, false),
    ATUALIZAR(9, R.color.purple,true,false);

    companion object {
        fun getColor(value: Int): Int {
            return values().firstOrNull { it.value == value }?.textColor ?: R.color.red
        }

        fun isShow(value: Int) = values().firstOrNull { it.value == value }?.isShow ?: false

        fun isEnabled(value: Int) = values().firstOrNull { it.value == value }?.isEnabled ?: false

        fun checkSaleStatus(saleStatusStr: String?): Int {
            var saleStatusCode =  values().first().value
            for(v in values()){
                if(saleStatusStr?.let { v.name.lowercase().contains(it) } == true){
                    saleStatusCode = v.value
                }
            }
            return saleStatusCode
        }

    }
}

