package br.com.mobicare.cielo.commons.data.clients.api

sealed class CieloDataResult<out T> {

    data class Success<out T>(val value: T) : CieloDataResult<T>()

    data class NetworkError(
        val title: String = "Error",
        val message: String = "",
        val logout: Boolean = false,
        val httpStatus: Int = 500,
        val brokenServiceUrl: String = "none"
    ) : CieloDataResult<Nothing>()

    data class GenericError(
        val exception: Exception
    ) : CieloDataResult<Nothing>()

}

inline fun <T : Any> CieloDataResult<T>.onSuccess(action: (T) -> Unit): CieloDataResult<T> {
    if (this is CieloDataResult.Success) {
        action(this.value)
    }
    return this
}

inline fun <T : Any> CieloDataResult<T>.onError(action: (CieloDataResult.NetworkError) -> Unit): CieloDataResult<T> {
    if (this is CieloDataResult.NetworkError) {
        action(this)
    }
    return this
}

inline fun <T : Any> CieloDataResult<T>.onFinally(action: () -> Unit) {
    action()
}
