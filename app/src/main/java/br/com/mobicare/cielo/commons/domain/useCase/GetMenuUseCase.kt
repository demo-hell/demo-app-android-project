package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.domain.repository.remote.MenuRepository

class GetMenuUseCase(
    private val repository: MenuRepository,
) {

    suspend operator fun invoke(
        isLocal: Boolean = true,
        ftTapOnPhoneWhiteList: Boolean = false
    ) = repository.getMenu(isLocal, ftTapOnPhoneWhiteList)

}