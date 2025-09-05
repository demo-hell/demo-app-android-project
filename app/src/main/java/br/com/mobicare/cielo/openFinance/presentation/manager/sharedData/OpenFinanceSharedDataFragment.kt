package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceDescSharedDataBinding
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceTabsSharedDataBinding
import br.com.mobicare.cielo.databinding.OpenFinanceSharedDataFragmentBinding
import br.com.mobicare.cielo.openFinance.presentation.manager.adapter.OpenFinancePageTypeAdapter
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.isSharedDataFragmentActive
import com.google.android.material.tabs.TabLayoutMediator

class OpenFinanceSharedDataFragment : Fragment(), CieloNavigationListener {
    private var binding: OpenFinanceSharedDataFragmentBinding? = null
    private var bindingTabs: LayoutOpenFinanceTabsSharedDataBinding? = null
    private var bindingDesc: LayoutOpenFinanceDescSharedDataBinding? = null
    private var navigation: CieloNavigation? = null
    private val args: OpenFinanceSharedDataFragmentArgs by navArgs()

    private val toolbarDefault
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.data_share_opf),
                showBackButton = true,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {}
                ),
            ),
            floatingTopSectionView = CieloCollapsingToolbarLayout.FloatingTopSectionView(
                fixedContentView = bindingTabs?.root,
                collapsableContentView = bindingDesc?.root
            )
        )

    private val toolbarFlowConclusion
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.data_share_opf),
                showBackButton = true,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {}
                ),
                onBackPressed = { requireActivity().finish() }
            ),
            floatingTopSectionView = CieloCollapsingToolbarLayout.FloatingTopSectionView(
                fixedContentView = bindingTabs?.root,
                collapsableContentView = bindingDesc?.root
            )
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingTabs = LayoutOpenFinanceTabsSharedDataBinding.inflate(
            inflater, container, false
        )
        bindingDesc = LayoutOpenFinanceDescSharedDataBinding.inflate(
            inflater, container, false
        )
        return OpenFinanceSharedDataFragmentBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        setupViewPager()
        isSharedDataFragmentActive = true
    }

    private fun setupViewPager() {
        val titlesArray = resources.getStringArray(R.array.titles_page_open_finance)
        val adapterPage = OpenFinancePageTypeAdapter(childFragmentManager, lifecycle, titlesArray)
        binding?.viewPagerOpenFinance?.apply {
            adapter = adapterPage
            bindingTabs?.tabLayout?.let {
                TabLayoutMediator(it, this) { tab, position ->
                    tab.text = titlesArray[position]
                }.attach()
            }
            currentItem = ZERO
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun configureToolbar() {
        if (args?.stringFlowConclusion) {
            navigation?.configureCollapsingToolbar(toolbarFlowConclusion)
        } else {
            navigation?.configureCollapsingToolbar(toolbarDefault)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        bindingTabs = null
        bindingDesc = null
        isSharedDataFragmentActive = false
    }
}