package br.com.mobicare.cielo.mfa.faq


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas.CentralAjudaPerguntasContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas.CentralAjudaPerguntasPresenter
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import kotlinx.android.synthetic.main.fragment_faq_questions.*
import kotlinx.android.synthetic.main.item_list_text_line.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class FaqQuestionsFragment : BaseFragment(), CentralAjudaPerguntasContract.View, CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null

    val presenter: CentralAjudaPerguntasPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faq_questions, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureCieloNavigation()
        configureRecyclerView()
        this.presenter.setView(this)
        this.presenter.loadQuestionsByName()
    }

    private fun configureCieloNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar("Token")
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun configureRecyclerView() {
        this.recycler_view_itens_perguntas?.layoutManager = LinearLayoutManager(this.context)
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                this.cieloNavigation?.showError(it)
            }
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            this.cieloNavigation?.showLoading(true)
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            this.cieloNavigation?.showLoading(false)
        }
    }

    override fun onRetry() {
        presenter.loadQuestionsByName()
    }

    override fun showQuestions(questions: List<QuestionDataResponse>) {
        this.cieloNavigation?.showContent(true)
        if (isAttached()) {
            val adapter = DefaultViewListAdapter(questions)
            adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<QuestionDataResponse> {
                override fun onBind(item: QuestionDataResponse, holder: DefaultViewHolderKotlin) {
                    holder.mView.textItemList.text = item.question
                    holder.mView.setOnClickListener {
                        this@FaqQuestionsFragment.presenter.onQuestionSelected(item)
                    }
                }
            })
            this.recycler_view_itens_perguntas?.adapter = adapter
        }
        this.cieloNavigation?.showHelpButton(false)
    }

    override fun showQuestionAnswerDetail(obj: QuestionRequestModelView) {
        findNavController().navigate(FaqQuestionsFragmentDirections.actionFaqQuestionsFragmentToFaqAnswerFragment(obj))
    }
}
