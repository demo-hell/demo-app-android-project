package br.com.mobicare.cielo.contactCielo.data.mapper

import br.com.cielo.libflue.util.SEVEN
import br.com.cielo.libflue.util.SIX
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaLogadoDuvidasGeraisFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaLogadoVirtualManagerFragment
import br.com.mobicare.cielo.commons.constants.EIGHT
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.contactCielo.domain.model.ContactCieloWhatsapp

object ContactCieloWhatsappMapper {

    private val allowedSegmentCodes =
        listOf(SIX.toString(), SEVEN.toString(), EIGHT.toString())

    val virtualManager = ContactCieloWhatsapp(
        title = R.string.contact_cielo_virtual_manager_title,
        description = R.string.contact_cielo_virtual_manager_description,
        whatsappNumber = R.string.contact_cielo_virtual_manager_whatsapp_number,
        whatsappMessage = null,
        contentName = R.string.contact_cielo_virtual_manager_content_name,
        baseFragment = CentralAjudaLogadoVirtualManagerFragment()
    )
    val generalDoubts = ContactCieloWhatsapp(
        title = R.string.contact_cielo_general_doubts_title,
        description = R.string.contact_cielo_general_doubts_description,
        whatsappNumber = R.string.contact_cielo_general_doubts_whatsapp_number,
        whatsappMessage = null,
        contentName = R.string.contact_cielo_general_doubts_content_name,
        baseFragment = CentralAjudaLogadoDuvidasGeraisFragment()
    )

    fun retrieveInitialContactCieloWhatsappList(isContactCieloEnabled: Boolean) =
        mutableListOf<ContactCieloWhatsapp>().apply {
            if (isContactCieloEnabled) {
                add(generalDoubts)
            }
        }


    fun retrieveContactCieloWhatsapp(
        segmentCode: String,
        isContactCieloEnabled: Boolean,
        isContactCieloGerenteVirtualEnabled: Boolean
    ): List<ContactCieloWhatsapp> {

        val isSegmentCodeAllowed = allowedSegmentCodes.any { it == segmentCode }
        return retrieveInitialContactCieloWhatsappList(isContactCieloEnabled).apply {
            if (isContactCieloEnabled && isSegmentCodeAllowed && isContactCieloGerenteVirtualEnabled) {
                add(ZERO, virtualManager)
            }
        }
    }
}