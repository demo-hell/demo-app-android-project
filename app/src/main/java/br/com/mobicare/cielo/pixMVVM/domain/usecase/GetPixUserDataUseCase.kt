package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult

class GetPixUserDataUseCase(private val menuPreference: MenuPreference) {

    operator fun invoke(): UserDataUiResult {
        val establishment = menuPreference.getEstablishment()
        val userName = establishment?.tradeName
        val document = establishment?.cnpj
        val merchant = establishment?.ec

        return if (merchant?.isNotBlank() == true && document?.isNotBlank() == true) {
            UserDataUiResult.WithMerchantAndDocument(merchant, document, userName)
        } else if (merchant?.isNotBlank() == true && document.isNullOrBlank()) {
            UserDataUiResult.WithMerchant(merchant, userName)
        } else if (merchant.isNullOrBlank() && document?.isNotBlank() == true) {
            UserDataUiResult.WithDocument(document, userName)
        } else {
            UserDataUiResult.WithOnlyOptionalUserName(userName)
        }
    }

}