package br.com.mobicare.cielo.home.presentation.main

import br.com.mobicare.cielo.login.domain.UserStatusPrepago

/**
 * Created by Enzo Teles on 04/04/19
 * email: enzo.carvalho.teles@gmail.com
 * Software Developer Sr.
 */

interface HomeStatusPrepago {
    fun loadCardPrepago(data: UserStatusPrepago?)
    fun errorLoadStatus()
    fun loadBannerMigration()
}