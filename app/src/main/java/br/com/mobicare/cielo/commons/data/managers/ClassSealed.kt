package br.com.mobicare.cielo.commons.data.managers

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

/**
 * @author Enzo Teles
 * return success or error api
 * */
sealed class Result<out T: Any>{
    data class Success<out T: Any>(val data: T): Result<T>()
    sealed class Error(val error: ErrorMessage): Result<Nothing>(){
        //401
        class ExpiredSession(error: ErrorMessage):
            Error(error)
        //500
        class ServerError(error: ErrorMessage):
            Error(error)
        //420
        class Enhance(error: ErrorMessage):
            Error(error)
        //404
        class NotFound(error: ErrorMessage):
            Error(error)
        //503
        class BadRequest(error: ErrorMessage):
            Error(error)
        //400
        class Forbidden(error: ErrorMessage):
            Error(error)
    }
}