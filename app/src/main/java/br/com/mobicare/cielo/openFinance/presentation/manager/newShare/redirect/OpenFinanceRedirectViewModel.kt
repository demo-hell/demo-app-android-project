package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.redirect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SIXTY
import br.com.mobicare.cielo.commons.constants.THIRTY
import br.com.mobicare.cielo.commons.utils.ONE_SECOND_MILLIS
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateAutomaticRedirect
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateRedirect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OpenFinanceRedirectViewModel : ViewModel() {
    private val _redirectLiveData =
        MutableLiveData<UIStateRedirect>()
    val redirectLiveData get() = _redirectLiveData

    private val _automaticRedirectLiveData =
        MutableLiveData<UIStateAutomaticRedirect>()
    val automaticRedirectLiveData get() = _automaticRedirectLiveData

    fun defineExpiredTimeConsent() {
        _redirectLiveData.postValue(UIStateRedirect.ConsentActive)
        waitFiveMinutes()
    }

    private fun waitFiveMinutes() {
        viewModelScope.launch {
            delay(FIVE * SIXTY * ONE_SECOND_MILLIS)
            _redirectLiveData.postValue(UIStateRedirect.ExpiredTimeConsent)
        }
    }

    fun automaticRedirect() {
        viewModelScope.launch {
            delay(ONE * THIRTY * ONE_SECOND_MILLIS)
            _automaticRedirectLiveData.postValue(UIStateAutomaticRedirect.AutomaticRedirect)
        }
    }

}