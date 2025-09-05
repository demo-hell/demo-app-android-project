package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.GridLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.MenuLabels.TECHNICAL_SUPPORT
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters.HelpCategoryAdapterViewHolderCallback
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.adapters.HelpSuportTecnicoAdapterViewHolderCallback
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.databinding.FragmentTechnicalSupportNewBinding
import br.com.mobicare.cielo.suporteTecnico.TechnicalSupportContract
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.suporteTecnico.ui.activity.TechnicalSupportProblemActivity
import br.com.mobicare.cielo.suporteTecnico.ui.presenter.TechnicalSupportPresenter
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Keep
class NewTechnicalSupportFragment : BaseFragment(), TechnicalSupportContract.View {

    private var binding: FragmentTechnicalSupportNewBinding? = null

    private var logoutListener: LogoutListener? = null
    private var showToolbar: Boolean = false
    private val infoMenuService: String?
        get() = arguments?.getString(TECHNICAL_SUPPORT_SERVICE)
    private val technicalSupportPresenter: TechnicalSupportPresenter by inject {
        parametersOf(this)
    }

    companion object {
        const val TECHNICAL_SUPPORT_SERVICE = "supporte_tecnico_servico"
        fun create(logoutListener: LogoutListener?, showToolbar: Boolean) =
            NewTechnicalSupportFragment().apply {
                this.logoutListener = logoutListener
                this.showToolbar = showToolbar
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTechnicalSupportNewBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()
        binding?.toolbarTechnicalSupportProblem?.apply {
            toolbarMain.visible(showToolbar)
        }
        technicalSupportPresenter.loadItems()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun loadTechnicalSupportItems(support: List<SupportItem>) {
        if (isAttached()) {
            binding?.apply {
                recyclerTechnicalSupportNewItems.layoutManager =
                    GridLayoutManager(context, if (infoMenuService != null) THREE else TWO)
                recyclerTechnicalSupportNewItems.setHasFixedSize(true)

                val adapter = DefaultViewListAdapter(
                    support.map {
                        HelpCategory(it.categoryName, it.categoryName, it.imageUrl, it.id)
                    },
                    if (infoMenuService != null) {
                        R.layout.item_home_shortcut_suporte_tecnico
                    } else {
                        R.layout.item_new_technical_support
                    }
                )

                adapter.setBindViewHolderCallback(
                    if (infoMenuService != null) {
                        HelpSuportTecnicoAdapterViewHolderCallback().apply {
                            onCategoryItemClickListener = object :
                                HelpSuportTecnicoAdapterViewHolderCallback.OnCategoryItemClickListener {
                                override fun onClick(
                                    helpCategorySelected: HelpCategory,
                                    position: Int
                                ) {
                                    trackSelectContent(helpCategorySelected.description ?: EMPTY)
                                    setupStartFragment(support[position])
                                }
                            }
                        }
                    } else {
                        HelpCategoryAdapterViewHolderCallback().apply {
                            onCategoryItemClickListener = object :
                                HelpCategoryAdapterViewHolderCallback.OnCategoryItemClickListener {
                                override fun onClick(
                                    helpCategorySelected: HelpCategory,
                                    position: Int
                                ) {
                                    trackSelectContent(helpCategorySelected.description ?: EMPTY)
                                    setupStartFragment(support[position])
                                }
                            }
                        }
                    })

                recyclerTechnicalSupportNewItems.adapter = adapter
            }
        }
    }

    private fun setupStartFragment(supportItem: SupportItem) {
        requireActivity().startActivity<TechnicalSupportProblemActivity>(
            TechnicalSupportProblemActivity.TECHNICAL_SUPPORT_ITEM_KEY
                    to supportItem
        )
    }

    override fun systemError(error: ErrorMessage) = handleError(error)

    override fun userError(error: ErrorMessage) = handleError(error)

    override fun showLoading() {
        if (isAttached()) {
            binding?.apply {
                recyclerTechnicalSupportNewItems.gone()
                errorTechnicalItems.root.gone()
                frameTechnicalItemsProgress.root.visible()
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            binding?.apply {
                frameTechnicalItemsProgress.root.gone()
                errorTechnicalItems.root.gone()
                recyclerTechnicalSupportNewItems.visible()
            }
        }
    }

    private fun handleError(error: ErrorMessage) {
        if (isAttached()) {
            trackError(
                errorCode = error.code,
                errorMessage = error.message,
            )
            binding?.apply {
                recyclerTechnicalSupportNewItems.gone()
                frameTechnicalItemsProgress.root.gone()
                errorTechnicalItems.apply {
                    val errorMessage = error.errorMessage
                    textViewErrorMsg.text = if (errorMessage.isBlank().not()) {
                        errorMessage
                    } else {
                        getString(R.string.text_technical_suppport_error)
                    }

                    buttonErrorTry.setOnClickListener {
                        technicalSupportPresenter.loadItems()
                    }
                    containerError.visible()
                    root.visible()
                }
            }
        }
    }

    private fun logScreenView() {
        GA4.logScreenView(GA4.ScreenView.SERVICES_TECHNICAL_SUPPORT)
        GA4.logScreenView(GA4.ScreenView.TECHNICAL_SUPPORT_HELP_CENTER)
    }

    private fun trackSelectContent(contentName: String) {
        GA4.logSelectContent(
            HELP_CENTER,
            TECHNICAL_SUPPORT,
            contentName
        )
        GA4.logClick(GA4.ScreenView.TECHNICAL_SUPPORT_HELP_CENTER, contentName)
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER, errorCode, errorMessage)
        }
    }
}