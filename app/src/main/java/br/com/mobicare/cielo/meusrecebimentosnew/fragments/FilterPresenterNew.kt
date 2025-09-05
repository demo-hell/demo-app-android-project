package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import br.com.mobicare.cielo.commons.utils.DataCustomNew

interface FilterPresenterNew {

    fun callCalculationVision(initialDate: DataCustomNew?, finalDate: DataCustomNew?)
    fun callCalculationVisionDaily(date: DataCustomNew?)
}