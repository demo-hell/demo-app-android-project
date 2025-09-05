package br.com.mobicare.cielo.pixMVVM.presentation.home.utils

sealed class UserDataUiResult {

    data class WithMerchantAndDocument(val merchant: String, val document: String, val username: String?) : UserDataUiResult()
    data class WithMerchant(val merchant: String, val username: String?) : UserDataUiResult()
    data class WithDocument(val document: String, val username: String?) : UserDataUiResult()
    open class WithOnlyOptionalUserName(val username: String?) : UserDataUiResult()

}