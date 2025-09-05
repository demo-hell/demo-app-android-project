package br.com.mobicare.cielo.contactCielo.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.contactCielo.data.mapper.ContactCieloWhatsappMapper
import br.com.mobicare.cielo.contactCielo.domain.model.ContactCieloWhatsapp
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetLocalSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.GetRemoteSegmentCodeUseCase
import br.com.mobicare.cielo.contactCielo.domain.useCase.SaveLocalSegmentCodeUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import kotlinx.coroutines.launch

class ContactCieloViewModel(
    private val getLocalSegmentCodeUseCase: GetLocalSegmentCodeUseCase,
    private val saveLocalSegmentCodeUseCase: SaveLocalSegmentCodeUseCase,
    private val getRemoteSegmentCodeUseCase: GetRemoteSegmentCodeUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _contactInfoSource = MutableLiveData<List<ContactCieloWhatsapp>>()
    val contactInfoSource = _contactInfoSource

    fun retrieveContactSourceInfo() {
        viewModelScope.launch {
            val isContactCieloEnabled = getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO).successValueOrNull ?: false
            val isContactCieloVirtualManagerEnabled =
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.CONTACT_CIELO_GERENTE_VIRTUAL).successValueOrNull ?: false

            getLocalSegmentCode(isContactCieloEnabled, isContactCieloVirtualManagerEnabled)
        }
    }

    private suspend fun getLocalSegmentCode(
        isContactCieloEnabled: Boolean, isContactCieloGerenteVirtualEnabled: Boolean
    ) {
        getLocalSegmentCodeUseCase().onSuccess { localSegmentCode ->
            if (localSegmentCode.isNotEmpty()) {
                updateContactInfoSource(localSegmentCode, isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
            } else {
                getRemoteSegmentCode(isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
            }
        }.onEmpty {
            getRemoteSegmentCode(isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
        }.onError {
            getRemoteSegmentCode(isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
        }
    }

    private suspend fun getRemoteSegmentCode(
        isContactCieloEnabled: Boolean, isContactCieloGerenteVirtualEnabled: Boolean
    ) {
        getRemoteSegmentCodeUseCase().onSuccess { segmentCode ->
            saveLocalSegmentCodeUseCase(segmentCode)
            getLocalSegmentCode(isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
        }.onError {
            _contactInfoSource.postValue(ContactCieloWhatsappMapper.retrieveInitialContactCieloWhatsappList(isContactCieloEnabled))
        }.onEmpty {
            _contactInfoSource.postValue(ContactCieloWhatsappMapper.retrieveInitialContactCieloWhatsappList(isContactCieloEnabled))
        }
    }

    private fun updateContactInfoSource(
        segmentCode: String, isContactCieloEnabled: Boolean, isContactCieloGerenteVirtualEnabled: Boolean
    ) {
        _contactInfoSource.postValue(
            ContactCieloWhatsappMapper.retrieveContactCieloWhatsapp(segmentCode, isContactCieloEnabled, isContactCieloGerenteVirtualEnabled)
        )
    }
}