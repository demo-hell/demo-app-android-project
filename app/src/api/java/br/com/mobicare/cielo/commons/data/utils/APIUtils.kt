package br.com.mobicare.cielo.commons.data.utils

import br.com.mobicare.cielo.commons.data.domain.SystemResponse
import br.com.mobicare.cielo.commons.domains.entities.ErrorListServer
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

object APIUtils {

    fun convertToErro(erro: Throwable): ErrorMessage {

        if (erro is RetrofitException) {
            return convertToErro(erro)
        }

        return ErrorMessage()
    }

    private fun convertToErro(error: RetrofitException): ErrorMessage {
        var responseError = ErrorMessage()

        try {
            val json = error.jsonError()
            if (json.startsWith("[{")) {
                setListErros(json, responseError)
            } else {
                val localError = getErrorMessage(json)
                localError?.let {
                    responseError = it
                }
            }

            responseError.setType(error.kind)
            responseError.brokenServiceUrl = error.url ?: "none"
            responseError.code = error.httpStatus.toString()
            responseError.httpStatus = error.httpStatus

            if (responseError.listErrorServer.isNotEmpty()) {
                responseError.message = concatListError(responseError.listErrorServer)
            } else if (responseError.errorMessage.isNotEmpty()) {
                responseError.message = responseError.errorMessage
            } else {
                responseError.errorMessage =
                    "Infelizmente ocorreu algum erro. Por favor, tente novamente."
                responseError.message = responseError.errorMessage
            }

            if (responseError.errorMessage.isEmpty()) {
                responseError.message
            }

            if (error.response != null) {
                responseError.statusText = error.response.raw()?.message() ?: ""
            } else {
                responseError.statusText = "none"
            }

            val code = error.httpStatus
            responseError.logout = code == 401

        } catch (ex: IOException) {
            responseError = updateErrorWithRetrofitEx(responseError, error)
        } catch (ex: Exception) {
            responseError = updateErrorWithRetrofitEx(responseError, error)
        }

        responseError.httpStatus = error.httpStatus
        return responseError
    }

    fun convertToErro(error: Response<Void>): ErrorMessage {
        var responseError = ErrorMessage()

        try {
            val json = error.errorBody()!!.string()
            if (json.startsWith("[{")) {
                setListErros(json, responseError)
            } else {
                val localError = getErrorMessage(json)  //= error.getErrorBodyAs(ErrorMessage::class.java)
                localError?.let {
                    responseError = it
                }
            }
            responseError.code = error.code().toString()
            responseError.httpStatus = error.code()

            if (responseError.listErrorServer.isNotEmpty()) {
                responseError.message = concatListError(responseError.listErrorServer)
            } else if (responseError.errorMessage.isNotEmpty()) {
                responseError.message = responseError.errorMessage
            }

            if (error != null) {
                responseError.statusText = error.raw()?.message() ?: ""
            } else {
                responseError.statusText = "none"
            }
        } catch (ex: IOException) {
            responseError = updateErrorWithRetrofitEx(responseError, error)
        } catch (ex: Exception) {
            responseError = updateErrorWithRetrofitEx(responseError, error)
        }

        responseError.httpStatus = error.code()
        return responseError
    }

    private fun setListErros(json: String, responseError: ErrorMessage) {
        val gson = Gson()
        val listType = object : TypeToken<List<ErrorListServer>>() {}.type
        val newList = gson.fromJson<List<ErrorListServer>>(json, listType)
        responseError.listErrorServer = newList
    }

    private fun getErrorMessage(json: String): ErrorMessage? {
        val gson = Gson()

        return if (json.isNotBlank()) {
            gson.fromJson<ErrorMessage>(json, ErrorMessage::class.java)
        } else {
            null
        }
    }

    private fun concatListError(listErrorServer: List<ErrorListServer>): String {
        val resultError = StringBuffer()
        listErrorServer.forEach {
            if (resultError.isNotEmpty())
                resultError.append(", ")
            resultError.append(it.errorMessage)
        }
        return resultError.toString()
    }

    private fun updateErrorWithRetrofitEx(responseError: ErrorMessage, error: RetrofitException):
            ErrorMessage {

        responseError.apply {

            this.setType(error.kind)
            this.brokenServiceUrl = error.url ?: "none"
            this.code = error.httpStatus.toString()
            this.httpStatus = error.httpStatus
            if (error.response != null) this.statusText = error.response.raw()?.message()
            else this.statusText = "none"

        }

        return responseError
    }

    private fun updateErrorWithRetrofitEx(responseError: ErrorMessage, error: Response<Void>):
            ErrorMessage {

        responseError.apply {
            this.code = error.code().toString()
            this.httpStatus = error.code()
            if (error != null) this.statusText = error.raw()?.message()
            else this.statusText = "none"

        }

        return responseError
    }

    fun convertToError(code: Int, error: ResponseBody?): ErrorMessage {
        val responseError: ErrorMessage = ErrorMessage()
        try {
            error?.also { itError ->
                val message = itError.string()
                val type = object : TypeToken<ArrayList<SystemResponse>>() {}.type
                val errors: List<SystemResponse> = Gson().fromJson(message, type)
                var errorMessage = "none"
                var errorCode = "BAD_REQUEST"
                if (errors.isNotEmpty()) {
                    val error = errors.first()
                    errorCode = error.errorCode
                    errorMessage = error.errorMessage
                }
                responseError.httpStatus = code
                responseError.code = errorCode
                responseError.statusText = errorMessage
            }
        } catch (t: Throwable) {
            responseError.httpStatus = code
            responseError.statusText =
                "Infelizmente ocorreu algum erro. Por favor, tente novamente."
            responseError.logout = code == 401
        }
        return responseError
    }

    fun createResponse(errorMessage: ErrorMessage): Response<String?> = Response.error(
            errorMessage.httpStatus,
            ResponseBody.create(
                    MediaType.parse("application/json"),
                    Gson().toJson(errorMessage)))
}