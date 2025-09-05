package br.com.mobicare.cielo.commons.data.utils

import android.annotation.SuppressLint
import android.content.Context
import br.com.mobicare.cielo.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type


/**
 * Created by benhur.souza on 07/04/2017.
 */

class RxErrorHandlingCallAdapterFactory private constructor(val context: Context) :
    CallAdapter.Factory() {
    private val original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        return RxCallAdapterWrapper(
            context,
            retrofit,
            original.get(returnType, annotations, retrofit) as CallAdapter<R, *>?
        )
    }

    private class RxCallAdapterWrapper(
        private val context: Context,
        private val retrofit: Retrofit,
        private val wrapped: CallAdapter<R, *>?
    ) : CallAdapter<R, Any> {

        @SuppressLint("CheckResult")
        override fun adapt(call: Call<R>): Any {

            val any = wrapped!!.adapt(call)

            try {
                if (any is Observable<*>) {
                    return any.onErrorResumeNext { t: Throwable ->
                        Observable.error(asRetrofitException(t))
                    }
                }

                if (any is Maybe<*>) {
                    return any.onErrorResumeNext { t: Throwable ->
                        Maybe.error(asRetrofitException(t))
                    }
                }

                if (any is Single<*>) {
                    return any.onErrorResumeNext { t: Throwable ->
                        Single.error(asRetrofitException(t))
                    }
                }

                if (any is Flowable<*>) {
                    return any.onErrorResumeNext { t: Throwable ->
                        Flowable.error(asRetrofitException(t))
                    }
                }

                if (any is Completable) {
                    return any.onErrorResumeNext { t: Throwable ->
                        Completable.error(asRetrofitException(t))
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            return any

        }

        override fun responseType(): Type {
            return wrapped!!.responseType()
        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            // We had non-200 http error
            if (throwable is HttpException) {
                val httpException = throwable
                val response = httpException.response()
                    ?: return RetrofitException.unexpectedError(throwable, httpException.code())

                if (httpException.code() == 401)
                    return RetrofitException.invalidToken(
                        url = response.raw().request().url().toString(),
                        response = response,
                        retrofit = retrofit,
                        httpStatus = httpException.code()
                    )

                if (httpException.code() == 502 || httpException.code() == 404)
                    return RetrofitException.httpError(
                        response.raw().request().url().toString(),
                        response,
                        retrofit,
                        httpException.code()
                    )

                if (httpException.code() in 500..599)
                    return RetrofitException.networkError(IOException(context.getString(R.string.text_server_error_message)))

                return try {
                    RetrofitException.httpError(
                        response.raw().request().url().toString(),
                        response,
                        retrofit,
                        httpException.code()
                    )
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    RetrofitException.networkError(IOException(context.getString(R.string.text_server_error_message)))
                }
            }
            // A network error happened
            if (throwable is IOException) {
                return RetrofitException.networkError(throwable)
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable)

        }
    }

    companion object {
        fun create(context: Context): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory(context)
        }
    }
}