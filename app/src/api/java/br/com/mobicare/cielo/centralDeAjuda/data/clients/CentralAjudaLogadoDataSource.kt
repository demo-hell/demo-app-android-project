package br.com.mobicare.cielo.centralDeAjuda.data.clients

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.*
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import io.reactivex.Observable

class CentralAjudaLogadoDataSource(context: Context) {
    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun faqCategories(imageType: String, accessToken: String) : Observable<List<HelpCategory>> {
        return api.faqCategories(imageType, accessToken)
    }

    fun getFrequentQuestions(accessToken: String):
            Observable<List<QuestionDataResponse>> {
        return api.getFrequentQuestions(accessToken)
    }

    fun faqSubCategories(access_token: String, faqId: String)
            = api.faqSubCategories(access_token, faqId)

    fun faqQuestions(accessToken: String, faqId: String, subcategoryId: String)
            = api.faqQuestions(accessToken, faqId, subcategoryId)

    fun getFaqQuestionsByName(accessToken: String, tag: String)
            = api.getFaqQuestionsByName(accessToken, tag)

    fun getQuestionDetails(accessToken: String,
                           faqId: String,
                           subcategoryId: String,
                           questionId: String): Observable<QuestionDataResponse> {
        return api.getQuestionDetails(accessToken, faqId, subcategoryId, questionId)
    }

    fun likeQuestion(accessToken: String, faqId: String,
                     subcategoryId: String,
                     questionId: String): Observable<QuestionReactionResponse> {
        return api.likeQuestion(accessToken, faqId, subcategoryId, questionId)
    }

    fun dislikeQuestion(accessToken: String, faqId: String,
                        subcategoryId: String,
                        questionId: String): Observable<QuestionReactionResponse> {
        return api.dislikeQuestion(accessToken, faqId, subcategoryId, questionId)
    }

    fun getFagContacts(accessToken: String)
            = api.getFaqContacts(accessToken)

    fun sendProtocol(ombudsman: OmbudsmanRequest)
            = api.sendProtocol(ombudsman)

}