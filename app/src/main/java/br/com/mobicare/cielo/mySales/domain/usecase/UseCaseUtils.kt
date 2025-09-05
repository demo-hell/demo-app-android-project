package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.mySales.data.model.Summary

object UseCaseUtils {

    fun isSalesEmpty(summary: Summary? ): Boolean{
        return summary?.totalQuantity == ZERO || summary?.totalAmount == ZERO_DOUBLE
    }
}