package br.com.mobicare.cielo.commons.data.repository.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.datasource.MenuLocalDataSource
import br.com.mobicare.cielo.commons.domain.datasource.MenuRemoteDataSource
import br.com.mobicare.cielo.commons.domain.repository.remote.MenuRepository
import br.com.mobicare.cielo.main.domain.AppMenuResponse

class MenuRepositoryImpl(
    private val remoteDataSource: MenuRemoteDataSource,
    private val localDataSource: MenuLocalDataSource
) : MenuRepository {

    override suspend fun getMenu(
        isLocal: Boolean,
        ftTapOnPhoneWhiteList: Boolean
    ): CieloDataResult<AppMenuResponse> {
        lateinit var result: CieloDataResult<AppMenuResponse>

        if (isLocal) {
            localDataSource.getMenu().onSuccess {
                result = CieloDataResult.Success(it)
            }.onError {
                result = getMenuFromRemoteRepository(ftTapOnPhoneWhiteList)
            }.onEmpty {
                result = getMenuFromRemoteRepository(ftTapOnPhoneWhiteList)
            }
        } else {
            result = getMenuFromRemoteRepository(ftTapOnPhoneWhiteList)
        }

        return result
    }

    private suspend fun getMenuFromRemoteRepository(ftTapOnPhoneWhiteList: Boolean): CieloDataResult<AppMenuResponse> {
        lateinit var result: CieloDataResult<AppMenuResponse>

        remoteDataSource.getMenu()
            .onSuccess {
                val isSuccessOnSave = saveMenuLocalStorage(it, ftTapOnPhoneWhiteList)
                result = if (isSuccessOnSave) {
                    CieloDataResult.Success(it)
                } else {
                    CieloDataResult.Empty()
                }
            }
            .onError {
                result = it
            }
            .onEmpty {
                result = it
            }

        return result
    }

    private suspend fun saveMenuLocalStorage(
        menu: AppMenuResponse,
        ftTapOnPhoneWhiteList: Boolean
    ): Boolean {
        var isSuccessOnSave = false

        getWhiteList(ftTapOnPhoneWhiteList)

        localDataSource.saveMenuLocalStorage(menu)
            .onSuccess {
                isSuccessOnSave = true
            }

        return isSuccessOnSave
    }

    /**
     * It is necessary to cache the result of the whitelist, because in the HomePresenter.kt class in the processMenu method, filtering is done based on this <isEligible> white list flag
     */
    private suspend fun getWhiteList(ftTapOnPhoneWhiteList: Boolean) {
        var isEligible = false

        if (ftTapOnPhoneWhiteList) {
            remoteDataSource.getPosVirtualWhiteList()
                .onSuccess {
                    isEligible = it.eligible.not()
                }
        } else {
            isEligible = true
        }

        localDataSource.savePosVirtualWhiteList(isEligible)
    }

}