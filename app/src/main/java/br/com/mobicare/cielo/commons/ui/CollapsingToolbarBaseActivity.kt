package br.com.mobicare.cielo.commons.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.utils.afterLayoutConfiguration
import br.com.mobicare.cielo.databinding.ActivityCollapsingToolbarBaseBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import com.google.android.material.appbar.AppBarLayout

@Deprecated("Utilize o componente CieloCollapsingToolbarLayout")
abstract class CollapsingToolbarBaseActivity : BaseLoggedActivity() {

    private var _binding: ActivityCollapsingToolbarBaseBinding? = null
    private val binding get() = _binding!!

    protected val buttonFooterAction get() = binding.btnAction

    private var _toolbarMenu: ToolbarMenu? = null
    private var isHeightRefreshed = false
    private var collapsingContentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCollapsingToolbarBaseBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        disableCollapsingToolbarDrag()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        _toolbarMenu?.let {
            menuInflater.inflate(it.menuRes, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        _toolbarMenu?.onOptionsItemSelected?.invoke(item)
        return super.onOptionsItemSelected(item)
    }

    protected fun setCollapsingToolbarContentView(view: View, showAppBarLayout: Boolean? = null) {
        collapsingContentView = view
        showAppBarLayout?.let { showAppBarLayout(it) }
    }

    protected fun updateConfiguration(configurator: Configurator) {
        with(configurator) {
            showAppBarLayout(show)
            setCollapsingToolbarExpanded(isExpanded)
            disableCollapsingToolbarExpandableMode(disableExpandableMode)
            showCollapsingToolbarBackButton(showBackButton)
            setCollapsingToolbarTitle(toolbarTitle)
            updateOptionsMenu(toolbarMenu)
            setCollapsingToolbarTitleAppearance(toolbarTitleAppearance)
            setFooterView(footerView)
        }
    }

    private fun updateOptionsMenu(toolbarMenu: ToolbarMenu?) {
        _toolbarMenu = toolbarMenu
        invalidateOptionsMenu()
    }

    protected fun showAppBarLayout(value: Boolean) {
        binding.apply {
            coordinatorLayout.visible(value)
            flContentExpanded.visible(value.not())

            nestedScrollView.removeAllViews()
            flContentExpanded.removeAllViews()

            collapsingContentView?.let {
                if (value) nestedScrollView.addView(it)
                else flContentExpanded.addView(it)
            }
        }
    }

    private fun showCollapsingToolbarBackButton(value: Boolean) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(value)
            setDisplayShowHomeEnabled(value)
        }
    }

    private fun setCollapsingToolbarTitle(text: String) {
        binding.collapsingToolbarLayout.apply {
            title = text
            afterLayoutConfiguration {
                if (isHeightRefreshed)
                    refreshCollapsingToolbarHeight()
                else
                    postDelayed({ refreshCollapsingToolbarHeight() }, ONE_HUNDRED.toLong())
            }
        }
    }

    private fun setCollapsingToolbarTitleAppearance(toolbarTitleAppearance: ToolbarTitleAppearance?) {
        toolbarTitleAppearance?.apply {
            binding.collapsingToolbarLayout.apply {
                collapsed?.let { setCollapsedTitleTextAppearance(it) }
                expanded?.let { setExpandedTitleTextAppearance(it) }
            }
        }
    }

    private fun setCollapsingToolbarExpanded(value: Boolean) {
        binding.appBarLayout.setExpanded(value)
    }

    private fun disableCollapsingToolbarExpandableMode(value: Boolean) {
        binding.apply {
            setCollapsingToolbarExpanded(value.not())
            nestedScrollView.isNestedScrollingEnabled = value.not()
        }
    }

    private fun refreshCollapsingToolbarHeight() {
        binding.collapsingToolbarLayout.apply {
            layoutParams.let { clonedLayoutParams ->
                clonedLayoutParams.height =
                    resources.getDimensionPixelSize(
                        if (lineCount <= ONE) R.dimen.dimen_96dp else R.dimen.dimen_120dp
                    )
                layoutParams = clonedLayoutParams
            }
        }
        isHeightRefreshed = true
    }

    private fun disableCollapsingToolbarDrag() {
        val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null) params.behavior = AppBarLayout.Behavior()
        (params.behavior as AppBarLayout.Behavior).apply {
            setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })
        }
    }

    private fun setFooterView(view: View?) {
        binding.containerFooter.apply {
            view?.let {
                addView(it)
                visible()
            } ?: gone()
        }
    }

    protected fun showFooterView(show: Boolean) {
        binding.containerFooter.visible(show)
    }

    data class Configurator(
        val show: Boolean = true,
        val isExpanded: Boolean = true,
        val disableExpandableMode: Boolean = false,
        val toolbarMenu: ToolbarMenu? = null,
        val toolbarTitle: String = ONE_SPACE,
        val showBackButton: Boolean = true,
        val toolbarTitleAppearance: ToolbarTitleAppearance? = null,
        val footerView: View? = null
    )

    data class ToolbarTitleAppearance(
        @StyleRes val collapsed: Int? = null,
        @StyleRes val expanded: Int? = null,
    )

    data class ToolbarMenu(
        @MenuRes val menuRes: Int,
        val onOptionsItemSelected: (MenuItem) -> Unit
    )

}