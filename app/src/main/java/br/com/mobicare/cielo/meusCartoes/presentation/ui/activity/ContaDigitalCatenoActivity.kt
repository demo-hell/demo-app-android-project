package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.MarginPageTransformer
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.adapter.ContaDigitalCatenoPageTypeAdapter
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_conta_digital_cateno.*
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import org.jetbrains.anko.startActivity

class ContaDigitalCatenoActivity: BaseLoggedActivity() {

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conta_digital_cateno)
        setupToolbar(toolbar as Toolbar, getString(R.string.txt_name_bank))
        init()
        configureTabs()
    }

    private fun init(){
        btnHelp.setOnClickListener {
            startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_CONTA_DIGITAL)
        }
    }
    private fun configureTabs() {
        viewPager.adapter = ContaDigitalCatenoPageTypeAdapter(this)
        viewPager.setPageTransformer(
            MarginPageTransformer(
                Utils
                    .convertDpToPixel(resources.getDimension(R.dimen.dimen_8dp)).toInt()
            )
        )
        viewPager.offscreenPageLimit = 1
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> }.attach()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_common_filter_faq, this.menu)
        this.menu?.findItem(R.id.action_filter)?.isVisible = false
        this.menu?.findItem(R.id.action_help_extrato)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help_extrato -> {
                startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_CONTA_DIGITAL)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun startHelpCenter(tagKey: String) {
        startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to tagKey,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.txt_name_bank),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true)
    }
}