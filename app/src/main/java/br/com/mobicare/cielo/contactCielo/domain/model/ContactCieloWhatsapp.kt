package br.com.mobicare.cielo.contactCielo.domain.model

import br.com.mobicare.cielo.commons.ui.BaseFragment
import org.androidannotations.annotations.res.StringRes

data class ContactCieloWhatsapp(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val whatsappNumber: Int,
    @StringRes val whatsappMessage: Int? = null,
    @StringRes val contentName: Int,
    val baseFragment: BaseFragment
)
