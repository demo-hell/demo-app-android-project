package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.MenuLabels.FREQUENTLY_ASK_QUESTIONS
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER
import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.FrequentQuestionHelpCenterPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.LoggedHelpCenterContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.activities.QuestionAndAnswerActivity
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.FragmentMostFrequentQuestionsBinding
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class MostFrequentQuestionsFragment : BaseFragment(),
    LoggedHelpCenterContract.FrequentQuestionsView,
    DefaultViewListAdapter.OnItemClickListener<FrequentQuestionsModelView> {

    var onLogoutListener: LogoutListener? = null

    private var _binding: FragmentMostFrequentQuestionsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val frequentQuestionsHelpCenterPresenter:
            FrequentQuestionHelpCenterPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(): MostFrequentQuestionsFragment = MostFrequentQuestionsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostFrequentQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMostFrequentQuestions.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                requireActivity(), LinearLayoutManager.HORIZONTAL, false
            )
            LinearSnapHelper().attachToRecyclerView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        callFrequentQuestions()
    }

    private fun callFrequentQuestions() {
        UserPreferences.getInstance().token.run {
            frequentQuestionsHelpCenterPresenter.getFrequentQuestions(this)
        }
    }

    override fun showFrequentQuestionsList(frequentQuestions: List<FrequentQuestionsModelView>) {
        if (isAttached()) {
            configureFrequentQuestionsAdapter(
                frequentQuestions.filterIndexed { index, _ ->
                    index <= 3
                }
            )
        }
    }

    private fun configureFrequentQuestionsAdapter(
        frequentQuestions:
        List<FrequentQuestionsModelView>
    ) {
        binding.recyclerMostFrequentQuestions.adapter = DefaultViewListAdapter(frequentQuestions, R.layout.item_frequent_question).apply {

            onItemClickListener = this@MostFrequentQuestionsFragment

            setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<FrequentQuestionsModelView> {

                override fun onBind(
                    item: FrequentQuestionsModelView,
                    holder: DefaultViewHolderKotlin
                ) {

                    val textQuestionContent = holder.itemView
                        .findViewById<TypefaceTextView>(R.id.textQuestionContent)
                    textQuestionContent.text = SpannableStringBuilder
                        .valueOf(item.question)

                    val cardContent = holder.itemView
                        .findViewById<CardView>(R.id.cardFrequentQuestionContent)

                    cardContent.setOnClickListener {
                        trackSelectContent(
                            contentName = item.question.orEmpty()
                        )
                        onItemClickListener?.onItemClick(item)
                    }
                }

            })
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            binding.recyclerMostFrequentQuestions.gone()
            binding.cardFrequentQuestionsError.gone()
            binding.cardFrequentQuestionsLoading.visible()
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            binding.cardFrequentQuestionsLoading.gone()
            binding.cardFrequentQuestionsError.gone()
            binding.recyclerMostFrequentQuestions.visible()
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            trackError(
                errorCode = error?.code.orEmpty(),
                errorMessage = error?.message.orEmpty(),
            )
            binding.recyclerMostFrequentQuestions.gone()
            binding.cardFrequentQuestionsError.visible()

            binding.linearFrequentQuestionsError.buttonLoanSimulationErrorRetry.setOnClickListener {
                callFrequentQuestions()
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun onItemClick(item: FrequentQuestionsModelView) {
        if (isAttached()) {
            requireActivity()
                .startActivity<QuestionAndAnswerActivity>(
                    QuestionAndAnswerActivity.USER_QUESTION to item
                )
        }
    }

    override fun onDestroy() {
        CentralAjudaLogadoRepository.allQuestionsList = null
        _binding = null
        super.onDestroy()
    }

    private fun trackSelectContent(contentName: String) {
        GA4.logSelectContent(
            HELP_CENTER,
            FREQUENTLY_ASK_QUESTIONS,
            contentName
        )
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER, errorCode, errorMessage)
        }
    }
}