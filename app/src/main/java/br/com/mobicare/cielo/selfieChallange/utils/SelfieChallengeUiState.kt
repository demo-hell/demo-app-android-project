package br.com.mobicare.cielo.selfieChallange.utils

sealed class SelfieChallengeUiState {
    class SelfieChallengeSuccess(val selfieChallengeResult: SelfieChallengeResult) : SelfieChallengeUiState()
    class StoneAgeTokenSuccess(val token: String) : SelfieChallengeUiState()
    class StoneAgeError(val message: Int) : SelfieChallengeUiState()
    class GenericError(val message: String? = null): SelfieChallengeUiState()
    class UserCancelled: SelfieChallengeUiState()
    class SelfieError(val errorCode: String? = null): SelfieChallengeUiState()
}