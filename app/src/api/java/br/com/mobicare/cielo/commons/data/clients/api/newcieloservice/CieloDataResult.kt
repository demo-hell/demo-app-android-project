package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice

import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

sealed class CieloDataResult<out T> {
    data class Success<out T>(val value: T) : CieloDataResult<T>()
    data class Empty(val code: Int = ZERO) : CieloDataResult<Nothing>()
    data class APIError(
        val apiException: CieloAPIException
    ) : CieloDataResult<Nothing>()
}

inline fun <T : Any> CieloDataResult<T>.onSuccess(action: (T) -> Unit): CieloDataResult<T> {
    if (this is CieloDataResult.Success) {
        action(this.value)
    }
    return this
}

inline fun <T : Any?> CieloDataResult<T?>.onError(action: (CieloDataResult.APIError) -> Unit): CieloDataResult<T?> {
    if (this is CieloDataResult.APIError) {
        action(this)
    }
    return this
}

inline fun <T : Any?> CieloDataResult<T?>.onEmpty(action: (CieloDataResult.Empty) -> Unit): CieloDataResult<T?> {
    if (this is CieloDataResult.Empty) {
        action(this)
    }
    return this
}

val <T : Any?> CieloDataResult<T?>.successValueOrNull: T?
    get() = if (this is CieloDataResult.Success) this.value else null

val <T : Any?> CieloDataResult<T?>.isSuccess: Boolean
    get() = this is CieloDataResult.Success

val <T : Any?> CieloDataResult<T?>.asSuccess: CieloDataResult.Success<T?>
    get() = this as CieloDataResult.Success<T?>

val <T : Any?> CieloDataResult<T?>.isError: Boolean
    get() = this is CieloDataResult.APIError

val <T : Any?> CieloDataResult<T?>.asError: CieloDataResult.APIError
    get() = this as CieloDataResult.APIError

val <T : Any?> CieloDataResult<T?>.isEmpty: Boolean
    get() = this is CieloDataResult.Empty
