package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.categories

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.MenuLabels.CATEGORIES
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.TECHNICAL_SUPPORT_HELP_CENTER
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_CATEGORY_ID
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_CATEGORY_NAME
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.typeDensity
import br.com.mobicare.cielo.databinding.ContentCategoriesItemBinding
import br.com.mobicare.cielo.databinding.ContentCategoriesMainFragmentBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class CentralAjudaCategoriesFragment : BaseFragment(), CentralAjudaCategoriesContract.View {
    private var _binding: ContentCategoriesMainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var typeDensity: String
    private var logoutListener: LogoutListener? = null

    val presenter: CentralAjudaCategoriesPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(logoutListener: LogoutListener?): CentralAjudaCategoriesFragment {
            val fragment = CentralAjudaCategoriesFragment()
            fragment.logoutListener = logoutListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ContentCategoriesMainFragmentBinding.inflate(inflater, container, false)
        .also {
            _binding = it
        }.root


    override fun onDestroy() {
        presenter.onCleared()
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        typeDensity = requireContext().typeDensity()

        presenter.setView(this)
        val accessToken = UserPreferences.getInstance().token
        presenter.loadFaqCategories(typeDensity, accessToken)
    }

    override fun showFaqCategories(faqList: List<HelpCategory>) {
        if (isAttached()) {
            binding.apply {
                recycleView.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

                val adapter = DefaultViewListAdapter(faqList, R.layout.content_categories_item)
                adapter.setBindViewHolderCallback(
                    object :
                        DefaultViewListAdapter.OnBindViewHolder<HelpCategory> {
                        override fun onBind(
                            item: HelpCategory,
                            holder: DefaultViewHolderKotlin,
                        ) {

                            ContentCategoriesItemBinding.bind(holder.mView).apply {
                                progressView.visibility = View.VISIBLE
                                imageView.visibility = View.INVISIBLE
                                textViewTitle.text = item.category
                                textViewDescription.text = item.description
                                cardViewMain.setOnClickListener {
                                    val currentButtonLabel =
                                        textViewTitle.text.toString()
                                    trackSelectContent(
                                        contentName = currentButtonLabel,
                                    )

                                    requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
                                        ARG_PARAM_CATEGORY_ID to item.id,
                                        ARG_PARAM_CATEGORY_NAME to item.category,
                                    )
                                }
                                Picasso.get()
                                    .load(Uri.parse(item.icon))
                                    .into(
                                        imageView,
                                        object : Callback {
                                            override fun onSuccess() {
                                                progressView.gone()
                                                imageView.visible()
                                            }

                                            override fun onError(e: Exception?) {
                                                e?.printStackTrace()
                                            }
                                        },
                                    )
                            }
                        }
                    },
                )

                recycleView.adapter = adapter
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            super.hideLoading()
            binding.apply {
                recycleView.visible()
                cardViewStep.gone()
                contentCategoriesProgress.gone()
                contentCategoriesError.contentLitleErrorMain.gone()
            }
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            super.showLoading()
            binding.apply {
                recycleView.gone()
                cardViewStep.visible()
                contentCategoriesProgress.visible()
                contentCategoriesError.contentLitleErrorMain.gone()
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            trackError(
                errorCode = error?.code.orEmpty(),
                errorMessage = error?.message.orEmpty(),
            )
            binding.apply {
                recycleView.gone()
                cardViewStep.visible()
                contentCategoriesProgress.gone()
                contentCategoriesError.contentLitleErrorMain.visible()
                contentCategoriesError.contentLitleErrorButtonRetry.setOnClickListener {
                    val accessToken = UserPreferences.getInstance().token
                    presenter.loadFaqCategories(typeDensity, accessToken)
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(requireContext(), true)
        }
    }

    private fun trackSelectContent(contentName: String) {
        GA4.logSelectContent(HELP_CENTER, CATEGORIES, contentName)
        GA4.logClick(TECHNICAL_SUPPORT_HELP_CENTER, contentName)
    }

    private fun trackError(
        errorCode: String,
        errorMessage: String,
    ) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER, errorCode, errorMessage)
        }
    }
}
