package br.com.mobicare.cielo.migration

import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.PasswordError
import br.com.mobicare.cielo.migration.domain.MigrationRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MigrationRepository(private var remoteDataSource: MigrationDataSource) {
    private var compositeDisp = CompositeDisposable()

    fun migrationUser(accessToken: String, authorization: String,migratonRequest: MigrationRequest,
                      callback: APICallbackDefault<MultichannelUserTokenResponse, String>) {
        compositeDisp.add(remoteDataSource.migrationUser(migratonRequest, accessToken, authorization)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    if (it is RetrofitException) {
                        val exception = it as RetrofitException
                        if (exception.httpStatus == 420) {
                            val json = exception.jsonError()
                            if (json.startsWith("[{") && json.contains("type")) {
                                val gson = Gson()
                                val listType = object : TypeToken<List<PasswordError>>() {}.type
                                val errorList = gson.fromJson<List<PasswordError>>(json, listType)
                                if (!errorList.isNullOrEmpty()) {
                                    val error = errorList.first()
                                    val errorMessage = ErrorMessage().apply {
                                        this.httpStatus = exception.httpStatus
                                        this.message = error.errorMessage
                                        this.code = error.type
                                        this.errorCode = error.errorCode
                                        this.errorMessage = error.errorMessage
                                    }
                                    callback.onError(errorMessage)
                                    return@subscribe
                                }
                            }
                        }
                    }
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }


    fun getMigrationVefication(accessToken: String): Observable<Response<Unit>> {
        return remoteDataSource.getMigrationVerification(accessToken)
    }

}