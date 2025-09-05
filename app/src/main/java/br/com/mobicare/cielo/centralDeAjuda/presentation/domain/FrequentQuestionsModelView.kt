package br.com.mobicare.cielo.centralDeAjuda.presentation.domain

import android.os.Parcelable
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FrequentQuestionsModelView(val id: String,
                                      val question: String?,
                                      val answer: String?,
                                      val faqId: String,
                                      val videoLink: String?,
                                      val subcategoryId: String,
                                      val likes: Int,
                                      val dislikes: Int): Parcelable {

    companion object {
        fun mapListFromQuestionResponse(response: List<QuestionDataResponse>): MutableList<FrequentQuestionsModelView> {
            val frequentQuestionList = mutableListOf<FrequentQuestionsModelView>()
            response.forEach {
                if (it.answer.isNullOrBlank().not()
                    && it.question.isNullOrBlank().not()
                ) {

                    var videoLink: String? = null

                    it.video?.let { video ->
                        videoLink = "${video.link}/${video.code}"
                    }

                    frequentQuestionList.add(
                        FrequentQuestionsModelView(
                            it.id,
                            it.question,
                            it.answer,
                            it.faqId,
                            videoLink,
                            it.subcategoryId,
                            it.likes,
                            it.dislikes
                        )
                    )
                }
            }
            return frequentQuestionList
        }
    }
}