package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import br.com.mobicare.cielo.commons.utils.DataCustomNew

class FilterPresenterNewImpl(val view: ComponentFilterListener) : FilterPresenterNew {

    override fun callCalculationVision(initialDate: DataCustomNew?, finalDate: DataCustomNew?) {
        if (initialDate != null && finalDate != null) {
            if (finalDate.toDate().before(initialDate.toDate())) {
                view.showFilterErroAlert()
            } else {
                view.onClickDate(initialDate.formatDateToAPI(), finalDate.formatDateToAPI())
            }
        }
    }

    override fun callCalculationVisionDaily(date: DataCustomNew?) {
        view.onClickDate(date!!.formatDateToAPI(), date.formatDateToAPI())
        view.showGraph(date)
    }
}