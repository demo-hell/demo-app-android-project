package br.com.mobicare.cielo.login.firstAccess.presentation.ui.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.mobicare.cielo.pix.constants.EMPTY

class FirstAccessCreateViewModel(val state: SavedStateHandle): ViewModel() {
    fun getEc() = state.get<String>(EC_KEY) ?: EMPTY
    fun getCpf() = state.get<String>(CPF_KEY) ?: EMPTY
    fun getEmail() = state.get<String>(EMAIL_KEY) ?: EMPTY

    fun keepUserInfo(ec: String, cpf: String, email: String) {
        state[EC_KEY] = ec
        state[CPF_KEY] = cpf
        state[EMAIL_KEY] = email
    }

    companion object {
        private const val EC_KEY = "EC_KEY"
        private const val CPF_KEY = "CPF_KEY"
        private const val EMAIL_KEY = "EMAIL_KEY"
    }
}