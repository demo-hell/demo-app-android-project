package br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.fragment.UserCanceledSellsFragment
import kotlinx.android.synthetic.main.activity_minhas_vendas_canceladas.*

class MinhasVendasCanceladasActivity : BaseActivity(), BaseActivity.OnBackButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_vendas_canceladas)

        setupToolbar(toolbarMySalesCanceled as Toolbar,
            getString(R.string.text_canceled_sells_label)
        )

        //TODO criar um fragment do zero para vendas canceladas
        UserCanceledSellsFragment().addInFrame(supportFragmentManager,
            R.id.frameCanceledSales
        )

    }

    override fun onBackTouched() {
        finish()
    }
}