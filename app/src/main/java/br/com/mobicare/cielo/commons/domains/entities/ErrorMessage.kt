package br.com.mobicare.cielo.commons.domains.entities

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException


class ErrorMessage : Message() {
    var title: String = "Error"
    var code: String = "500"
    var logout: Boolean = false
    var brokenServiceUrl: String = "none"
    var httpStatus: Int = 500


    var errorCode: String = ""
    var errorMessage: String = ""
        set(value) {
            field = value
            message = value
        }

    var listErrorServer: List<ErrorListServer> = listOf()
    var statusText: String? = null

    fun setType(kind: RetrofitException.Kind) {
        logout = kind === RetrofitException.Kind.INVALID_TOKEN
    }

    companion object {
        fun fromThrowable(error: Throwable): ErrorMessage {
            return APIUtils.convertToErro(error)
        }
    }
}
