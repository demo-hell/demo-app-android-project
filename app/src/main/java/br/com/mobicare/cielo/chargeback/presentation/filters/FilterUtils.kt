package br.com.mobicare.cielo.chargeback.presentation.filters

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.data.model.request.OnResultFilterListener
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO

object FilterUtils {

    fun applyFilters(
        listOfSelectedBrands: ArrayList<Int>,
        listOfSelectedProcess: ArrayList<Int>,
        initialDate: String?,
        finalDate: String?,
        nsu: String?,
        tid: String?,
        case: String?,
        chargebackStatus: String,
        applyFilterListener: OnResultFilterListener, filterPendingWithoutDate: Boolean, disputeStatus: ArrayList<Int>
    ) {

        val numberOfAppliedFilters = countNumberOfAppliedFilters(
            listOfSelectedBrands, listOfSelectedProcess, disputeStatus, nsu, tid, case, filterPendingWithoutDate
        )

        val chargebackListParams = ChargebackListParams(
            status = chargebackStatus,
            initDate = initialDate,
            finalDate = finalDate,
            cardBrandCode = listOfSelectedBrands,
            processCode = listOfSelectedProcess,
            disputeStatus = disputeStatus,
            nsu = if (nsu?.isNotBlank() == true) nsu else null,
            tid = if (tid?.isNotBlank() == true) tid else null,
            idCase = if (case?.isEmpty()?.not() == true) case.toInt() else null
        )

        applyFilterListener.onResultFilterListener(chargebackListParams, numberOfAppliedFilters)
    }

    private fun countNumberOfAppliedFilters(
        listOfSelectedBrands: ArrayList<Int>,
        listOfSelectedProcess: ArrayList<Int>,
        disputeStatus: ArrayList<Int>,
        nsu: String?,
        tid: String?,
        case: String?, filterPendingWithoutDate: Boolean
    ): Int {

        var counter = ZERO
        if (filterPendingWithoutDate.not()) counter = ONE

        if (nsu.isNullOrEmpty().not())
            counter++

        if (tid.isNullOrEmpty().not())
            counter++

        if (case.isNullOrEmpty().not())
            counter++

        if (listOfSelectedBrands.isNotEmpty())
            counter++

        if (listOfSelectedProcess.isNotEmpty())
            counter++

        if(disputeStatus.isNotEmpty())
            counter++

        return counter
    }

}