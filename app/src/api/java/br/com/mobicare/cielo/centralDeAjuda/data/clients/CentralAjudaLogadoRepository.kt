package br.com.mobicare.cielo.centralDeAjuda.data.clients

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.*
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.*
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CentralAjudaLogadoRepository(private val remoteDataSource: CentralAjudaLogadoDataSource,
private val configuration: ConfigurationPreference) : DisposableDefault {

    private var disposable = CompositeDisposable()

    companion object {
        var allQuestionsList: List<FrequentQuestionsModelView>? = null
    }

    override fun disposable() {
        disposable.clear()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    fun faqCategories(imageType: String, access_token: String, callback: APICallbackDefault<List<HelpCategory>, String>) {

        disposable.add(remoteDataSource.faqCategories(imageType, access_token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun faqSubCategories(access_token: String, faqId: String, callback: APICallbackDefault<List<SubCategorie>, String>) {

        disposable.add(
                remoteDataSource.faqSubCategories(access_token, faqId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )

    }

    fun getFrequentQuestions(accessToken: String):
            Observable<List<QuestionDataResponse>> {
        return remoteDataSource.getFrequentQuestions(accessToken)
    }

    fun getQuestionDetail(
            accessToken: String,
            questionRequest: QuestionRequestModelView,
    ):
            Observable<QuestionDataResponse> {

        return remoteDataSource.getQuestionDetails(accessToken,
                questionRequest.faqId, questionRequest.subcategoryId, questionRequest.questionId)
    }

    fun likeQuestion(
            accessToken: String,
            questionRequest: QuestionRequestModelView,
    ):
            Observable<QuestionReactionResponse> {

        return remoteDataSource.likeQuestion(accessToken, questionRequest.faqId,
                questionRequest.subcategoryId,
                questionRequest.questionId)
    }

    fun dislikeQuestion(
            accessToken: String,
            questionRequest: QuestionRequestModelView,
    ):
            Observable<QuestionReactionResponse> {

        return remoteDataSource.dislikeQuestion(accessToken, questionRequest.faqId,
                questionRequest.subcategoryId,
                questionRequest.questionId)
    }

    fun faqQuestions(accessToken: String, faqId: String, subcategoryId: String, callback: APICallbackDefault<List<QuestionDataResponse>, String>) {

        disposable.add(
                remoteDataSource.faqQuestions(accessToken, faqId, subcategoryId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )

    }

    fun getFaqQuestionsByName(tagToApi: String?, accessToken: String, callback: APICallbackDefault<List<QuestionDataResponse>, String>) {
        var tag = ""
        if (tagToApi != null) {
            tag = tagToApi
        } else {
            tag = configuration.getConfigurationValue(
                    ConfigurationDef.TAG_HELP_CENTER_MFA, "")
        }

        disposable.add(
                remoteDataSource.getFaqQuestionsByName(accessToken, tag)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )
    }

    fun getFaqContacts(
            accessToken: String,
            callback: APICallbackDefault<List<Contact>, String>,
    ) {
        disposable.add(
                remoteDataSource.getFagContacts(accessToken)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )
    }

    fun sendProtocol(
            ombudsman: OmbudsmanRequest,
            callback: APICallbackDefault<OmbudsmanResponse, String>,
    ) {
        disposable.add(
                remoteDataSource.sendProtocol(ombudsman)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )
    }
}