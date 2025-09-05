package br.com.mobicare.cielo.biometricToken.presentation.home

import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieContract
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.pix.constants.EMPTY

class BiometricTokenHomePresenter(
    private val view: BiometricTokenHomeContract.View
) : BiometricTokenHomeContract.Presenter {

    override fun getUserName() {
        val name = UserPreferences.getInstance().userInformation?.let {
            it.username.split(ONE_SPACE)[ZERO]
        } ?: EMPTY

        view.setupView(name)
    }

}