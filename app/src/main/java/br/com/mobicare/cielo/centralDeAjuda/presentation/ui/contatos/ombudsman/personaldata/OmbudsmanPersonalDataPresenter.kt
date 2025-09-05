package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference

class OmbudsmanPersonalDataPresenter(private val view: OmbudsmanPersonalDataContract.View,
                                     private val userPreferences: UserPreferences,
                                     private val menuPreference: MenuPreference) : OmbudsmanPersonalDataContract.Presenter {

    override fun onLoadPersonalData() {
        view.showLoading()
        userPreferences.userInformation?.let { me ->
            val ec = menuPreference.getEC()
            val ombudsman = onCreateObject(
                    userName = me.username,
                    ec = ec,
                    email = me.email,
                    phone = me.phoneNumber)
            showData(ombudsman)
        } ?: run {
            showData()
        }
    }

    override fun onCreateObject(userName: String?,
                                ec: String?,
                                email: String?,
                                phone: String?
    ): OmbudsmanRequest = OmbudsmanRequest(contactPerson = userName,
            merchant = ec,
            email = email,
            phone = phone?.removeNonNumbers()
    )

    private fun showData(ombudsman: OmbudsmanRequest? = null) {
        view.hideLoading()
        view.onShowPersonalData(ombudsman)
    }
}