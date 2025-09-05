package br.com.mobicare.cielo.commons.data.clients.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

open class SafeApiCaller(private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = backgroundDispatcher,
        apiCall: suspend () -> T
    ): CieloDataResult<T> {
        return withContext(dispatcher) {
            callApi(apiCall)
        }
    }

    private suspend fun <T> callApi(apiCall: suspend () -> T): CieloDataResult<T> {
        return try {
            val result = apiCall.invoke()
            onResult(result)
        } catch (exception: Exception) {
            onError(Exception())
        }
    }

    private fun <T> onResult(result: T): CieloDataResult<T> {
        if (result is Response<*> && !result.isSuccessful) {
            return createNetworkError(result)
        }
        return CieloDataResult.Success(result)
    }

    private fun <T> onError(exception: Exception): CieloDataResult<T> {
        return CieloDataResult.GenericError(exception)
    }

    private fun createNetworkError(result: Response<*>): CieloDataResult.NetworkError {
        val title = result.errorBody().toString()
        val isLogout = result.code() == 401
        val httpStatus = result.code()
        val brokenServiceUrl = result.raw().request().url().toString() ?: "none"
        val message = result.raw().message() ?: "none"
        return CieloDataResult.NetworkError(
            title = title,
            logout = isLogout,
            httpStatus = httpStatus,
            brokenServiceUrl = brokenServiceUrl,
            message = message
        )
    }
}