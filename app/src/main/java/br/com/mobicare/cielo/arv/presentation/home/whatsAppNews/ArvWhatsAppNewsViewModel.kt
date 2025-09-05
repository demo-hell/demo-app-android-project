package br.com.mobicare.cielo.arv.presentation.home.whatsAppNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.ARV_WHATSAPP_NEWS_ALREADY_VIEWED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.ARV_WHATSAPP_NEWS_DISMISSED_COUNTER
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.ARV_ENABLE_WHATSAPP_NEWS
import kotlinx.coroutines.launch

class ArvWhatsAppNewsViewModel(
    private val getUserViewHistoryUseCase: GetUserViewHistoryUseCase,
    private val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase,
    private val getUserViewCounterUseCase: GetUserViewCounterUseCase,
    private val saveUserViewCounterUseCase: SaveUserViewCounterUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private val _enableNews = MutableLiveData<Boolean>()
    val enableNews: LiveData<Boolean> get() = _enableNews

    fun checkEnablement() {
        viewModelScope.launch {
            getFeatureTogglePreferenceUseCase(ARV_ENABLE_WHATSAPP_NEWS).onSuccess { isTrue ->
                if (isTrue.not()) {
                    _enableNews.postValue(false)
                    return@launch
                }
            }

            getUserViewHistoryUseCase(ARV_WHATSAPP_NEWS_ALREADY_VIEWED).onSuccess { isAlreadyViewed ->
                if (isAlreadyViewed) {
                    _enableNews.postValue(false)
                    return@launch
                }
            }

            getUserViewCounterUseCase(ARV_WHATSAPP_NEWS_DISMISSED_COUNTER).onSuccess { dismissCounter ->
                _enableNews.postValue(dismissCounter < THREE)
            }
        }
    }

    fun saveViewed() {
        viewModelScope.launch {
            saveUserViewHistoryUseCase(ARV_WHATSAPP_NEWS_ALREADY_VIEWED)
        }
    }

    fun updateDismissCounter() {
        viewModelScope.launch {
            saveUserViewCounterUseCase(ARV_WHATSAPP_NEWS_DISMISSED_COUNTER)
        }
    }
}
