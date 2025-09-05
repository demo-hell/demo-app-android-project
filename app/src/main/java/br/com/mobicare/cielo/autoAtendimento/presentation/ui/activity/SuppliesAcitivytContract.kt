package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity

import br.com.mobicare.cielo.coil.domains.CoilOptionObj

interface SuppliesAcitivytContract{

    interface View {
        fun listStickers(coilOptions: ArrayList<CoilOptionObj>)
    }

}