package br.com.mobicare.cielo.commons.data.utils

import retrofit2.Response
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class RetrofitException internal constructor(message: String?,
                                             /** The request URL which produced the error.  */
                                             val url: String? = null,
                                             /** Response object containing status code, headers, body, etc.  */
                                             val response: Response<*>?,
                                             /** The event kind which triggered this error.  */
                                             val kind: RetrofitException.Kind, exception: Throwable?,
                                             /** The Retrofit this request was executed on  */
                                             val retrofit: Retrofit?,
                                             /** Http status */
                                             val httpStatus: Int) : RuntimeException(message, exception) {

    /** Identifies the event kind which triggered a [RetrofitException].  */
    enum class Kind {
        /** An [IOException] occurred while communicating to the server.  */
        NETWORK,
        /** A non-200 HTTP status code was received from the server.  */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED,
        INVALID_TOKEN,
        LOCKED

    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.

     * @throws IOException if unable to convert the body to the Cispecified `type`.
     */
    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {
        if (response?.errorBody() == null) {
            return null
        }
        val converter = retrofit?.responseBodyConverter<T>(type, arrayOfNulls<Annotation>(0))
        return response?.let {
            converter?.convert(it.errorBody())
        } ?: null
    }

    fun jsonError(): String {
        response?.let { resp ->
            resp.errorBody()?.let { responseBody ->
                val result = StringBuilder()

                val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
                reader.forEachLine {
                    result.append(it)
                }
                return result.toString()
            }
        }
        return ""
    }

    companion object {
        fun httpError(url: String?, response: Response<*>?, retrofit: Retrofit, httpStatus: Int): RetrofitException {
            val message = response?.code().toString() + " " + response?.message()
            return RetrofitException(message, url, response, Kind.HTTP, null, retrofit, httpStatus)
        }

        fun networkError(exception: IOException, httpStatus: Int = 500): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception, null, httpStatus)
        }

        fun unexpectedError(exception: Throwable, httpStatus: Int = 500): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception, null, httpStatus)
        }

        fun invalidToken(url: String, response: Response<*>?, retrofit: Retrofit, httpStatus: Int): RetrofitException {
            val message = response?.code().toString() + " " + response?.message()
            return RetrofitException(message, url, response, Kind.INVALID_TOKEN, null, retrofit, httpStatus)
        }
    }
}