package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.grafico

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj

/**
 * Created by benhur.souza on 26/06/2017.
 */

interface OnChangeDataListener {
    fun onItemChange(position: Int = 0, incomingObj: IncomingObj) {}
    fun onGraphSuccess() {}
    fun showGraphError(error: ErrorMessage) {}
}
