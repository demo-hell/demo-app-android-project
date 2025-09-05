package br.com.mobicare.cielo.mySales.presentation.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler


import android.content.Context
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase

object  ViewModelUtils {

    suspend fun <T> setupError(
        apiError: CieloDataResult.APIError,
        viewState: MutableLiveData<MySalesViewState<T>>,
        context: Context? = null,
        userObjUseCase: GetUserObjUseCase,
        TAG: String? = "[ERROR]"
    ) {
        val errorMessage = apiError.apiException.newErrorMessage
        context?.let { itContext ->
            newErrorHandler(
                context = itContext,
                getUserObjUseCase = userObjUseCase,
                newErrorMessage = errorMessage,
                onHideLoading = { viewState.postValue(MySalesViewState.HIDE_LOADING) },
                onErrorAction = { viewState.postValue(MySalesViewState.ERROR(newErrorMessage = errorMessage)) }
            )
        } ?: viewState.postValue(MySalesViewState.ERROR(newErrorMessage = errorMessage))
    }



}