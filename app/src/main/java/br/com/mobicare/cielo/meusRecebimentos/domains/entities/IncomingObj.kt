package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.dateWithoutTime
import java.util.*


/**
 * Created by silvia.miranda on 20/06/2017.
 */

class IncomingObj {
    var date: String? = null
    var cieloDate: String? = null
    var cieloEndDate: String? = null
    var dayOfMonth: String? = null
    var dayOfWeek: String? = null
        get() = field?.uppercase()
    var dayDescription: String? = null
    var totalDeposited: Double = 0.0
    var totalPending: Double = 0.00
    var pending: ReceiptDetailObj? = null
    var mainDay: Boolean = false
    var receiptDetails: ArrayList<ReceiptDetailObj>? = null
    var bankDatas: ArrayList<BankDataObj>? = null


    fun isFeature(): Boolean = if (!date.isNullOrEmpty()) {
        val chooseDate = DataCustom(date!!).toDate()
        val dayDate = chooseDate.compareTo(Calendar.getInstance().dateWithoutTime(Date()))
        dayDate >= 0
    } else {
          true
    }

}


