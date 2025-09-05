package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.SuppliesEngineActivity
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.LISTSUPPLIES
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.TAGNAME
import br.com.mobicare.cielo.coil.presentation.activity.CoilEngineActivity
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.cielo.libflue.util.THREE
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.constants.Text.CAROUSEL
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.ItemHomeShortcutBinding
import br.com.mobicare.cielo.databinding.SelfServiceSupplyFragmentBinding
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_COIL
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_STICKER
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import br.com.mobicare.cielo.recebaMais.GA_RM_SOLICITAR_MATERIAIS_SHEET_SCREEN
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Keep
class SelfServiceSupplyFragment : BaseFragment(), SelfServiceSupplyContract.View {

    private var binding: SelfServiceSupplyFragmentBinding? = null

    private val presenter: SelfServiceSupplyPresenter by inject {
        parametersOf(this)
    }

    private val ga4: SelfServiceAnalytics by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SelfServiceSupplyFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        presenter.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun configureRecyclerView() {
        binding?.rvMaterials?.apply {
            layoutManager = GridLayoutManager(requireContext(), THREE)
            setHasFixedSize(true)
        }
    }

    override fun show(supplies: List<ItemBannerMigration>) {
        binding?.apply {
            rvMaterials.visible()

            val adapterMaterials = DefaultViewListAdapter(supplies, R.layout.item_home_shortcut)
            adapterMaterials.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<ItemBannerMigration> {
                override fun onBind(item: ItemBannerMigration, holder: DefaultViewHolderKotlin) {
                    ItemHomeShortcutBinding.bind(holder.mView).apply {
                        textHeaderLabel.text = item.firstName
                        imageHeaderButton.setImageDrawable(item.imageUrl)
                    }
                }
            })

            adapterMaterials.onItemClickListener =
                object : DefaultViewListAdapter.OnItemClickListener<ItemBannerMigration> {
                    override fun onItemClick(item: ItemBannerMigration) {
                        gaSendCardSupplies(item.firstName)
                        logSelectContentMaterial(item.firstName)
                        presenter.selectItem(item)
                    }
                }

            rvMaterials.adapter = adapterMaterials
        }
    }

    override fun showError(error: ErrorMessage?) {
        hideLoading()
        binding?.apply {
            rvMaterials.gone()
            errorLayout.visible()
            errorLayout.cieloErrorMessage = getString(R.string.text_message_generic_error)
            errorLayout.configureButtonLabel(getString(R.string.text_try_again_label))
            errorLayout.cieloErrorTitle = getString(R.string.text_title_generic_error)
            errorLayout.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            errorLayout.configureActionClickListener {
                presenter.load()
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun openCoinEngine(tagName: String, supplies: List<SupplyDTO>) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.SOLICITAR_MATERIAIS),
                action = listOf(tagName.toLowerCasePTBR()),
                label = listOf(Label.CARD, tagName.toLowerCasePTBR())
            )
            this.requireActivity().startActivityForResult<CoilEngineActivity>(REQUEST_CODE_COIL,
                LISTSUPPLIES to supplies,
                TAGNAME to tagName)
        }
    }

    override fun openSuppliesEngine(tagName: String, supplies: List<SupplyDTO>) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, AUTOATENDIMENTO),
            action = listOf(CAROUSEL),
            label = listOf(tagName.toLowerCasePTBR())
        )

        this.requireActivity().startActivityForResult<SuppliesEngineActivity>(
            REQUEST_CODE_STICKER,
            LISTSUPPLIES to supplies,
            TAGNAME to tagName
        )
    }

    override fun showLoading() {
        binding?.apply {
            rvMaterials.gone()
            errorLayout.gone()
            progress.root.visible()
        }
    }

    override fun hideLoading() {
        binding?.progress?.root?.gone()
    }

    override fun showIneligibleUser(message: String) {
        binding?.apply {
            rvMaterials.gone()
            progress.root.gone()
            errorLayout.visible()
            errorLayout.cieloErrorMessage = message
            errorLayout.configureButtonLabel(getString(R.string.ok))
            errorLayout.cieloErrorTitle = getString(R.string.text_title_service_unavailable)
            errorLayout.errorHandlerCieloViewImageDrawable = R.drawable.img_ineligible_user
            errorLayout.configureActionClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun gaSendCardSupplies(status: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.SOLICITAR_MATERIAIS),
                action = listOf(Action.SOLICITAR_MATERIAIS),
                label = listOf(Label.CARD, status.toLowerCasePTBR())
            )
        }
    }

    private fun logScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_SOLICITAR_MATERIAIS_SHEET_SCREEN,
            screenClass = this.javaClass
        )
        ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS)
    }

    private fun logSelectContentMaterial(material: String) {
        ga4.logSelectContentMaterial(material)
    }

}