package br.com.mobicare.cielo.meusRecebimentos.presentation.ui

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.commons.utils.DataCustom

/**
 * Created by silvia.miranda on 20/06/2017.
 */
interface ComponentFilterContract {

    interface View : IAttached {

        fun tvDailyUpdate(date: String)
        fun tvDateInitUpdate(date: String)
        fun tvDateEndUpdate(date: String)
        fun daily() : DataCustom

    }
}