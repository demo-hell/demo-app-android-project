package br.com.mobicare.cielo.centralDeAjuda.search

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataVideo
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.search.HelpCenterSearchPresenter.Companion.TOKEN_ERROR_HTTP_STATUS
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HelpCenterSearchPresenterTest {

    @Mock
    lateinit var view: HelpCenterSearchContract.View

    @Mock
    lateinit var repository: CentralAjudaLogadoRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    lateinit var presenter: HelpCenterSearchPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = HelpCenterSearchPresenter(
            view,
            uiScheduler,
            ioScheduler,
            repository,
            userPreferences
        )
    }

    @Test
    fun `check search(term) max length 80, accepts special chars, specific term`() {
        val questionWith80Lenght =
            "Texto com 80 caracteres a ser filtrado pela busca. Terá caracteres especiais !@#"
        CentralAjudaLogadoRepository.allQuestionsList = listOf(
            questionModel(question = questionWith80Lenght),
            questionModel(question = "Texto com ")
        )

        val largerSearchTerm = "Texto com 80 caracteres a ser filtrado pela busca. Terá caracteres especiais !@#__________mais de 80, a ser ignorado____"

        presenter.search(largerSearchTerm)

        verify(view).onSearchResult(
            argThat {
                this.size == 1 &&
                        this.first().question?.contains(questionWith80Lenght) ?: false
            }
        )
    }

    @Test
    fun `check search(term) result order (found questions before found answers)`() {
        val mockList = listOf(
            questionModel(videoLink = "up", question = "test"),
            questionModel(videoLink = "down", answer = "test"),
            questionModel(videoLink = "up", question = "test"),
            questionModel(videoLink = "down", answer = "test"),
            questionModel(videoLink = "up", question = "test"),
            questionModel(videoLink = "down", answer = "test"),
            questionModel(videoLink = "up", question = "test"),
            questionModel(videoLink = "down", answer = "test"),
            questionModel(videoLink = "up", question = "test"),
            questionModel(videoLink = "down", answer = "test")
        )
        CentralAjudaLogadoRepository.allQuestionsList = mockList

        val searchTerm = "test"
        presenter.search(searchTerm)

        val resultList = argumentCaptor<List<FrequentQuestionsModelView>>()
        verify(view).onSearchResult(resultList.capture())

        assertEquals(mockList.size, resultList.lastValue.size)

        val stringBuilder = StringBuilder()
        resultList.lastValue.forEach { item ->
            stringBuilder.append("${item.videoLink}|")
        }

        assertEquals("up|up|up|up|up|down|down|down|down|down|", stringBuilder.toString())
    }

    @Test
    fun `check success on search(term) download questions`() {
        val mockResponse =
            listOf(
                questionData(), questionData(), questionData(), questionData(), questionData()
            )

        CentralAjudaLogadoRepository.allQuestionsList = null

        doReturn("token")
            .whenever(userPreferences).token

        doAnswer {
            Observable.just(mockResponse)
        }.whenever(repository).getFrequentQuestions(any())

        presenter.search("a")

        inOrder(view) {
            verify(view).showLoading()
            verify(view).hideLoading()
            verify(view).onSearchResult(
                argThat {
                    this.size == mockResponse.size
                }
            )

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `check error on search(term) download questions (no token)`() {
        CentralAjudaLogadoRepository.allQuestionsList = null

        doReturn(null)
            .whenever(userPreferences).token

        presenter.search("")

        inOrder(view) {
            verify(view).showError(
                argThat {
                    this.httpStatus == TOKEN_ERROR_HTTP_STATUS
                }
            )
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `check error on search(term) download questions`() {
        val retrofitException = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        CentralAjudaLogadoRepository.allQuestionsList = null

        doReturn("token")
            .whenever(userPreferences).token

        doAnswer {
            Observable.error<RetrofitException>(retrofitException)
        }.whenever(repository).getFrequentQuestions(any())

        presenter.search("")

        inOrder(view) {
            verify(view).showLoading()
            verify(view).hideLoading()
            verify(view).showError(
                argThat {
                    this.httpStatus == retrofitException.httpStatus
                }
            )
            verifyNoMoreInteractions()
        }
    }

    private fun questionModel(
        id: String = "0",
        question: String? = "a",
        answer: String? = "b",
        faqId: String = "0",
        videoLink: String? = null,
        subcategoryId: String = "0",
        likes: Int = 1,
        dislikes: Int = 0
    ) = FrequentQuestionsModelView(
        id, question, answer, faqId, videoLink, subcategoryId, likes, dislikes
    )

    private fun questionData(
        answer: String? = "b",
        dislikes: Int = 0,
        faqId: String = "0",
        id: String = "0",
        likes: Int = 1,
        priority: Int = 0,
        video: QuestionDataVideo? = mock(),
        question: String? = "a",
        subcategoryId: String = "0",
        tag: String = ""
    ) = QuestionDataResponse(
        answer, dislikes, faqId, id, likes, priority, video, question, subcategoryId, tag,
    )
}