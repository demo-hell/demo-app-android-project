package br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.extrato.presentation.ui.fragments.ExtratoFragment
import kotlinx.android.synthetic.main.activity_minhas_vendas_home.*


class MinhasVendasHomeActivity : BaseActivity(), BaseActivity.OnBackButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_vendas_home)

        val ecToolbarSub = UserPreferences.getInstance().userInformation?.activeMerchant?.id ?: ""
        setupToolbar(toolbarMySales as Toolbar, resources.getString(R.string.menu_minhas_vendas),
                toolbarSubtitle = getString(R.string.ec_value_x, ecToolbarSub))
        super.onBackButtonListener = this


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainframe, ExtratoFragment())
        transaction.commitAllowingStateLoss()
    }


    override fun onBackTouched() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPICON),
            action = listOf(Action.HOME_MINHAS_VENDAS),
            label = listOf(String.format(Label.VOLTAR_PARA, "Inicio"))
        )
        finish()
    }
}